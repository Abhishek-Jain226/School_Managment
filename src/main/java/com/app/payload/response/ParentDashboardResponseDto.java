package com.app.payload.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentDashboardResponseDto {

    private Integer userId;
    private String userName;
    private String email;
    private String contactNumber;
    
    // Student Information
    private Integer studentId;
    private String studentName;
    private String studentPhoto;
    private String className;
    private String sectionName;
    private String schoolName;
    
    // Today's Status
    private String todayAttendanceStatus;
    private String todayArrivalTime;
    private String todayDepartureTime;
    
    // Statistics
    private Long totalPresentDays;
    private Long totalAbsentDays;
    private Long totalLateDays;
    private Double attendancePercentage;
    
    // Recent Notifications
    private List<Map<String, Object>> recentNotifications;
    
    // Recent Trips
    private List<Map<String, Object>> recentTrips;
    
    // Monthly Summary
    private Map<String, Object> monthlySummary;
    
    private LocalDate lastUpdated;
}
