package com.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.response.ApiResponse;
import com.app.service.IAppAdminService;

@RestController
@RequestMapping("/api/app-admin")
public class AppAdminController {

    @Autowired
    private IAppAdminService appAdminService;

    /**
     * Get AppAdmin Dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> getDashboard() {
        ApiResponse response = appAdminService.getDashboard();
        return ResponseEntity.ok(response);
    }

    /**
     * Get AppAdmin Profile
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile() {
        ApiResponse response = appAdminService.getProfile();
        return ResponseEntity.ok(response);
    }

    /**
     * Update AppAdmin Profile
     */
    @GetMapping("/system-stats")
    public ResponseEntity<ApiResponse> getSystemStats() {
        ApiResponse response = appAdminService.getSystemStats();
        return ResponseEntity.ok(response);
    }

    /**
     * Get Reports
     */
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse> getReports(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String startDate,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String endDate) {
        ApiResponse response = appAdminService.getReports(startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
