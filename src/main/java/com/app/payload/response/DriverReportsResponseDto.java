package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverReportsResponseDto {
    
    // Overall Statistics
    private Integer totalTripsCompleted;
    private Integer totalStudentsTransported;
    private Integer totalDistanceCovered; // in kilometers
    private Double averageRating;
    
    // Today's Statistics
    private Integer todayTrips;
    private Integer todayStudents;
    private Integer todayPickups;
    private Integer todayDrops;
    
    // This Week Statistics
    private Integer weekTrips;
    private Integer weekStudents;
    private Integer weekPickups;
    private Integer weekDrops;
    
    // This Month Statistics
    private Integer monthTrips;
    private Integer monthStudents;
    private Integer monthPickups;
    private Integer monthDrops;
    
    // Attendance Records
    private List<AttendanceRecord> attendanceRecords;
    
    // Recent Trips
    private List<RecentTrip> recentTrips;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendanceRecord {
        private LocalDate date;
        private Integer totalTrips;
        private Integer completedTrips;
        private Integer studentsPickedUp;
        private Integer studentsDropped;
        private String status; // PRESENT, ABSENT, LATE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentTrip {
        private Integer tripId;
        private String tripName;
        private String tripType;
        private LocalDate tripDate;
        private String startTime;
        private String endTime;
        private Integer studentsCount;
        private String status;
        private String route;
    }
    
}
