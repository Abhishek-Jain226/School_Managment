package com.app.payload.response;

import com.app.Enum.TripType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponseDto {

    private Integer tripId;
    private String tripName;
    private Integer tripNumber;
    private TripType tripType;
    private String tripTypeDisplay; // Display name for frontend
    private LocalTime scheduledTime;
    private Integer estimatedDurationMinutes;
    private String routeName;
    private String routeDescription;
    private Boolean isActive;
    
    // Vehicle Information
    private Integer vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    private Integer vehicleCapacity;
    
    // School Information
    private Integer schoolId;
    private String schoolName;
    
    // Driver Information
    private Integer driverId;
    private String driverName;
    private String driverContactNumber;
    
    // Trip Status
    private String tripStatus; // NOT_STARTED, IN_PROGRESS, COMPLETED, CANCELLED
    private LocalDateTime tripStartTime;
    private LocalDateTime tripEndTime;
    
    // Student Information
    private Integer totalStudents;
    private Integer studentsPickedUp;
    private Integer studentsDropped;
    private Integer studentsAbsent;
    
    // Student List
    private List<TripStudentDto> students;
    
    // Metadata
    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripStudentDto {
        private Integer studentId;
        private String studentName;
        private String studentPhoto;
        private String className;
        private String sectionName;
        private String pickupLocation;
        private String dropLocation;
        private Integer pickupOrder;
        private Integer dropOrder;
        private String attendanceStatus; // PENDING, PICKED_UP, DROPPED, ABSENT
        private LocalDateTime pickupTime;
        private LocalDateTime dropTime;
        private String remarks;
        
        // Parent Information
        private String parentName;
        private String parentContactNumber;
        private String parentEmail;
    }
}