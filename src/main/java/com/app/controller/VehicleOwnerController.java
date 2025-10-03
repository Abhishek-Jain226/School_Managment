package com.app.controller;

import com.app.payload.request.VehicleOwnerRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IVehicleOwnerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicle-owners")
public class VehicleOwnerController {

	@Autowired
    private IVehicleOwnerService vehicleOwnerService;

    // ----------- Register Vehicle Owner -----------
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerOwner(@RequestBody VehicleOwnerRequestDto request) {
        return ResponseEntity.ok(vehicleOwnerService.registerVehicleOwner(request));
    }

    // ----------- Activate Vehicle Owner -----------
    @PostMapping("/{ownerId}/activate")
    public ResponseEntity<ApiResponse> activateOwner(
            @PathVariable Integer ownerId,
            @RequestParam String activationCode) {
        return ResponseEntity.ok(vehicleOwnerService.activateOwner(ownerId, activationCode));
    }

    // ----------- Update Vehicle Owner -----------
    @PutMapping("/{ownerId}")
    public ResponseEntity<ApiResponse> updateOwner(
            @PathVariable Integer ownerId,
            @RequestBody VehicleOwnerRequestDto request) {
        return ResponseEntity.ok(vehicleOwnerService.updateVehicleOwner(ownerId, request));
    }

    // ----------- Delete Vehicle Owner -----------
    @DeleteMapping("/{ownerId}")
    public ResponseEntity<ApiResponse> deleteOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.deleteVehicleOwner(ownerId));
    }

    // ----------- Get Vehicle Owner By Id -----------
    @GetMapping("/{ownerId}")
    public ResponseEntity<ApiResponse> getOwnerById(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getVehicleOwnerById(ownerId));
    }

    // ----------- Get All Vehicle Owners for a School -----------
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ApiResponse> getAllOwners(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(vehicleOwnerService.getAllVehicleOwners(schoolId));
    }
    
 // ----------- Get Vehicle Owner By UserId -----------
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getOwnerByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(vehicleOwnerService.getVehicleOwnerByUserId(userId));
    }
    
    // ----------- Associate Existing Vehicle Owner with School -----------
    @PostMapping("/{ownerId}/associate-school")
    public ResponseEntity<ApiResponse> associateOwnerWithSchool(
            @PathVariable Integer ownerId,
            @RequestParam Integer schoolId,
            @RequestParam String createdBy) {
        return ResponseEntity.ok(vehicleOwnerService.associateOwnerWithSchool(ownerId, schoolId, createdBy));
    }
}
