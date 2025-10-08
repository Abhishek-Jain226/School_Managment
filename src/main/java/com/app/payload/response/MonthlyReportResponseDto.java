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
public class MonthlyReportResponseDto {

    private Integer studentId;
    private String studentName;
    private String schoolName;
    private String className;
    private String sectionName;
    private Integer year;
    private Integer month;
    private String monthName;
    
    // Attendance Summary
    private Long totalSchoolDays;
    private Long presentDays;
    private Long absentDays;
    private Long lateDays;
    private Double attendancePercentage;
    
    // Trip Summary
    private Long totalTrips;
    private Long completedTrips;
    private Long missedTrips;
    private Double tripCompletionRate;
    
    // Performance Metrics
    private Map<String, Object> performanceMetrics;
    
    // Daily Breakdown
    private List<DailyReportDto> dailyReports;
    
    // Weekly Summary
    private List<WeeklyReportDto> weeklyReports;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyReportDto {
        private LocalDate date;
        private String dayOfWeek;
        private String attendanceStatus;
        private String tripStatus;
        private String arrivalTime;
        private String departureTime;
        private String remarks;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeeklyReportDto {
        private Integer weekNumber;
        private LocalDate weekStart;
        private LocalDate weekEnd;
        private Long presentDays;
        private Long absentDays;
        private Long lateDays;
        private Double weeklyAttendancePercentage;
    }
}
