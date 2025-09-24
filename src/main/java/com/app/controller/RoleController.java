package com.app.controller;

import com.app.payload.request.RoleRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IRoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

	@Autowired
    private IRoleService roleService;

    // ----------- Create Role -----------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createRole(@RequestBody RoleRequestDto request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    // ----------- Update Role -----------
    @PutMapping("/{roleId}")
    public ResponseEntity<ApiResponse> updateRole(
            @PathVariable Integer roleId,
            @RequestBody RoleRequestDto request) {
        return ResponseEntity.ok(roleService.updateRole(roleId, request));
    }

    // ----------- Delete Role -----------
    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse> deleteRole(@PathVariable Integer roleId) {
        return ResponseEntity.ok(roleService.deleteRole(roleId));
    }

    // ----------- Get Role By Id -----------
    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse> getRoleById(@PathVariable Integer roleId) {
        return ResponseEntity.ok(roleService.getRoleById(roleId));
    }

    // ----------- Get All Roles -----------
    @GetMapping
    public ResponseEntity<ApiResponse> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
