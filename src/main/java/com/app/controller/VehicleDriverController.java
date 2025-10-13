package com.app.controller;

import com.app.payload.request.VehicleDriverRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IVehicleDriverService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vehicle-drivers")
public class VehicleDriverController {

	@Autowired
    private IVehicleDriverService vehicleDriverService;

    // ----------- Assign Driver To Vehicle -----------
    @PostMapping("/assign")
    public ResponseEntity<ApiResponse> assignDriverToVehicle(@Valid @RequestBody VehicleDriverRequestDto request) {
        return ResponseEntity.ok(vehicleDriverService.assignDriverToVehicle(request));
    }

    // ----------- Update Vehicle Driver Assignment -----------
    @PutMapping("/{vehicleDriverId}")
    public ResponseEntity<ApiResponse> updateVehicleDriver(
            @PathVariable Integer vehicleDriverId,
            @RequestBody VehicleDriverRequestDto request) {
        return ResponseEntity.ok(vehicleDriverService.updateVehicleDriver(vehicleDriverId, request));
    }

    // ----------- Remove Driver From Vehicle -----------
    @DeleteMapping("/{vehicleDriverId}")
    public ResponseEntity<ApiResponse> removeDriverFromVehicle(@PathVariable Integer vehicleDriverId) {
        return ResponseEntity.ok(vehicleDriverService.removeDriverFromVehicle(vehicleDriverId));
    }

    // ----------- Get Drivers By Vehicle -----------
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse> getDriversByVehicle(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(vehicleDriverService.getDriversByVehicle(vehicleId));
    }

    // ----------- Get Driver Assignments By Owner -----------
    @GetMapping("/owner/{ownerId}/assignments")
    public ResponseEntity<ApiResponse> getDriverAssignmentsByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleDriverService.getDriverAssignmentsByOwner(ownerId));
    }
}
