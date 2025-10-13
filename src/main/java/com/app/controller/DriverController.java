package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.app.payload.request.DriverRequestDto;
import com.app.payload.request.StudentAttendanceRequestDto;
import com.app.payload.request.NotificationRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IDriverService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

	@Autowired
	private IDriverService driverService;

	//  Create Driver
	@PostMapping("/create")
	public ResponseEntity<ApiResponse> createDriver(@Valid @RequestBody DriverRequestDto requestDto) {
		ApiResponse response = driverService.createDriver(requestDto);
		return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
	}

	// ----------- Get Driver by User ID -----------
	@GetMapping("/user/{userId}")
	public ResponseEntity<ApiResponse> getDriverByUserId(@PathVariable Integer userId) {
		return ResponseEntity.ok(driverService.getDriverByUserId(userId));
	}

	// ----------- Get Driver Dashboard -----------
	@GetMapping("/dashboard/{driverId}")
	public ResponseEntity<ApiResponse> getDriverDashboard(@PathVariable Integer driverId) {
		return ResponseEntity.ok(driverService.getDriverDashboard(driverId));
	}

	// ----------- Get Assigned Trips for Driver -----------
	@GetMapping("/{driverId}/trips")
	public ResponseEntity<ApiResponse> getAssignedTrips(@PathVariable Integer driverId) {
		return ResponseEntity.ok(driverService.getAssignedTrips(driverId));
	}

	// ----------- Get Students for a Specific Trip -----------
	@GetMapping("/{driverId}/trip/{tripId}/students")
	public ResponseEntity<ApiResponse> getTripStudents(@PathVariable Integer driverId, @PathVariable Integer tripId) {
		return ResponseEntity.ok(driverService.getTripStudents(driverId, tripId));
	}

	// ----------- Mark Student Attendance -----------
	@PostMapping("/{driverId}/attendance")
	public ResponseEntity<ApiResponse> markAttendance(@PathVariable Integer driverId, @Valid @RequestBody StudentAttendanceRequestDto request) {
		return ResponseEntity.ok(driverService.markStudentAttendance(driverId, request));
	}

	// ----------- Send Notification to Parents -----------
	@PostMapping("/{driverId}/notify-parents")
	public ResponseEntity<ApiResponse> notifyParents(@PathVariable Integer driverId, @Valid @RequestBody NotificationRequestDto request) {
		return ResponseEntity.ok(driverService.sendParentNotification(driverId, request));
	}

	// ----------- Start Trip -----------
	@PostMapping("/{driverId}/trip/{tripId}/start")
	public ResponseEntity<ApiResponse> startTrip(@PathVariable Integer driverId, @PathVariable Integer tripId) {
		return ResponseEntity.ok(driverService.startTrip(driverId, tripId));
	}

    // ----------- End Trip -----------
    @PostMapping("/{driverId}/trip/{tripId}/end")
    public ResponseEntity<ApiResponse> endTrip(@PathVariable Integer driverId, @PathVariable Integer tripId) {
        return ResponseEntity.ok(driverService.endTrip(driverId, tripId));
    }

}
