package com.app.controller;

import com.app.payload.request.SchoolRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.ISchoolService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {

	@Autowired
    private ISchoolService schoolService;

    // ----------- Register School -----------
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerSchool(@RequestBody SchoolRequestDto request) {
        return ResponseEntity.ok(schoolService.registerSchool(request));
    }

    // ----------- Activate School -----------
    @PostMapping("/{schoolId}/activate")
    public ResponseEntity<ApiResponse> activateSchool(
            @PathVariable Integer schoolId,
            @RequestParam String activationCode) {
        return ResponseEntity.ok(schoolService.activateSchool(schoolId, activationCode));
    }

    // ----------- Activate School Account (New) -----------
    @PostMapping("/{schoolId}/activate-school")
    public ResponseEntity<ApiResponse> activateSchoolAccount(
            @PathVariable Integer schoolId,
            @RequestParam String activationToken) {
        return ResponseEntity.ok(schoolService.activateSchoolAccount(schoolId, activationToken));
    }

    // ----------- Update School -----------
    @PutMapping("/{schoolId}")
    public ResponseEntity<ApiResponse> updateSchool(
            @PathVariable Integer schoolId,
            @RequestBody SchoolRequestDto request) {
        return ResponseEntity.ok(schoolService.updateSchool(schoolId, request));
    }

    // ----------- Delete School -----------
    @DeleteMapping("/{schoolId}")
    public ResponseEntity<ApiResponse> deleteSchool(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolService.deleteSchool(schoolId));
    }

    // ----------- Get School By Id -----------
    @GetMapping("/{schoolId}")
    public ResponseEntity<ApiResponse> getSchoolById(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolService.getSchoolById(schoolId));
    }

    // ----------- Get All Schools -----------
    @GetMapping
    public ResponseEntity<ApiResponse> getAllSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    // ----------- Get Vehicles in Transit for School -----------
    @GetMapping("/{schoolId}/vehicles-in-transit")
    public ResponseEntity<ApiResponse> getVehiclesInTransit(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolService.getVehiclesInTransit(schoolId));
    }

    // ----------- Get Today's Attendance for School -----------
    @GetMapping("/{schoolId}/today-attendance")
    public ResponseEntity<ApiResponse> getTodayAttendance(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolService.getTodayAttendance(schoolId));
    }

    // ----------- Get Notifications for School -----------
    @GetMapping("/{schoolId}/notifications")
    public ResponseEntity<ApiResponse> getSchoolNotifications(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolService.getSchoolNotifications(schoolId));
    }

    // ----------- Get Classes for School -----------
    @GetMapping("/{schoolId}/classes")
    public ResponseEntity<ApiResponse> getSchoolClasses(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolService.getSchoolClasses(schoolId));
    }

    // ----------- Get Sections for School -----------
    @GetMapping("/{schoolId}/sections")
    public ResponseEntity<ApiResponse> getSchoolSections(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolService.getSchoolSections(schoolId));
    }

    // ----------- Get All Staff by School -----------
//    @GetMapping("/{schoolId}/staff")
//    public ResponseEntity<ApiResponse> getAllStaffBySchool(@PathVariable Integer schoolId) {
//        return ResponseEntity.ok(schoolService.getAllStaffBySchool(schoolId));
//    }

    // ----------- Update Staff Status -----------
    @PutMapping("/staff/{staffId}/status")
    public ResponseEntity<ApiResponse> updateStaffStatus(
            @PathVariable Integer staffId,
            @RequestParam Boolean isActive,
            @RequestParam String updatedBy) {
        return ResponseEntity.ok(schoolService.updateStaffStatus(staffId, isActive, updatedBy));
    }

    // ----------- Delete Staff -----------
    @DeleteMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse> deleteStaff(
            @PathVariable Integer staffId,
            @RequestParam String deletedBy) {
        return ResponseEntity.ok(schoolService.deleteStaff(staffId, deletedBy));
    }
}
