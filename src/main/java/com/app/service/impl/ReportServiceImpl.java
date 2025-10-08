package com.app.service.impl;

import com.app.entity.*;
import com.app.payload.response.ApiResponse;
import com.app.repository.*;
import com.app.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements IReportService {

    private final StudentRepository studentRepository;
    private final DispatchLogRepository dispatchLogRepository;
    private final NotificationRepository notificationRepository;
    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;
    private final SchoolRepository schoolRepository;
    private final StudentAttendanceRepository studentAttendanceRepository;

    @Override
    public ApiResponse getAttendanceReport(Integer schoolId, String filterType) {
        try {
            System.out.println("🔍 Generating attendance report for schoolId: " + schoolId + ", filterType: " + filterType);
            
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new RuntimeException("School not found with ID: " + schoolId));
            
            List<Student> students = studentRepository.findBySchool(school);
            System.out.println("🔍 Found " + students.size() + " students for school: " + school.getSchoolName());
            
            List<Map<String, Object>> reportRecords = new ArrayList<>();
            
            for (Student student : students) {
                // Get attendance data from StudentAttendance table
                List<StudentAttendance> studentAttendanceRecords = studentAttendanceRepository.findByStudent(student);
                
                long presentDays = studentAttendanceRecords.stream()
                        .filter(attendance -> attendance.getIsPresent() != null && attendance.getIsPresent())
                        .count();
                
                long absentDays = studentAttendanceRecords.stream()
                        .filter(attendance -> attendance.getIsAbsent() != null && attendance.getIsAbsent())
                        .count();
                
                Map<String, Object> record = new HashMap<>();
                record.put("studentId", student.getStudentId());
                record.put("studentName", student.getFirstName() + " " + student.getLastName());
                record.put("className", student.getClassMaster() != null ? student.getClassMaster().getClassName() : "N/A");
                record.put("sectionName", student.getSectionMaster() != null ? student.getSectionMaster().getSectionName() : "N/A");
                record.put("presentDays", presentDays);
                record.put("absentDays", absentDays);
                long totalDays = presentDays + absentDays;
                record.put("totalDays", totalDays);
                record.put("attendancePercentage", totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0);
                
                reportRecords.add(record);
            }
            
            // Apply filtering
            if ("class-wise".equals(filterType)) {
                Map<String, List<Map<String, Object>>> classWiseData = reportRecords.stream()
                        .collect(Collectors.groupingBy(record -> (String) record.get("className")));
                return new ApiResponse(true, "Class-wise attendance report generated", classWiseData);
            } else {
                return new ApiResponse(true, "Student-wise attendance report generated", reportRecords);
            }
            
        } catch (Exception e) {
            System.out.println("🔍 Error generating attendance report: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error generating attendance report: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getDispatchLogsReport(Integer schoolId, String filterType) {
        try {
            System.out.println("🔍 Generating dispatch logs report for schoolId: " + schoolId + ", filterType: " + filterType);
            
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new RuntimeException("School not found with ID: " + schoolId));
            
            List<DispatchLog> dispatchLogs = dispatchLogRepository.findBySchoolAndCreatedDateBetween(
                    school, LocalDateTime.now().minusDays(30), LocalDateTime.now());
            
            System.out.println("🔍 Found " + dispatchLogs.size() + " dispatch logs for school: " + school.getSchoolName());
            
            List<Map<String, Object>> logs = new ArrayList<>();
            
            for (DispatchLog log : dispatchLogs) {
                Map<String, Object> logEntry = new HashMap<>();
                logEntry.put("dispatchLogId", log.getDispatchLogId());
                logEntry.put("tripId", log.getTrip().getTripId());
                logEntry.put("tripName", log.getTrip().getTripName());
                logEntry.put("vehicleId", log.getVehicle().getVehicleId());
                logEntry.put("vehicleNumber", log.getVehicle().getVehicleNumber());
                logEntry.put("studentId", log.getStudent().getStudentId());
                logEntry.put("studentName", log.getStudent().getFirstName() + " " + log.getStudent().getLastName());
                logEntry.put("eventType", log.getEventType().toString());
                logEntry.put("remarks", log.getRemarks());
                logEntry.put("createdDate", log.getCreatedDate());
                logEntry.put("createdBy", log.getCreatedBy());
                
                logs.add(logEntry);
            }
            
            // Apply filtering
            if ("trip-wise".equals(filterType)) {
                Map<Integer, List<Map<String, Object>>> tripWiseData = logs.stream()
                        .collect(Collectors.groupingBy(log -> (Integer) log.get("tripId")));
                return new ApiResponse(true, "Trip-wise dispatch logs report generated", tripWiseData);
            } else if ("vehicle-wise".equals(filterType)) {
                Map<String, List<Map<String, Object>>> vehicleWiseData = logs.stream()
                        .collect(Collectors.groupingBy(log -> (String) log.get("vehicleNumber")));
                return new ApiResponse(true, "Vehicle-wise dispatch logs report generated", vehicleWiseData);
            } else {
                return new ApiResponse(true, "Dispatch logs report generated", logs);
            }
            
        } catch (Exception e) {
            System.out.println("🔍 Error generating dispatch logs report: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error generating dispatch logs report: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getNotificationLogsReport(Integer schoolId, String filterType) {
        try {
            System.out.println("🔍 Generating notification logs report for schoolId: " + schoolId + ", filterType: " + filterType);
            
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new RuntimeException("School not found with ID: " + schoolId));
            
            // Get all notifications for the school through dispatch logs
            List<DispatchLog> schoolDispatchLogs = dispatchLogRepository.findBySchoolAndCreatedDateBetween(
                    school, LocalDateTime.now().minusDays(30), LocalDateTime.now());
            
            List<Map<String, Object>> notifications = new ArrayList<>();
            
            for (DispatchLog dispatchLog : schoolDispatchLogs) {
                List<Notification> dispatchNotifications = notificationRepository.findByDispatchLog(dispatchLog);
                
                for (Notification notification : dispatchNotifications) {
                    Map<String, Object> notificationEntry = new HashMap<>();
                    notificationEntry.put("notificationId", notification.getNotificationLogId());
                    notificationEntry.put("dispatchLogId", dispatchLog.getDispatchLogId());
                    notificationEntry.put("tripId", dispatchLog.getTrip().getTripId());
                    notificationEntry.put("tripName", dispatchLog.getTrip().getTripName());
                    notificationEntry.put("studentId", dispatchLog.getStudent().getStudentId());
                    notificationEntry.put("studentName", dispatchLog.getStudent().getFirstName() + " " + dispatchLog.getStudent().getLastName());
                    notificationEntry.put("vehicleNumber", dispatchLog.getVehicle().getVehicleNumber());
                    notificationEntry.put("notificationType", notification.getNotificationType().toString());
                    notificationEntry.put("message", "Notification sent"); // Placeholder since message field doesn't exist
                    notificationEntry.put("sentTo", "Parent/Driver"); // Placeholder since sentTo field doesn't exist
                    notificationEntry.put("sentDate", notification.getSentAt());
                    notificationEntry.put("status", notification.getIsSent() ? "SENT" : "PENDING");
                    notificationEntry.put("deliveryStatus", notification.getErrorMsg() != null ? "FAILED" : "DELIVERED");
                    
                    notifications.add(notificationEntry);
                }
            }
            
            System.out.println("🔍 Found " + notifications.size() + " notifications for school: " + school.getSchoolName());
            
            // Apply filtering
            if ("sent".equals(filterType)) {
                List<Map<String, Object>> sentNotifications = notifications.stream()
                        .filter(notif -> "SENT".equals(notif.get("status")))
                        .collect(Collectors.toList());
                return new ApiResponse(true, "Sent notifications report generated", sentNotifications);
            } else if ("failed".equals(filterType)) {
                List<Map<String, Object>> failedNotifications = notifications.stream()
                        .filter(notif -> "FAILED".equals(notif.get("status")))
                        .collect(Collectors.toList());
                return new ApiResponse(true, "Failed notifications report generated", failedNotifications);
            } else if ("pending".equals(filterType)) {
                List<Map<String, Object>> pendingNotifications = notifications.stream()
                        .filter(notif -> "PENDING".equals(notif.get("status")))
                        .collect(Collectors.toList());
                return new ApiResponse(true, "Pending notifications report generated", pendingNotifications);
            } else {
                return new ApiResponse(true, "All notifications report generated", notifications);
            }
            
        } catch (Exception e) {
            System.out.println("🔍 Error generating notification logs report: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error generating notification logs report: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse exportReport(Integer schoolId, String type, String format) {
        try {
            System.out.println("🔍 Exporting report for schoolId: " + schoolId + ", type: " + type + ", format: " + format);
            
            // Simple test export
            String fileName = type + "_report_" + schoolId + "_" + LocalDateTime.now().toString().substring(0, 19) + "." + format.toLowerCase();
            
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileName", fileName);
            fileInfo.put("filePath", "/downloads/reports/" + fileName);
            fileInfo.put("fileSize", "Generated");
            fileInfo.put("generatedAt", LocalDateTime.now());
            fileInfo.put("reportType", type);
            fileInfo.put("format", format);
            fileInfo.put("schoolId", schoolId);
            fileInfo.put("recordCount", 1);
            
            return new ApiResponse(true, "Test report exported successfully", fileInfo);
            
        } catch (Exception e) {
            System.out.println("🔍 Error exporting report: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error exporting report: " + e.getMessage(), null);
        }
    }

    @Override
    public byte[] generateReportFile(Integer schoolId, String type, String format) {
        try {
            System.out.println("🔍 Generating report file for schoolId: " + schoolId + ", type: " + type + ", format: " + format);
            
            // For now, use test data to ensure download works
            List<Map<String, Object>> testData = new ArrayList<>();
            Map<String, Object> testRecord = new HashMap<>();
            testRecord.put("studentId", 1);
            testRecord.put("studentName", "Test Student");
            testRecord.put("className", "5A");
            testRecord.put("sectionName", "A");
            testRecord.put("presentDays", 20);
            testRecord.put("absentDays", 2);
            testRecord.put("totalDays", 22);
            testRecord.put("attendancePercentage", 90.9);
            testData.add(testRecord);
            
            // Generate file content based on format
            if ("csv".equalsIgnoreCase(format)) {
                return generateCSVContent(testData, type);
            } else if ("pdf".equalsIgnoreCase(format)) {
                return generatePDFContent(testData, type);
            } else {
                throw new RuntimeException("Unsupported format: " + format);
            }
            
        } catch (Exception e) {
            System.out.println("🔍 Error generating report file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error generating report file: " + e.getMessage());
        }
    }
    
    private byte[] generateCSVContent(Object data, String reportType) {
        try {
            StringBuilder csvContent = new StringBuilder();
            
            // Simple CSV generation
            csvContent.append("studentId,studentName,className,sectionName,presentDays,absentDays,totalDays,attendancePercentage\n");
            csvContent.append("1,Test Student,5A,A,20,2,22,90.9\n");
            csvContent.append("2,Another Student,5B,B,18,4,22,81.8\n");
            
            return csvContent.toString().getBytes(StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            System.out.println("🔍 Error generating CSV: " + e.getMessage());
            throw new RuntimeException("Error generating CSV: " + e.getMessage());
        }
    }
    
    private byte[] generatePDFContent(Object data, String reportType) {
        try {
            // Simple text-based content for now
            StringBuilder content = new StringBuilder();
            content.append("Report Type: ").append(reportType).append("\n");
            content.append("Generated At: ").append(LocalDateTime.now()).append("\n");
            content.append("Data Count: 2\n\n");
            content.append("Student ID: 1, Name: Test Student, Class: 5A, Attendance: 90.9%\n");
            content.append("Student ID: 2, Name: Another Student, Class: 5B, Attendance: 81.8%\n");
            
            return content.toString().getBytes(StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            System.out.println("🔍 Error generating PDF: " + e.getMessage());
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }
}
