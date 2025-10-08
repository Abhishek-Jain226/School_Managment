package com.app.payload.response;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceHistoryResponseDto {

    private Integer studentId;
    private String studentName;
    private LocalDate fromDate;
    private LocalDate toDate;
    
    // Summary Statistics
    private Long totalDays;
    private Long presentDays;
    private Long absentDays;
    private Long lateDays;
    private Double attendancePercentage;
    
    // Daily Records
    private List<AttendanceRecordDto> attendanceRecords;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttendanceRecordDto {
        private LocalDate date;
        private String dayOfWeek;
        private Boolean isPresent;
        private Boolean isAbsent;
        private Boolean isLate;
        private String arrivalTime;
        private String departureTime;
        private String remarks;
    }
}
