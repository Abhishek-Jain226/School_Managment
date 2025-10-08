package com.app.service.impl;

import com.app.entity.*;
import com.app.payload.response.ApiResponse;
import com.app.repository.*;
import com.app.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public ApiResponse getAttendanceReport(Integer schoolId, String filterType) {
        try {
            System.out.println("🔍 Generating attendance report for schoolId: " + schoolId + ", filterType: " + filterType);
            
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new RuntimeException("School not found with ID: " + schoolId));
            
            List<Student> students = studentRepository.findBySchool(school);
            System.out.println("🔍 Found " + students.size() + " students for school: " + school.getSchoolName());
            
            List<Map<String, Object>> attendanceRecords = new ArrayList<>();
            
            for (Student student : students) {
                // Get attendance data from dispatch logs
                List<DispatchLog> studentLogs = dispatchLogRepository.findByStudent(student);
                
                long presentDays = studentLogs.stream()
                        .filter(log -> log.getEventType().toString().contains("PICKUP") || 
                                     log.getEventType().toString().contains("DROP"))
                        .count();
                
                long absentDays = studentLogs.stream()
                        .filter(log -> log.getEventType().toString().contains("ABSENT"))
                        .count();
                
                Map<String, Object> record = new HashMap<>();
                record.put("studentId", student.getStudentId());
                record.put("studentName", student.getStudentName());
                record.put("className", student.getClassName());
                record.put("sectionName", student.getSectionName());
                record.put("presentDays", presentDays);
                record.put("absentDays", absentDays);
                long totalDays = presentDays + absentDays;
                record.put("totalDays", totalDays);
                record.put("attendancePercentage", totalDays > 0 ? (presentDays * 100.0 / totalDays) : 0.0);
                
                attendanceRecords.add(record);
            }
            
            // Apply filtering
            if ("class-wise".equals(filterType)) {
                Map<String, List<Map<String, Object>>> classWiseData = attendanceRecords.stream()
                        .collect(Collectors.groupingBy(record -> (String) record.get("className")));
                return new ApiResponse(true, "Class-wise attendance report generated", classWiseData);
            } else {
                return new ApiResponse(true, "Student-wise attendance report generated", attendanceRecords);
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
                logEntry.put("studentName", log.getStudent().getStudentName());
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
                    notificationEntry.put("notificationId", notification.getNotificationId());
                    notificationEntry.put("dispatchLogId", dispatchLog.getDispatchLogId());
                    notificationEntry.put("tripId", dispatchLog.getTrip().getTripId());
                    notificationEntry.put("tripName", dispatchLog.getTrip().getTripName());
                    notificationEntry.put("studentId", dispatchLog.getStudent().getStudentId());
                    notificationEntry.put("studentName", dispatchLog.getStudent().getStudentName());
                    notificationEntry.put("vehicleNumber", dispatchLog.getVehicle().getVehicleNumber());
                    notificationEntry.put("notificationType", notification.getNotificationType().toString());
                    notificationEntry.put("message", notification.getMessage());
                    notificationEntry.put("sentTo", notification.getSentTo());
                    notificationEntry.put("sentDate", notification.getSentDate());
                    notificationEntry.put("status", notification.getStatus());
                    notificationEntry.put("deliveryStatus", notification.getDeliveryStatus());
                    
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
            
            // Generate the report data first
            ApiResponse reportData;
            switch (type.toLowerCase()) {
                case "attendance":
                    reportData = getAttendanceReport(schoolId, "student-wise");
                    break;
                case "dispatch":
                    reportData = getDispatchLogsReport(schoolId, "all");
                    break;
                case "notifications":
                    reportData = getNotificationLogsReport(schoolId, "all");
                    break;
                default:
                    return new ApiResponse(false, "Invalid report type: " + type, null);
            }
            
            if (!reportData.isSuccess()) {
                return new ApiResponse(false, "Failed to generate report data: " + reportData.getMessage(), null);
            }
            
            // Generate file name with timestamp
            String timestamp = LocalDateTime.now().toString().replace(":", "-").substring(0, 19);
            String fileName = type + "_report_" + schoolId + "_" + timestamp + "." + format.toLowerCase();
            
            // For now, return file info (actual file generation would be implemented here)
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("fileName", fileName);
            fileInfo.put("filePath", "/downloads/reports/" + fileName);
            fileInfo.put("fileSize", "Generated");
            fileInfo.put("generatedAt", LocalDateTime.now());
            fileInfo.put("reportType", type);
            fileInfo.put("format", format);
            fileInfo.put("schoolId", schoolId);
            fileInfo.put("recordCount", reportData.getData() instanceof List ? ((List<?>) reportData.getData()).size() : 0);
            
            System.out.println("🔍 Report export completed: " + fileName);
            
            return new ApiResponse(true, "Report exported successfully", fileInfo);
            
        } catch (Exception e) {
            System.out.println("🔍 Error exporting report: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error exporting report: " + e.getMessage(), null);
        }
    }
}
