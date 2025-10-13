package com.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.response.ApiResponse;
import com.app.service.IAppAdminSchoolService;

@RestController
@RequestMapping("/api/app-admin/schools")
//@CrossOrigin(origins = "*")
public class AppAdminSchoolController {

    @Autowired
    private IAppAdminSchoolService appAdminSchoolService;

    /**
     * Get all schools with details for AppAdmin
     */
    @GetMapping
    public ResponseEntity<ApiResponse> getAllSchools() {
        ApiResponse response = appAdminSchoolService.getAllSchools();
        return ResponseEntity.ok(response);
    }

    /**
     * Get school details by ID
     */
    @GetMapping("/{schoolId}")
    public ResponseEntity<ApiResponse> getSchoolById(@PathVariable Integer schoolId) {
        ApiResponse response = appAdminSchoolService.getSchoolById(schoolId);
        return ResponseEntity.ok(response);
    }

    /**
     * Activate/Deactivate school
     */
    @PutMapping("/{schoolId}/status")
    public ResponseEntity<ApiResponse> updateSchoolStatus(
            @PathVariable Integer schoolId,
            @RequestParam Boolean isActive,
            @RequestParam String updatedBy) {
        ApiResponse response = appAdminSchoolService.updateSchoolStatus(schoolId, isActive, updatedBy);
        return ResponseEntity.ok(response);
    }

    /**
     * Update school start and end dates
     */
    @PutMapping("/{schoolId}/dates")
    public ResponseEntity<ApiResponse> updateSchoolDates(
            @PathVariable Integer schoolId,
            @RequestBody Map<String, Object> dateRequest) {
        ApiResponse response = appAdminSchoolService.updateSchoolDates(schoolId, dateRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get school statistics for AppAdmin dashboard
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse> getSchoolStatistics() {
        ApiResponse response = appAdminSchoolService.getSchoolStatistics();
        return ResponseEntity.ok(response);
    }

    /**
     * Search schools by name, city, or state
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchSchools(@RequestParam String query) {
        ApiResponse response = appAdminSchoolService.searchSchools(query);
        return ResponseEntity.ok(response);
    }
}
