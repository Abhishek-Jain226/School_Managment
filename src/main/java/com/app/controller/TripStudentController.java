package com.app.controller;

import com.app.payload.request.TripStudentRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.ITripStudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trip-students")

public class TripStudentController {

	@Autowired
    private ITripStudentService tripStudentService;

    // ----------- Assign Student To Trip -----------
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse> assignStudentToTrip(@RequestBody TripStudentRequestDto request) {
        return ResponseEntity.ok(tripStudentService.assignStudentToTrip(request));
    }

    // ----------- Update Trip Student -----------
    @PutMapping("/{tripStudentId}")
    public ResponseEntity<ApiResponse> updateTripStudent(
            @PathVariable Integer tripStudentId,
            @RequestBody TripStudentRequestDto request) {
        return ResponseEntity.ok(tripStudentService.updateTripStudent(tripStudentId, request));
    }

    // ----------- Remove Student From Trip -----------
    @DeleteMapping("/{tripStudentId}")
    public ResponseEntity<ApiResponse> removeStudentFromTrip(@PathVariable Integer tripStudentId) {
        return ResponseEntity.ok(tripStudentService.removeStudentFromTrip(tripStudentId));
    }

    // ----------- Get Students By Trip -----------
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<ApiResponse> getStudentsByTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripStudentService.getStudentsByTrip(tripId));
    }

    // ----------- Get All Trip-Student Assignments by School -----------
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ApiResponse> getAllAssignmentsBySchool(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(tripStudentService.getAllAssignmentsBySchool(schoolId));
    }
}
