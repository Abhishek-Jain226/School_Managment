package com.app.controller;

import com.app.payload.request.LoginRequestDTO;
import com.app.payload.request.SchoolUserRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.ISchoolAdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/school-admin")
public class SchoolAdminController {

	@Autowired
    private ISchoolAdminService schoolAdminService;

    @PostMapping("/create-super-admin")
    public ResponseEntity<ApiResponse> createSuperAdmin(@RequestBody UserRequestDto request) {
        return ResponseEntity.ok(schoolAdminService.createSuperAdmin(request));
    }

//    @PostMapping("/login")
//    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDTO request) {
//        return ResponseEntity.ok(schoolAdminService.login(request));
//    }

    @PutMapping("/update-profile/{userId}")
    public ResponseEntity<ApiResponse> updateProfile(
            @PathVariable Integer userId,
            @RequestBody UserRequestDto request) {
        return ResponseEntity.ok(schoolAdminService.updateProfile(userId, request));
    }

    @PostMapping("/assign-staff")
    public ResponseEntity<ApiResponse> assignStaff(@RequestBody SchoolUserRequestDto request) {
        return ResponseEntity.ok(schoolAdminService.assignStaffToSchool(request));
    }

    @GetMapping("/dashboard/{schoolId}")
    public ResponseEntity<ApiResponse> getDashboardStats(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(schoolAdminService.getDashboardStats(schoolId));
    }
}
