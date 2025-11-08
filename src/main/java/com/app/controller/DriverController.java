package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

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

    // ----------- Get Time-Based Filtered Trips -----------
    @GetMapping("/{driverId}/trips/time-based")
    public ResponseEntity<ApiResponse> getTimeBasedTrips(@PathVariable Integer driverId) {
        return ResponseEntity.ok(driverService.getTimeBasedTrips(driverId));
    }

    // ----------- Get Driver Profile -----------
    @GetMapping("/{driverId}/profile")
    public ResponseEntity<ApiResponse> getDriverProfile(@PathVariable Integer driverId) {
        return ResponseEntity.ok(driverService.getDriverProfile(driverId));
    }

    // ----------- Update Driver Profile -----------
    @PutMapping("/{driverId}/profile")
    public ResponseEntity<ApiResponse> updateDriverProfile(@PathVariable Integer driverId, @Valid @RequestBody DriverRequestDto requestDto) {
        return ResponseEntity.ok(driverService.updateDriverProfile(driverId, requestDto));
    }

    // ----------- Get Driver Reports -----------
    @GetMapping("/{driverId}/reports")
    public ResponseEntity<ApiResponse> getDriverReports(@PathVariable Integer driverId) {
        return ResponseEntity.ok(driverService.getDriverReports(driverId));
    }

    // ----------- Send 5-Minute Alert -----------
    @PostMapping("/{driverId}/trip/{tripId}/student/{studentId}/alert-5min")
    public ResponseEntity<ApiResponse> send5MinuteAlert(@PathVariable Integer driverId, @PathVariable Integer tripId, @PathVariable Integer studentId) {
        return ResponseEntity.ok(driverService.send5MinuteAlert(driverId, tripId, studentId));
    }

    // ----------- Mark Pickup from Home (Morning Trip) -----------
    @PostMapping("/{driverId}/trip/{tripId}/student/{studentId}/pickup-home")
    public ResponseEntity<ApiResponse> markPickupFromHome(@PathVariable Integer driverId, @PathVariable Integer tripId, @PathVariable Integer studentId) {
        return ResponseEntity.ok(driverService.markPickupFromHome(driverId, tripId, studentId));
    }

    // ----------- Mark Drop to School (Morning Trip) -----------
    @PostMapping("/{driverId}/trip/{tripId}/student/{studentId}/drop-school")
    public ResponseEntity<ApiResponse> markDropToSchool(@PathVariable Integer driverId, @PathVariable Integer tripId, @PathVariable Integer studentId) {
        return ResponseEntity.ok(driverService.markDropToSchool(driverId, tripId, studentId));
    }

    // ----------- Mark Pickup from School (Afternoon Trip) -----------
    @PostMapping("/{driverId}/trip/{tripId}/student/{studentId}/pickup-school")
    public ResponseEntity<ApiResponse> markPickupFromSchool(@PathVariable Integer driverId, @PathVariable Integer tripId, @PathVariable Integer studentId) {
        return ResponseEntity.ok(driverService.markPickupFromSchool(driverId, tripId, studentId));
    }

    // ----------- Mark Drop to Home (Afternoon Trip) -----------
    @PostMapping("/{driverId}/trip/{tripId}/student/{studentId}/drop-home")
    public ResponseEntity<ApiResponse> markDropToHome(@PathVariable Integer driverId, @PathVariable Integer tripId, @PathVariable Integer studentId) {
        return ResponseEntity.ok(driverService.markDropToHome(driverId, tripId, studentId));
    }

    // ----------- Trip Management -----------
//    @PostMapping("/{driverId}/trip/{tripId}/end")
//    public ResponseEntity<ApiResponse> endTrip(@PathVariable Integer driverId, @PathVariable Integer tripId) {
//        return ResponseEntity.ok(driverService.endTrip(driverId, tripId));
//    }

    // ----------- Location Tracking -----------
    @PostMapping("/{driverId}/location")
    public ResponseEntity<ApiResponse> updateDriverLocation(@PathVariable Integer driverId, @RequestBody Map<String, Object> locationData) {
        return ResponseEntity.ok(driverService.updateDriverLocation(driverId, locationData));
    }

    @GetMapping("/{driverId}/location")
    public ResponseEntity<ApiResponse> getDriverLocation(@PathVariable Integer driverId) {
        return ResponseEntity.ok(driverService.getDriverLocation(driverId));
    }

    // ----------- Save Location Update for Active Trip -----------
    @PostMapping("/{driverId}/trip/{tripId}/location")
    public ResponseEntity<ApiResponse> saveLocationUpdate(
            @PathVariable Integer driverId,
            @PathVariable Integer tripId,
            @RequestBody Map<String, Object> locationData) {
        Double latitude = locationData.get("latitude") != null ? 
            ((Number) locationData.get("latitude")).doubleValue() : null;
        Double longitude = locationData.get("longitude") != null ? 
            ((Number) locationData.get("longitude")).doubleValue() : null;
        String address = locationData.get("address") != null ? 
            (String) locationData.get("address") : null;
        
        if (latitude == null || longitude == null) {
            return ResponseEntity.badRequest().body(
                new com.app.payload.response.ApiResponse(false, "latitude and longitude are required", null));
        }
        
        return ResponseEntity.ok(driverService.saveLocationUpdate(driverId, tripId, latitude, longitude, address));
    }

}
