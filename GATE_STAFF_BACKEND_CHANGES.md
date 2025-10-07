# Gate Staff Backend Changes - Step by Step

## 1. Uncomment and Fix Gate Staff Controller

**File**: `src/main/java/com/app/controller/GateStaffController.java`

```java
package com.app.controller;

import com.app.payload.request.GateEventRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IGateStaffService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gate-staff")
public class GateStaffController {

    @Autowired
    private IGateStaffService gateStaffService;

    @GetMapping("/dashboard/{gateStaffId}")
    public ResponseEntity<ApiResponse> getGateStaffDashboard(@PathVariable Integer gateStaffId) {
        return ResponseEntity.ok(gateStaffService.getGateStaffDashboard(gateStaffId));
    }

    @GetMapping("/students/{schoolId}")
    public ResponseEntity<ApiResponse> getAssignedStudents(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(gateStaffService.getAssignedStudents(schoolId));
    }

    @PostMapping("/entry")
    public ResponseEntity<ApiResponse> markGateEntry(@RequestBody GateEventRequestDto request) {
        return ResponseEntity.ok(gateStaffService.markGateEntry(request));
    }

    @PostMapping("/exit")
    public ResponseEntity<ApiResponse> markGateExit(@RequestBody GateEventRequestDto request) {
        return ResponseEntity.ok(gateStaffService.markGateExit(request));
    }

    @GetMapping("/logs/{schoolId}")
    public ResponseEntity<ApiResponse> getRecentDispatchLogs(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(gateStaffService.getRecentDispatchLogs(schoolId));
    }
}
```

## 2. Uncomment and Fix Gate Staff Service Interface

**File**: `src/main/java/com/app/service/IGateStaffService.java`

```java
package com.app.service;

import com.app.payload.request.GateEventRequestDto;
import com.app.payload.response.ApiResponse;

public interface IGateStaffService {

    ApiResponse getGateStaffDashboard(Integer gateStaffId);
    
    ApiResponse getAssignedStudents(Integer schoolId);

    ApiResponse markGateEntry(GateEventRequestDto request);

    ApiResponse markGateExit(GateEventRequestDto request);
    
    ApiResponse getRecentDispatchLogs(Integer schoolId);

}
```

## 3. Create Gate Staff Entity

**File**: `src/main/java/com/app/entity/GateStaff.java`

```java
package com.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gate_staff", uniqueConstraints = {
    @UniqueConstraint(name = "uk_gate_staff_user", columnNames = {"u_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GateStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gate_staff_id")
    private Integer gateStaffId;

    @OneToOne
    @JoinColumn(name = "u_id", unique = true)
    private User user;

    @NotBlank(message = "Gate staff name is required")
    @Column(name = "gate_staff_name", nullable = false, length = 100)
    private String gateStaffName;

    @NotBlank(message = "Gate staff contact number is required")
    @Size(min = 10, max = 15, message = "Gate staff contact number must be between 10 and 15 characters")
    @Column(name = "gate_staff_contact_number", nullable = false, length = 15, unique = true)
    private String gateStaffContactNumber;

    @NotBlank(message = "Gate staff address is required")
    @Column(name = "gate_staff_address", nullable = false, length = 255)
    private String gateStaffAddress;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
```

## 4. Create Gate Staff Repository

**File**: `src/main/java/com/app/repository/GateStaffRepository.java`

```java
package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.GateStaff;
import com.app.entity.User;

public interface GateStaffRepository extends JpaRepository<GateStaff, Integer> {

    boolean existsByGateStaffContactNumber(String gateStaffContactNumber);
    
    Optional<GateStaff> findByUser(User user);
    
    boolean existsByUser(User user);
    
    Optional<GateStaff> findByUser_uId(Integer uId);
}
```

## 5. Create Gate Staff Dashboard Response DTO

**File**: `src/main/java/com/app/payload/response/GateStaffDashboardResponseDto.java`

```java
package com.app.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GateStaffDashboardResponseDto {

    // Gate Staff Information
    private Integer gateStaffId;
    private String gateStaffName;
    private String gateStaffContactNumber;
    
    // School Information
    private Integer schoolId;
    private String schoolName;
    
    // Today's Statistics
    private Integer totalStudentsToday;
    private Integer studentsEnteredToday;
    private Integer studentsExitedToday;
    private Integer activeTripsToday;
    
    // Recent Activity
    private List<DispatchLogResponseDto> recentDispatchLogs;
}
```

