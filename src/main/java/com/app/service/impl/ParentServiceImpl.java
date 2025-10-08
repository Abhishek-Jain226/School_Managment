package com.app.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.DispatchLog;
import com.app.entity.Notification;
import com.app.entity.Student;
import com.app.entity.StudentAttendance;
import com.app.entity.StudentParent;
import com.app.entity.Trip;
import com.app.entity.User;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.StudentParentRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.AttendanceHistoryResponseDto;
import com.app.payload.response.MonthlyReportResponseDto;
import com.app.payload.response.ParentDashboardResponseDto;
import com.app.payload.response.ParentNotificationResponseDto;
import com.app.payload.response.StudentResponseDto;
import com.app.repository.DispatchLogRepository;
import com.app.repository.NotificationRepository;
import com.app.repository.StudentAttendanceRepository;
import com.app.repository.StudentParentRepository;
import com.app.repository.StudentRepository;
import com.app.repository.TripRepository;
import com.app.repository.UserRepository;
import com.app.service.IParentService;

@Service
@Transactional
public class ParentServiceImpl implements IParentService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
    private StudentRepository studentRepository;
	@Autowired
    private StudentParentRepository studentParentRepository;
	@Autowired
    private StudentAttendanceRepository studentAttendanceRepository;
	@Autowired
    private NotificationRepository notificationRepository;
	@Autowired
    private DispatchLogRepository dispatchLogRepository;
	@Autowired
    private TripRepository tripRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse createParent(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already exists", null);
        }

        User parent = User.builder()
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword())) 
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        User savedParent = userRepository.save(parent);

        return new ApiResponse(true, "Parent created successfully", savedParent);
    }

    @Override
    public ApiResponse linkParentToStudent(StudentParentRequestDto request) {
        User parent = userRepository.findById(request.getParentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + request.getParentUserId()));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));

        StudentParent link = StudentParent.builder()
                .student(student)
                .parentUser(parent)
                .relation(request.getRelation())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        studentParentRepository.save(link);

        return new ApiResponse(true, "Parent linked to student successfully", null);
    }

    @Override
    public ApiResponse updateParent(Integer parentId, UserRequestDto request) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + parentId));

        parent.setUserName(request.getUserName());
        parent.setPassword(passwordEncoder.encode(request.getPassword()));
        parent.setContactNumber(request.getContactNumber());
        parent.setUpdatedBy(request.getUpdatedBy());
        parent.setUpdatedDate(LocalDateTime.now());

        User updatedParent = userRepository.save(parent);

        return new ApiResponse(true, "Parent updated successfully", updatedParent);
    }

    @Override
    public ApiResponse getParentById(Integer parentId) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + parentId));

        return new ApiResponse(true, "Parent fetched successfully", parent);
    }

    @Override
    public ApiResponse getAllParents(Integer schoolId) {
        List<User> parents = studentParentRepository.findByStudent_School_SchoolId(schoolId).stream()
                .map(StudentParent::getParentUser)
                .distinct()
                .collect(Collectors.toList());

        return new ApiResponse(true, "Parents fetched successfully", parents);
    }
    @Override
    public ApiResponse getParentByUserId(Integer userId) {
        StudentParent sp = studentParentRepository.findByParentUser_uId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Parent not found for userId: " + userId));

        Map<String, Object> resp = Map.of(
            "studentParentId", sp.getStudentParentId(),
            "relation", sp.getRelation(),
            "parentUserId", sp.getParentUser().getUId(),
            "parentName", sp.getParentUser().getUserName(),
            "email", sp.getParentUser().getEmail(),
            "contactNumber", sp.getParentUser().getContactNumber(),
            "studentName", sp.getStudent().getFirstName() + " " + sp.getStudent().getLastName(),
            "className", sp.getStudent().getClassMaster(),
            "section", sp.getStudent().getSectionMaster()
        );

        return new ApiResponse(true, "Parent fetched successfully", resp);
    }
    
    @Override
    public ApiResponse getStudentByParentUserId(Integer userId) {
        StudentParent sp = studentParentRepository.findByParentUser_uId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("No student linked with this parent"));

        // ✅ सिर्फ Student return करेंगे
        Student student = sp.getStudent();

        return new ApiResponse(true, "Student fetched successfully", mapToResponse(student));
    }

    // Mapper for StudentResponseDto
    private StudentResponseDto mapToResponse(Student student) {
        return StudentResponseDto.builder()
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .classId(student.getClassMaster() != null ? student.getClassMaster().getClassId() : null)
                .className(student.getClassMaster() != null ? student.getClassMaster().getClassName() : null)
                .sectionId(student.getSectionMaster() != null ? student.getSectionMaster().getSectionId() : null)
                .sectionName(student.getSectionMaster() != null ? student.getSectionMaster().getSectionName() : null)
                .gender(student.getGender())
                .motherName(student.getMotherName())
                .fatherName(student.getFatherName())
                .primaryContactNumber(student.getPrimaryContactNumber())
                .alternateContactNumber(student.getAlternateContactNumber())
                .email(student.getEmail())
                .isActive(student.getIsActive())
                .createdBy(student.getCreatedBy())
                .createdDate(student.getCreatedDate())
                .updatedBy(student.getUpdatedBy())
                .updatedDate(student.getUpdatedDate())
                .build();
    }

    // ================ PARENT DASHBOARD METHODS ================

    @Override
    public ApiResponse getParentDashboard(Integer userId) {
        System.out.println("🔍 getParentDashboard called with userId: " + userId);
        try {
            // Get parent user
            User parentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent user not found with ID: " + userId));

            // Get student linked to this parent
            StudentParent studentParent = studentParentRepository.findByParentUser_uId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("No student linked to this parent"));

            Student student = studentParent.getStudent();
            System.out.println("🔍 Found student: " + student.getFirstName() + " " + student.getLastName());

            // Get today's attendance
            Optional<StudentAttendance> todayAttendance = studentAttendanceRepository
                    .findByStudentAndAttendanceDate(student, LocalDate.now());

            // Get attendance statistics for current month
            LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
            LocalDate monthEnd = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            
            Long presentDays = studentAttendanceRepository.countPresentDaysByStudentAndDateRange(student, monthStart, monthEnd);
            Long absentDays = studentAttendanceRepository.countAbsentDaysByStudentAndDateRange(student, monthStart, monthEnd);
            Long lateDays = studentAttendanceRepository.countLateDaysByStudentAndDateRange(student, monthStart, monthEnd);
            
            Double attendancePercentage = 0.0;
            if (presentDays + absentDays > 0) {
                attendancePercentage = (presentDays.doubleValue() / (presentDays + absentDays)) * 100;
            }

            // Get recent notifications (last 5) - optimized query
            List<Notification> recentNotifications = notificationRepository
                    .findByDispatchLog_Student_StudentIdOrderByCreatedDateDesc(student.getStudentId())
                    .stream()
                    .limit(5)
                    .collect(Collectors.toList());

            // Get recent trips (last 5)
            List<Trip> recentTrips = tripRepository.findBySchool(student.getSchool())
                    .stream()
                    .limit(5)
                    .collect(Collectors.toList());

            // Build dashboard response
            ParentDashboardResponseDto dashboard = ParentDashboardResponseDto.builder()
                    .userId(userId)
                    .userName(parentUser.getUserName())
                    .email(parentUser.getEmail())
                    .contactNumber(parentUser.getContactNumber())
                    .studentId(student.getStudentId())
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .studentPhoto(student.getStudentPhoto())
                    .className(student.getClassMaster() != null ? student.getClassMaster().getClassName() : "N/A")
                    .sectionName(student.getSectionMaster() != null ? student.getSectionMaster().getSectionName() : "N/A")
                    .schoolName(student.getSchool().getSchoolName())
                    .todayAttendanceStatus(todayAttendance.map(att -> {
                        if (att.getIsPresent()) return "Present";
                        if (att.getIsAbsent()) return "Absent";
                        if (att.getIsLate()) return "Late";
                        return "Unknown";
                    }).orElse("Not Marked"))
                    .todayArrivalTime(todayAttendance.map(StudentAttendance::getArrivalTime).orElse(null))
                    .todayDepartureTime(todayAttendance.map(StudentAttendance::getDepartureTime).orElse(null))
                    .totalPresentDays(presentDays)
                    .totalAbsentDays(absentDays)
                    .totalLateDays(lateDays)
                    .attendancePercentage(attendancePercentage)
                    .recentNotifications(recentNotifications.stream().map(this::mapToNotificationDto).collect(Collectors.toList()))
                    .recentTrips(recentTrips.stream().map(this::mapToTripDto).collect(Collectors.toList()))
                    .lastUpdated(LocalDate.now())
                    .build();

            System.out.println("🔍 Parent dashboard created successfully");
            return new ApiResponse(true, "Parent dashboard data retrieved successfully", dashboard);

        } catch (Exception e) {
            System.out.println("🔍 Error in getParentDashboard: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving parent dashboard: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getParentNotifications(Integer userId) {
        System.out.println("🔍 getParentNotifications called with userId: " + userId);
        try {
            // Get parent user
            User parentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent user not found with ID: " + userId));

            // Get student linked to this parent
            StudentParent studentParent = studentParentRepository.findByParentUser_uId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("No student linked to this parent"));

            Student student = studentParent.getStudent();

            // Get all notifications for this student - optimized query
            List<Notification> notifications = notificationRepository
                    .findByDispatchLog_Student_StudentIdOrderByCreatedDateDesc(student.getStudentId());

            List<ParentNotificationResponseDto> notificationDtos = notifications.stream()
                    .map(this::mapToParentNotificationDto)
                    .collect(Collectors.toList());

            System.out.println("🔍 Found " + notificationDtos.size() + " notifications for parent");
            return new ApiResponse(true, "Parent notifications retrieved successfully", notificationDtos);

        } catch (Exception e) {
            System.out.println("🔍 Error in getParentNotifications: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving parent notifications: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getAttendanceHistory(Integer userId, String fromDate, String toDate) {
        System.out.println("🔍 getAttendanceHistory called with userId: " + userId);
        try {
            // Get parent user and student
            User parentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent user not found with ID: " + userId));

            StudentParent studentParent = studentParentRepository.findByParentUser_uId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("No student linked to this parent"));

            Student student = studentParent.getStudent();

            // Parse dates
            LocalDate startDate = fromDate != null ? LocalDate.parse(fromDate) : LocalDate.now().minusDays(30);
            LocalDate endDate = toDate != null ? LocalDate.parse(toDate) : LocalDate.now();

            // Get attendance records
            List<StudentAttendance> attendanceRecords = studentAttendanceRepository
                    .findByStudentAndAttendanceDateBetween(student, startDate, endDate);

            // Calculate statistics
            Long totalDays = (long) attendanceRecords.size();
            Long presentDays = attendanceRecords.stream().mapToLong(att -> att.getIsPresent() ? 1 : 0).sum();
            Long absentDays = attendanceRecords.stream().mapToLong(att -> att.getIsAbsent() ? 1 : 0).sum();
            Long lateDays = attendanceRecords.stream().mapToLong(att -> att.getIsLate() ? 1 : 0).sum();
            
            Double attendancePercentage = 0.0;
            if (totalDays > 0) {
                attendancePercentage = (presentDays.doubleValue() / totalDays) * 100;
            }

            // Map to DTOs
            List<AttendanceHistoryResponseDto.AttendanceRecordDto> recordDtos = attendanceRecords.stream()
                    .map(this::mapToAttendanceRecordDto)
                    .collect(Collectors.toList());

            AttendanceHistoryResponseDto history = AttendanceHistoryResponseDto.builder()
                    .studentId(student.getStudentId())
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .fromDate(startDate)
                    .toDate(endDate)
                    .totalDays(totalDays)
                    .presentDays(presentDays)
                    .absentDays(absentDays)
                    .lateDays(lateDays)
                    .attendancePercentage(attendancePercentage)
                    .attendanceRecords(recordDtos)
                    .build();

            System.out.println("🔍 Attendance history created successfully");
            return new ApiResponse(true, "Attendance history retrieved successfully", history);

        } catch (Exception e) {
            System.out.println("🔍 Error in getAttendanceHistory: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving attendance history: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getMonthlyReport(Integer userId, Integer year, Integer month) {
        System.out.println("🔍 getMonthlyReport called with userId: " + userId);
        try {
            // Get parent user and student
            User parentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent user not found with ID: " + userId));

            StudentParent studentParent = studentParentRepository.findByParentUser_uId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("No student linked to this parent"));

            Student student = studentParent.getStudent();

            // Use current year/month if not provided
            int reportYear = year != null ? year : LocalDate.now().getYear();
            int reportMonth = month != null ? month : LocalDate.now().getMonthValue();

            LocalDate monthStart = LocalDate.of(reportYear, reportMonth, 1);
            LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

            // Get attendance data
            List<StudentAttendance> attendanceRecords = studentAttendanceRepository
                    .findByStudentAndAttendanceDateBetween(student, monthStart, monthEnd);

            // Calculate statistics
            Long totalSchoolDays = (long) attendanceRecords.size();
            Long presentDays = attendanceRecords.stream().mapToLong(att -> att.getIsPresent() ? 1 : 0).sum();
            Long absentDays = attendanceRecords.stream().mapToLong(att -> att.getIsAbsent() ? 1 : 0).sum();
            Long lateDays = attendanceRecords.stream().mapToLong(att -> att.getIsLate() ? 1 : 0).sum();
            
            Double attendancePercentage = 0.0;
            if (totalSchoolDays > 0) {
                attendancePercentage = (presentDays.doubleValue() / totalSchoolDays) * 100;
            }

            // Get trip data
            List<Trip> trips = tripRepository.findBySchool(student.getSchool())
                    .stream()
                    .filter(trip -> trip.getTripStartTime() != null && 
                                   trip.getTripStartTime().toLocalDate().isAfter(monthStart.minusDays(1)) && 
                                   trip.getTripStartTime().toLocalDate().isBefore(monthEnd.plusDays(1)))
                    .collect(Collectors.toList());

            Long totalTrips = (long) trips.size();
            Long completedTrips = trips.stream().mapToLong(trip -> 1).sum(); // Simplified
            Long missedTrips = totalTrips - completedTrips;
            
            Double tripCompletionRate = 0.0;
            if (totalTrips > 0) {
                tripCompletionRate = (completedTrips.doubleValue() / totalTrips) * 100;
            }

            // Build monthly report
            MonthlyReportResponseDto report = MonthlyReportResponseDto.builder()
                    .studentId(student.getStudentId())
                    .studentName(student.getFirstName() + " " + student.getLastName())
                    .schoolName(student.getSchool().getSchoolName())
                    .className(student.getClassMaster() != null ? student.getClassMaster().getClassName() : "N/A")
                    .sectionName(student.getSectionMaster() != null ? student.getSectionMaster().getSectionName() : "N/A")
                    .year(reportYear)
                    .month(reportMonth)
                    .monthName(monthStart.getMonth().name())
                    .totalSchoolDays(totalSchoolDays)
                    .presentDays(presentDays)
                    .absentDays(absentDays)
                    .lateDays(lateDays)
                    .attendancePercentage(attendancePercentage)
                    .totalTrips(totalTrips)
                    .completedTrips(completedTrips)
                    .missedTrips(missedTrips)
                    .tripCompletionRate(tripCompletionRate)
                    .build();

            System.out.println("🔍 Monthly report created successfully");
            return new ApiResponse(true, "Monthly report retrieved successfully", report);

        } catch (Exception e) {
            System.out.println("🔍 Error in getMonthlyReport: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving monthly report: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse updateParentProfile(Integer userId, UserRequestDto request) {
        System.out.println("🔍 updateParentProfile called with userId: " + userId);
        try {
            User parentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent user not found with ID: " + userId));

            // Update user fields
            if (request.getUserName() != null) {
                parentUser.setUserName(request.getUserName());
            }
            if (request.getEmail() != null) {
                parentUser.setEmail(request.getEmail());
            }
            if (request.getContactNumber() != null) {
                parentUser.setContactNumber(request.getContactNumber());
            }
            if (request.getPassword() != null && !request.getPassword().isEmpty()) {
                parentUser.setPassword(passwordEncoder.encode(request.getPassword()));
            }

            parentUser.setUpdatedBy(request.getCreatedBy());
            parentUser.setUpdatedDate(LocalDateTime.now());

            User savedUser = userRepository.save(parentUser);
            System.out.println("🔍 Parent profile updated successfully");

            return new ApiResponse(true, "Parent profile updated successfully", mapToUserResponse(savedUser));

        } catch (Exception e) {
            System.out.println("🔍 Error in updateParentProfile: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error updating parent profile: " + e.getMessage(), null);
        }
    }

    // ================ HELPER METHODS ================

    private ParentNotificationResponseDto mapToParentNotificationDto(Notification notification) {
        return ParentNotificationResponseDto.builder()
                .notificationId(notification.getNotificationLogId())
                .title("Trip Update")
                .message("Your child's trip status has been updated")
                .notificationType(notification.getNotificationType().toString())
                .eventType(notification.getDispatchLog().getEventType().toString())
                .studentName(notification.getDispatchLog().getStudent().getFirstName() + " " + 
                           notification.getDispatchLog().getStudent().getLastName())
                .vehicleNumber(notification.getDispatchLog().getVehicle() != null ? 
                             notification.getDispatchLog().getVehicle().getVehicleNumber() : "N/A")
                .tripName(notification.getDispatchLog().getTrip() != null ? 
                        notification.getDispatchLog().getTrip().getTripName() : "N/A")
                .notificationTime(notification.getCreatedDate())
                .isRead(false)
                .priority("Normal")
                .build();
    }

    private Map<String, Object> mapToNotificationDto(Notification notification) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("notificationId", notification.getNotificationLogId());
        dto.put("title", "Trip Update");
        dto.put("message", "Your child's trip status has been updated");
        dto.put("notificationType", notification.getNotificationType().toString());
        dto.put("eventType", notification.getDispatchLog().getEventType().toString());
        dto.put("studentName", notification.getDispatchLog().getStudent().getFirstName() + " " + 
                               notification.getDispatchLog().getStudent().getLastName());
        dto.put("vehicleNumber", notification.getDispatchLog().getVehicle() != null ? 
                                 notification.getDispatchLog().getVehicle().getVehicleNumber() : "N/A");
        dto.put("tripName", notification.getDispatchLog().getTrip() != null ? 
                            notification.getDispatchLog().getTrip().getTripName() : "N/A");
        dto.put("notificationTime", notification.getCreatedDate());
        dto.put("isRead", false);
        dto.put("priority", "Normal");
        return dto;
    }

    private Map<String, Object> mapToTripDto(Trip trip) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("tripId", trip.getTripId());
        dto.put("tripName", trip.getTripName());
        dto.put("tripNumber", trip.getTripNumber());
        dto.put("tripDate", trip.getTripStartTime() != null ? trip.getTripStartTime().toLocalDate() : null);
        dto.put("vehicleNumber", trip.getVehicle() != null ? trip.getVehicle().getVehicleNumber() : "N/A");
        dto.put("driverName", trip.getDriver() != null ? trip.getDriver().getDriverName() : "N/A");
        return dto;
    }

    private AttendanceHistoryResponseDto.AttendanceRecordDto mapToAttendanceRecordDto(StudentAttendance attendance) {
        return AttendanceHistoryResponseDto.AttendanceRecordDto.builder()
                .date(attendance.getAttendanceDate())
                .dayOfWeek(attendance.getAttendanceDate().getDayOfWeek().name())
                .isPresent(attendance.getIsPresent())
                .isAbsent(attendance.getIsAbsent())
                .isLate(attendance.getIsLate())
                .arrivalTime(attendance.getArrivalTime())
                .departureTime(attendance.getDepartureTime())
                .remarks(attendance.getRemarks())
                .build();
    }

    private Map<String, Object> mapToUserResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUId());
        response.put("userName", user.getUserName());
        response.put("email", user.getEmail());
        response.put("contactNumber", user.getContactNumber());
        response.put("isActive", user.getIsActive());
        response.put("createdDate", user.getCreatedDate());
        response.put("updatedDate", user.getUpdatedDate());
        return response;
    }

}
