package com.app.controller;

import com.app.Enum.TripType;
import com.app.payload.request.TripRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.ITripService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
public class TripController {

	@Autowired
    private ITripService tripService;

    // ----------- Create Trip -----------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createTrip(@Valid @RequestBody TripRequestDto request) {
        return ResponseEntity.ok(tripService.createTrip(request));
    }

    // ----------- Update Trip -----------
    @PutMapping("/{tripId}")
    public ResponseEntity<ApiResponse> updateTrip(
            @PathVariable Integer tripId,
            @Valid @RequestBody TripRequestDto request) {
        return ResponseEntity.ok(tripService.updateTrip(tripId, request));
    }

    // ----------- Delete Trip -----------
    @DeleteMapping("/{tripId}")
    public ResponseEntity<ApiResponse> deleteTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripService.deleteTrip(tripId));
    }

    // ----------- Get Trip By Id -----------
    @GetMapping("/{tripId}")
    public ResponseEntity<ApiResponse> getTripById(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripService.getTripById(tripId));
    }

    // ----------- Get All Trips By School -----------
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ApiResponse> getAllTrips(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(tripService.getAllTrips(schoolId));
    }

    // ----------- Get Trips By Vehicle -----------
//    @GetMapping("/vehicle/{vehicleId}")
//    public ResponseEntity<ApiResponse> getTripsByVehicle(@PathVariable Integer vehicleId) {
//        return ResponseEntity.ok(tripService.getTripsByVehicle(vehicleId));
//    }

    // ----------- Get Trips By Driver -----------
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<ApiResponse> getTripsByDriver(@PathVariable Integer driverId) {
        return ResponseEntity.ok(tripService.getTripsByDriver(driverId));
    }

    // ----------- Get Today's Trips By Driver -----------
    @GetMapping("/driver/{driverId}/today")
    public ResponseEntity<ApiResponse> getTodayTripsByDriver(@PathVariable Integer driverId) {
        return ResponseEntity.ok(tripService.getTodayTripsByDriver(driverId));
    }

    // ----------- Get Trip Types for Dropdown -----------
    @GetMapping("/trip-types")
    public ResponseEntity<ApiResponse> getTripTypes() {
        List<Map<String, String>> tripTypes = Arrays.stream(TripType.values())
                .map(type -> Map.of(
                    "value", type.name(),
                    "label", type.getDisplayName()
                ))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(new ApiResponse(true, "Trip types retrieved successfully", tripTypes));
    }

    // ----------- Get Trip Status History -----------
    @GetMapping("/{tripId}/status-history")
    public ResponseEntity<ApiResponse> getTripStatusHistory(@PathVariable Integer tripId) {
        return ResponseEntity.ok(tripService.getTripStatusHistory(tripId));
    }
}