## 6. Create Student by Trip Response DTO

**File**: `src/main/java/com/app/payload/response/StudentByTripResponseDto.java`

```java
package com.app.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentByTripResponseDto {

    private Integer tripId;
    private String tripName;
    private String tripType;
    private String tripStatus;
    private String scheduledTime;
    
    private Integer vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    
    private Integer driverId;
    private String driverName;
    private String driverContactNumber;
    
    private List<StudentResponseDto> students;
    
    // Trip Statistics
    private Integer totalStudents;
    private Integer studentsEntered;
    private Integer studentsExited;
}
```

## 7. Update Gate Event Request DTO

**File**: `src/main/java/com/app/payload/request/GateEventRequestDto.java`

```java
package com.app.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GateEventRequestDto {
    
    @NotNull(message = "studentId is required")
    private Integer studentId;

    @NotNull(message = "tripId is required")
    private Integer tripId;

    @NotNull(message = "gateStaffId is required")
    private Integer gateStaffId; // Changed from staffUserId to gateStaffId

    private String remarks; // Optional remarks for the event
}
```

## 8. Uncomment and Fix Gate Staff Service Implementation

**File**: `src/main/java/com/app/service/impl/GateStaffServiceImpl.java`

```java
package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.DispatchLog;
import com.app.entity.GateStaff;
import com.app.entity.School;
import com.app.entity.Student;
import com.app.entity.Trip;
import com.app.entity.User;
import com.app.Enum.EventType;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.GateEventRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.DispatchLogResponseDto;
import com.app.payload.response.GateStaffDashboardResponseDto;
import com.app.payload.response.StudentByTripResponseDto;
import com.app.repository.DispatchLogRepository;
import com.app.repository.GateStaffRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.StudentRepository;
import com.app.repository.TripRepository;
import com.app.repository.UserRepository;
import com.app.service.IGateStaffService;
import com.app.service.INotificationService;

@Service
@Transactional
public class GateStaffServiceImpl implements IGateStaffService{
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private DispatchLogRepository dispatchLogRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private GateStaffRepository gateStaffRepository;
    
    @Autowired
    private INotificationService notificationService;
    
    @Override
    public ApiResponse getGateStaffDashboard(Integer gateStaffId) {
        try {
            GateStaff gateStaff = gateStaffRepository.findById(gateStaffId)
                    .orElseThrow(() -> new ResourceNotFoundException("Gate staff not found with ID: " + gateStaffId));

            School school = gateStaff.getSchool();
            
            // Get today's statistics
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            
            List<DispatchLog> todayLogs = dispatchLogRepository.findBySchoolAndCreatedDateBetween(
                school, startOfDay, endOfDay);
            
            int studentsEnteredToday = (int) todayLogs.stream()
                .filter(log -> log.getEventType() == EventType.GATE_ENTRY)
                .count();
            
            int studentsExitedToday = (int) todayLogs.stream()
                .filter(log -> log.getEventType() == EventType.GATE_EXIT)
                .count();
            
            // Get active trips for today
            List<Trip> activeTrips = tripRepository.findBySchoolAndIsActiveTrue(school);
            int activeTripsToday = activeTrips.size();
            
            // Get total students for today
            int totalStudentsToday = school.getStudents().size();
            
            // Get recent dispatch logs
            List<DispatchLog> recentLogs = dispatchLogRepository.findTop10BySchoolOrderByCreatedDateDesc(school);
            List<DispatchLogResponseDto> recentDispatchLogs = recentLogs.stream()
                .map(this::mapToDispatchLogResponse)
                .collect(Collectors.toList());

            GateStaffDashboardResponseDto dashboard = GateStaffDashboardResponseDto.builder()
                    .gateStaffId(gateStaff.getGateStaffId())
                    .gateStaffName(gateStaff.getGateStaffName())
                    .gateStaffContactNumber(gateStaff.getGateStaffContactNumber())
                    .schoolId(school.getSchoolId())
                    .schoolName(school.getSchoolName())
                    .totalStudentsToday(totalStudentsToday)
                    .studentsEnteredToday(studentsEnteredToday)
                    .studentsExitedToday(studentsExitedToday)
                    .activeTripsToday(activeTripsToday)
                    .recentDispatchLogs(recentDispatchLogs)
                    .build();

            return new ApiResponse(true, "Gate staff dashboard fetched successfully", dashboard);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to fetch gate staff dashboard: " + e.getMessage(), null);
        }
    }
    
    @Override
    public ApiResponse getAssignedStudents(Integer schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            // Get active trips for the school
            List<Trip> activeTrips = tripRepository.findBySchoolAndIsActiveTrue(school);
            
            List<StudentByTripResponseDto> studentsByTrip = activeTrips.stream()
                .map(trip -> {
                    List<Student> tripStudents = studentRepository.findByTripsContaining(trip);
                    
                    return StudentByTripResponseDto.builder()
                        .tripId(trip.getTripId())
                        .tripName(trip.getTripName())
                        .tripType(trip.getTripType())
                        .tripStatus(trip.getTripStatus())
                        .scheduledTime(trip.getScheduledTime() != null ? trip.getScheduledTime().toString() : null)
                        .vehicleId(trip.getVehicle().getVehicleId())
                        .vehicleNumber(trip.getVehicle().getVehicleNumber())
                        .vehicleType(trip.getVehicle().getVehicleType())
                        .driverId(trip.getDriver() != null ? trip.getDriver().getDriverId() : null)
                        .driverName(trip.getDriver() != null ? trip.getDriver().getDriverName() : null)
                        .driverContactNumber(trip.getDriver() != null ? trip.getDriver().getDriverContactNumber() : null)
                        .students(tripStudents.stream().map(this::mapToStudentResponse).collect(Collectors.toList()))
                        .totalStudents(tripStudents.size())
                        .studentsEntered(0) // TODO: Calculate from dispatch logs
                        .studentsExited(0) // TODO: Calculate from dispatch logs
                        .build();
                })
                .collect(Collectors.toList());

            return new ApiResponse(true, "Assigned students fetched successfully", studentsByTrip);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to fetch assigned students: " + e.getMessage(), null);
        }
    }
    
    @Override
    public ApiResponse markGateEntry(GateEventRequestDto request) {
        try {
            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

            Trip trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

            GateStaff gateStaff = gateStaffRepository.findById(request.getGateStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Gate staff not found"));

            // Create dispatch log
            DispatchLog log = DispatchLog.builder()
                    .student(student)
                    .trip(trip)
                    .school(student.getSchool())
                    .vehicle(trip.getVehicle())
                    .eventType(EventType.GATE_ENTRY)
                    .remarks(request.getRemarks())
                    .createdBy(gateStaff.getUser().getUserName())
                    .build();

            dispatchLogRepository.save(log);

            // Send notifications
            String message = "Student " + student.getStudentName() + " has ENTERED the school gate at " + 
                           LocalDateTime.now().toString();
            notificationService.sendNotification(student, message);

            return new ApiResponse(true, "Gate Entry marked successfully", null);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to mark gate entry: " + e.getMessage(), null);
        }
    }
    
    @Override
    public ApiResponse markGateExit(GateEventRequestDto request) {
        try {
            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

            Trip trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

            GateStaff gateStaff = gateStaffRepository.findById(request.getGateStaffId())
                    .orElseThrow(() -> new ResourceNotFoundException("Gate staff not found"));

            // Create dispatch log
            DispatchLog log = DispatchLog.builder()
                    .student(student)
                    .trip(trip)
                    .school(student.getSchool())
                    .vehicle(trip.getVehicle())
                    .eventType(EventType.GATE_EXIT)
                    .remarks(request.getRemarks())
                    .createdBy(gateStaff.getUser().getUserName())
                    .build();

            dispatchLogRepository.save(log);

            // Send notifications
            String message = "Student " + student.getStudentName() + " has EXITED the school gate at " + 
                           LocalDateTime.now().toString();
            notificationService.sendNotification(student, message);

            return new ApiResponse(true, "Gate Exit marked successfully", null);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to mark gate exit: " + e.getMessage(), null);
        }
    }
    
    @Override
    public ApiResponse getRecentDispatchLogs(Integer schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            List<DispatchLog> recentLogs = dispatchLogRepository.findTop20BySchoolOrderByCreatedDateDesc(school);
            List<DispatchLogResponseDto> logs = recentLogs.stream()
                .map(this::mapToDispatchLogResponse)
                .collect(Collectors.toList());

            return new ApiResponse(true, "Recent dispatch logs fetched successfully", logs);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Failed to fetch dispatch logs: " + e.getMessage(), null);
        }
    }
    
    // Helper methods
    private DispatchLogResponseDto mapToDispatchLogResponse(DispatchLog log) {
        return DispatchLogResponseDto.builder()
                .dispatchLogId(log.getDispatchLogId())
                .tripId(log.getTrip().getTripId())
                .tripName(log.getTrip().getTripName())
                .studentId(log.getStudent().getStudentId())
                .studentName(log.getStudent().getStudentName())
                .schoolId(log.getSchool().getSchoolId())
                .schoolName(log.getSchool().getSchoolName())
                .vehicleId(log.getVehicle().getVehicleId())
                .vehicleNumber(log.getVehicle().getVehicleNumber())
                .eventType(log.getEventType().name())
                .remarks(log.getRemarks())
                .createdBy(log.getCreatedBy())
                .createdDate(log.getCreatedDate())
                .updatedBy(log.getUpdatedBy())
                .updatedDate(log.getUpdatedDate())
                .build();
    }
    
    private StudentResponseDto mapToStudentResponse(Student student) {
        return StudentResponseDto.builder()
                .studentId(student.getStudentId())
                .studentName(student.getStudentName())
                .studentContactNumber(student.getStudentContactNumber())
                .studentAddress(student.getStudentAddress())
                .parentName(student.getParentName())
                .parentContactNumber(student.getParentContactNumber())
                .build();
    }
}
```

