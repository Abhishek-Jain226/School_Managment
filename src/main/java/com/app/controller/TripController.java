package com.app.controller;

import com.app.payload.request.TripRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.ITripService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {

	@Autowired
    private ITripService tripService;

    // ----------- Create Trip -----------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createTrip(@RequestBody TripRequestDto request) {
        return ResponseEntity.ok(tripService.createTrip(request));
    }

    // ----------- Update Trip -----------
    @PutMapping("/{tripId}")
    public ResponseEntity<ApiResponse> updateTrip(
            @PathVariable Integer tripId,
            @RequestBody TripRequestDto request) {
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
}
