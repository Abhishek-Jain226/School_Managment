package com.app.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.service.IGateStaffService;
import com.app.payload.response.ApiResponse;

@RestController
@RequestMapping("/api/gate-staff")
public class GateStaffController {

    @Autowired
    private IGateStaffService gateStaffService;

    // ----------- Get Gate Staff Dashboard -----------
    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<?> getGateStaffDashboard(@PathVariable Integer userId) {
        return ResponseEntity.ok(gateStaffService.getGateStaffDashboard(userId));
    }

    // ----------- Get Students by Trip -----------
    @GetMapping("/{userId}/trips/{tripId}/students")
    public ResponseEntity<?> getStudentsByTrip(
            @PathVariable Integer userId,
            @PathVariable Integer tripId) {
        return ResponseEntity.ok(gateStaffService.getStudentsByTrip(userId, tripId));
    }

    // ----------- Mark Gate Entry -----------
    @PostMapping("/{userId}/gate-entry")
    public ResponseEntity<?> markGateEntry(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> request) {
        try {
            Integer studentId = (Integer) request.get("studentId");
            Integer tripId = (Integer) request.get("tripId");
            String remarks = (String) request.get("remarks");
            
            if (studentId == null || tripId == null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "Student ID and Trip ID are required", null)
                );
            }
            
            return ResponseEntity.ok(gateStaffService.markGateEntry(userId, studentId, tripId, remarks));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse(false, "Error marking gate entry: " + e.getMessage(), null)
            );
        }
    }

    // ----------- Mark Gate Exit -----------
    @PostMapping("/{userId}/gate-exit")
    public ResponseEntity<?> markGateExit(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> request) {
        try {
            Integer studentId = (Integer) request.get("studentId");
            Integer tripId = (Integer) request.get("tripId");
            String remarks = (String) request.get("remarks");
            
            if (studentId == null || tripId == null) {
                return ResponseEntity.badRequest().body(
                    new ApiResponse(false, "Student ID and Trip ID are required", null)
                );
            }
            
            return ResponseEntity.ok(gateStaffService.markGateExit(userId, studentId, tripId, remarks));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                new ApiResponse(false, "Error marking gate exit: " + e.getMessage(), null)
            );
        }
    }

    // ----------- Get Recent Dispatch Logs -----------
    @GetMapping("/{userId}/recent-logs")
    public ResponseEntity<?> getRecentDispatchLogs(@PathVariable Integer userId) {
        return ResponseEntity.ok(gateStaffService.getRecentDispatchLogs(userId));
    }

    // ----------- Get Gate Staff by User ID -----------
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getGateStaffByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(gateStaffService.getGateStaffByUserId(userId));
    }
}