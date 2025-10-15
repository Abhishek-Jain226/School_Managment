package com.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.app.payload.request.SchoolUserRequestDto;
import com.app.payload.request.StaffCreateRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.ISchoolAdminService;

@RestController
@RequestMapping("/api/school-admin")
public class SchoolAdminController {

	@Autowired
    private ISchoolAdminService schoolAdminService;

    @PostMapping("/create-super-admin")
    public ResponseEntity<ApiResponse> createSuperAdmin(@Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(schoolAdminService.createSuperAdmin(request));
    }

//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDTO request) {
//        return ResponseEntity.ok(schoolAdminService.login(request));
//    }

    @PutMapping("/update-profile/{userId}")
    public ResponseEntity<ApiResponse> updateProfile(
            @PathVariable Integer userId,
            @Valid @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(schoolAdminService.updateProfile(userId, request));
    }

    @PostMapping("/assign-staff")
    public ResponseEntity<ApiResponse> assignStaff(@Valid @RequestBody SchoolUserRequestDto request) {
        return ResponseEntity.ok(schoolAdminService.assignStaffToSchool(request));
    }

    @GetMapping("/dashboard/{schoolId}")
    public ResponseEntity<ApiResponse> getDashboardStats(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolAdminService.getDashboardStats(schoolId));
    }
    
    @PostMapping("/create-staff")
    public ResponseEntity<ApiResponse> createStaff(@Valid @RequestBody StaffCreateRequestDto request) {
        return ResponseEntity.ok(schoolAdminService.createStaffAndAssign(request));
    }
    
    // ----------- Get All Staff by School -----------
    @GetMapping("/school/{schoolId}/staff")
    public ResponseEntity<ApiResponse> getAllStaffBySchool(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolAdminService.getAllStaffBySchool(schoolId));
    }
    
    // ----------- Update Staff Status -----------
    @PutMapping("/staff/{staffId}/status")
    public ResponseEntity<ApiResponse> updateStaffStatus(
            @PathVariable Integer staffId,
            @RequestParam Boolean isActive,
            @RequestParam String updatedBy) {
        return ResponseEntity.ok(schoolAdminService.updateStaffStatus(staffId, isActive, updatedBy));
    }
    
    // ----------- Delete Staff -----------
    @PutMapping("/staff/{staffId}/delete")
    public ResponseEntity<ApiResponse> deleteStaff(
            @PathVariable Integer staffId,
            @RequestParam String updatedBy) {
        return ResponseEntity.ok(schoolAdminService.deleteStaff(staffId, updatedBy));
    }
    
    // ----------- Debug: Get Staff by Name -----------
    @GetMapping("/school/{schoolId}/staff/debug/{name}")
    public ResponseEntity<ApiResponse> getStaffByName(
            @PathVariable Integer schoolId,
            @PathVariable String name) {
        return ResponseEntity.ok(schoolAdminService.getStaffByName(schoolId, name));
    }
    
    // ----------- Update Staff Role -----------
    @PutMapping("/staff/{staffId}/role")
    public ResponseEntity<ApiResponse> updateStaffRole(
            @PathVariable Integer staffId,
            @RequestParam Integer newRoleId,
            @RequestParam String updatedBy) {
        return ResponseEntity.ok(schoolAdminService.updateStaffRole(staffId, newRoleId, updatedBy));
    }
    
    // ----------- Update Staff Details -----------
    @PutMapping("/staff/{staffId}")
    public ResponseEntity<ApiResponse> updateStaffDetails(
            @PathVariable Integer staffId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(schoolAdminService.updateStaffDetails(staffId, request));
    }
    
    // ----------- Debug: Get All Users (Including PARENT) -----------
    @GetMapping("/school/{schoolId}/all-users")
    public ResponseEntity<ApiResponse> getAllUsersBySchool(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolAdminService.getAllUsersBySchool(schoolId));
    }
    
}
