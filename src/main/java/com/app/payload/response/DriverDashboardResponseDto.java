package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDashboardResponseDto {

    private Integer driverId;
    private String driverName;
    private String driverContactNumber;
    private String driverPhoto;
    
    // Vehicle Information
    private Integer vehicleId;
    private String vehicleNumber;
    private String vehicleType;
    private Integer vehicleCapacity;
    
    // School Information
    private Integer schoolId;
    private String schoolName;
    
    // Dashboard Statistics
    private Integer totalTripsToday;
    private Integer completedTrips;
    private Integer pendingTrips;
    private Integer totalStudentsToday;
    private Integer studentsPickedUp;
    private Integer studentsDropped;
    
    // Current Trip Information
    private Integer currentTripId;
    private String currentTripName;
    private String currentTripStatus;
    private LocalDateTime currentTripStartTime;
    private Integer currentTripStudentCount;
    
    // Recent Activity
    private List<RecentActivityDto> recentActivities;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecentActivityDto {
        private Integer activityId;
        private String activityType;
        private String description;
        private LocalDateTime activityTime;
        private String studentName;
        private String location;
    }
}
