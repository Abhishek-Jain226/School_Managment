package com.app.controller;

import com.app.payload.request.SchoolVehicleRequestDto;
import com.app.payload.request.VehicleRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IVehicleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
 
	@Autowired
    private IVehicleService vehicleService;

    // ----------- Register Vehicle -----------
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerVehicle(@RequestBody VehicleRequestDto request) {
        return ResponseEntity.ok(vehicleService.createVehicle(request));
    }

    // ----------- Update Vehicle -----------
    @PutMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse> updateVehicle(
            @PathVariable Integer vehicleId,
            @RequestBody VehicleRequestDto request) {
        return ResponseEntity.ok(vehicleService.updateVehicle(vehicleId, request));
    }

    // ----------- Delete Vehicle -----------
    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse> deleteVehicle(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(vehicleService.deleteVehicle(vehicleId));
    }

    // ----------- Get Vehicle By Id -----------
    @GetMapping("/{vehicleId}")
    public ResponseEntity<ApiResponse> getVehicleById(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(vehicleService.getVehicleById(vehicleId));
    }

    // ----------- Get All Vehicles By School -----------
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ApiResponse> getVehiclesBySchool(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(vehicleService.getAllVehicles(schoolId));
    }

    // ----------- Assign Vehicle To School -----------
    @PostMapping("/assign-to-school")
    public ResponseEntity<ApiResponse> assignVehicleToSchool(@RequestBody SchoolVehicleRequestDto request) {
        return ResponseEntity.ok(vehicleService.assignVehicleToSchool(request));
    }
    

    // ✅ Vehicle count by schoolId
    @GetMapping("/count/{schoolId}")
    public ResponseEntity<ApiResponse> getVehicleCount(@PathVariable Integer schoolId) {
        long count = vehicleService.getVehicleCountBySchool(schoolId);
        return ResponseEntity.ok(new ApiResponse(true, "Vehicle count fetched", count));
    }
    
    // ✅ Get vehicles by ownerId
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<ApiResponse> getVehiclesByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleService.getVehiclesByOwner(ownerId));
    }
    
    // ✅ Get vehicles by username (alternative method)
    @GetMapping("/owner/username/{username}")
    public ResponseEntity<ApiResponse> getVehiclesByCreatedBy(@PathVariable String username) {
        return ResponseEntity.ok(vehicleService.getVehiclesByCreatedBy(username));
    }
    
    // ✅ Get UNASSIGNED vehicles by ownerId (for school assignment requests)
    @GetMapping("/owner/{ownerId}/unassigned")
    public ResponseEntity<ApiResponse> getUnassignedVehiclesByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleService.getUnassignedVehiclesByOwner(ownerId));
    }
}