## 9. Add Gate Staff ID to Login Response

**File**: `src/main/java/com/app/service/impl/AuthServiceImpl.java`

Add these imports:
```java
import com.app.repository.GateStaffRepository;
import com.app.entity.GateStaff;
```

Add this field:
```java
@Autowired
private GateStaffRepository gateStaffRepository;
```

Add this variable in login method:
```java
Integer gateStaffId = null;
```

Add this logic after the driver logic:
```java
// ✅ Agar Gate Staff hai
if (roles.contains("GATE_STAFF")) {
    System.out.println("🔍 User has GATE_STAFF role, looking up gate staff record for user: " + user.getUserName());
    try {
        GateStaff gateStaff = gateStaffRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Gate staff not found for user: " + user.getUserName()));
        gateStaffId = gateStaff.getGateStaffId();
        System.out.println("🔍 Gate Staff ID found: " + gateStaffId);
    } catch (Exception e) {
        System.out.println("⚠️ Error finding gate staff: " + e.getMessage());
        e.printStackTrace();
    }
   
    if (schoolUserOpt.isPresent()) {
        School school = schoolUserOpt.get().getSchool();
        schoolId = school.getSchoolId();
        schoolName = school.getSchoolName();
    }
} else {
    System.out.println("🔍 User does not have GATE_STAFF role. Roles: " + roles);
}
```

