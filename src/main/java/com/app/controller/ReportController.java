package com.app.controller;

import com.app.payload.response.ApiResponse;
import com.app.service.IReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReportController {

	@Autowired
    private IReportService reportService;

    // ----------- Attendance Report -----------
    @GetMapping("/attendance/{schoolId}")
    public ResponseEntity<ApiResponse> getAttendanceReport(
            @PathVariable Integer schoolId,
            @RequestParam String filterType) {
        return ResponseEntity.ok(reportService.getAttendanceReport(schoolId, filterType));
    }

    // ----------- Dispatch Logs Report -----------
    @GetMapping("/dispatch-logs/{schoolId}")
    public ResponseEntity<ApiResponse> getDispatchLogsReport(
            @PathVariable Integer schoolId,
            @RequestParam String filterType) {
        return ResponseEntity.ok(reportService.getDispatchLogsReport(schoolId, filterType));
    }

    // ----------- Notification Logs Report -----------
    @GetMapping("/notifications/{schoolId}")
    public ResponseEntity<ApiResponse> getNotificationLogsReport(
            @PathVariable Integer schoolId,
            @RequestParam String filterType) {
        return ResponseEntity.ok(reportService.getNotificationLogsReport(schoolId, filterType));
    }

    // ----------- Export Report -----------
    @GetMapping("/export/{schoolId}")
    public ResponseEntity<ApiResponse> exportReport(
            @PathVariable Integer schoolId,
            @RequestParam String type,
            @RequestParam String format) {
        return ResponseEntity.ok(reportService.exportReport(schoolId, type, format));
    }
}
