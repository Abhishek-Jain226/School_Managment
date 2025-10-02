package com.app.controller;

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

import com.app.payload.request.VehicleAssignmentRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IVehicleAssignmentService;

@RestController
@RequestMapping("/api/vehicle-assignments")
public class VehicleAssignmentController {
	
	@Autowired
	private IVehicleAssignmentService service;
	
	// Owner -> create request
    @PostMapping("/request")
    public ResponseEntity<ApiResponse> createRequest(@RequestBody VehicleAssignmentRequestDto dto) {
        return ResponseEntity.ok(service.createRequest(dto));
    }

    // Admin -> approve
    @PutMapping("/{requestId}/approve")
    public ResponseEntity<ApiResponse> approve(@PathVariable Integer requestId, @RequestParam String updatedBy) {
        return ResponseEntity.ok(service.approveRequest(requestId, updatedBy));
    }

    // Admin -> reject
    @PutMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse> reject(@PathVariable Integer requestId, @RequestParam String updatedBy) {
        return ResponseEntity.ok(service.rejectRequest(requestId, updatedBy));
    }

    // Admin -> list pending for school
    @GetMapping("/school/{schoolId}/pending")
    public ResponseEntity<ApiResponse> getPending(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(service.getPendingRequestsBySchool(schoolId));
    }

    // Owner -> list his requests
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse> getOwnerRequests(@PathVariable Integer ownerId) {
    	return ResponseEntity.ok(service.getRequestsByOwner(ownerId));
    }
}