Add to response data:
```java
data.put("gateStaffId", gateStaffId);
```

## 10. Add Missing Repository Methods

**File**: `src/main/java/com/app/repository/DispatchLogRepository.java`

Add these methods:
```java
List<DispatchLog> findBySchoolAndCreatedDateBetween(School school, LocalDateTime start, LocalDateTime end);
List<DispatchLog> findTop10BySchoolOrderByCreatedDateDesc(School school);
List<DispatchLog> findTop20BySchoolOrderByCreatedDateDesc(School school);
```

**File**: `src/main/java/com/app/repository/TripRepository.java`

Add this method:
```java
List<Trip> findBySchoolAndIsActiveTrue(School school);
```

**File**: `src/main/java/com/app/repository/StudentRepository.java`

Add this method:
```java
List<Student> findByTripsContaining(Trip trip);
```

## 11. Database Setup

Run these SQL commands:

```sql
-- Create gate_staff table
CREATE TABLE gate_staff (
    gate_staff_id INT PRIMARY KEY AUTO_INCREMENT,
    u_id INT UNIQUE NOT NULL,
    gate_staff_name VARCHAR(100) NOT NULL,
    gate_staff_contact_number VARCHAR(15) UNIQUE NOT NULL,
    gate_staff_address VARCHAR(255) NOT NULL,
    school_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_by VARCHAR(50),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50),
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (u_id) REFERENCES users(u_id),
    FOREIGN KEY (school_id) REFERENCES schools(school_id)
);

-- Add GATE_STAFF role if not exists
INSERT INTO roles (role_name, role_description) 
VALUES ('GATE_STAFF', 'Gate Staff Role') 
ON DUPLICATE KEY UPDATE role_name = role_name;
```

This completes all the backend changes needed for the Gate Staff Dashboard functionality.


