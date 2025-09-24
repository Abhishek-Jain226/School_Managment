package com.app.controller;

import com.app.payload.request.UserRoleRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IUserRoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-roles")
public class UserRoleController {

	@Autowired
    private IUserRoleService userRoleService;

    // ----------- Assign User Role -----------
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse> assignUserRole(@RequestBody UserRoleRequestDto request) {
        return ResponseEntity.ok(userRoleService.assignUserRole(request));
    }

    // ----------- Update User Role -----------
    @PutMapping("/{userRoleId}")
    public ResponseEntity<ApiResponse> updateUserRole(
            @PathVariable Integer userRoleId,
            @RequestBody UserRoleRequestDto request) {
        return ResponseEntity.ok(userRoleService.updateUserRole(userRoleId, request));
    }

    // ----------- Remove User Role -----------
    @DeleteMapping("/{userRoleId}")
    public ResponseEntity<ApiResponse> removeUserRole(@PathVariable Integer userRoleId) {
        return ResponseEntity.ok(userRoleService.removeUserRole(userRoleId));
    }

    // ----------- Get Roles By User -----------
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserRolesByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(userRoleService.getUserRolesByUser(userId));
    }

    // ----------- Get All User Roles -----------
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUserRoles() {
        return ResponseEntity.ok(userRoleService.getAllUserRoles());
    }
}
