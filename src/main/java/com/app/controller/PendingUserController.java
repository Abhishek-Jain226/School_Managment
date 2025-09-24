package com.app.controller;

import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.response.ApiResponse;
import com.app.service.IPendingUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pending-users")
public class PendingUserController {

	@Autowired
    private IPendingUserService pendingUserService;

    // ----------- Create Pending User -----------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createPendingUser(@RequestBody PendingUserRequestDTO request) {
        return ResponseEntity.ok(pendingUserService.createPendingUser(request));
    }

    // ----------- Verify Pending User By Token -----------
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyPendingUser(@RequestParam String token) {
        return ResponseEntity.ok(pendingUserService.verifyPendingUser(token));
    }

    // ----------- Get Pending User By Id -----------
    @GetMapping("/{pendingUserId}")
    public ResponseEntity<ApiResponse> getPendingUserById(@PathVariable Integer pendingUserId) {
        return ResponseEntity.ok(pendingUserService.getPendingUserById(pendingUserId));
    }

    // ----------- Get All Pending Users -----------
    @GetMapping
    public ResponseEntity<ApiResponse> getAllPendingUsers() {
        return ResponseEntity.ok(pendingUserService.getAllPendingUsers());
    }

    // ----------- Delete Pending User -----------
    @DeleteMapping("/{pendingUserId}")
    public ResponseEntity<ApiResponse> deletePendingUser(@PathVariable Integer pendingUserId) {
        return ResponseEntity.ok(pendingUserService.deletePendingUser(pendingUserId));
    }
}
