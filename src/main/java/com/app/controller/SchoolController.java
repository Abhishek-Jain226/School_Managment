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
}
