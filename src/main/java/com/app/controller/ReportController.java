package com.app.controller;

import com.app.payload.response.ApiResponse;
import com.app.service.IReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
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

    // ----------- Download Report File -----------
    @GetMapping("/download/{schoolId}")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable Integer schoolId,
            @RequestParam String type,
            @RequestParam String format) {
        try {
            byte[] fileContent = reportService.generateReportFile(schoolId, type, format);
            String fileName = type + "_report_" + schoolId + "_" + System.currentTimeMillis() + "." + format.toLowerCase();
            
            ByteArrayResource resource = new ByteArrayResource(fileContent);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(fileContent.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // ----------- Test Endpoint -----------
    @GetMapping("/test")
    public ResponseEntity<ApiResponse> testEndpoint() {
        return ResponseEntity.ok(new ApiResponse(true, "Reports API is working!", null));
    }
}
