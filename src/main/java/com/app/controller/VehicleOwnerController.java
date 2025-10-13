package com.app.controller;

import com.app.payload.request.VehicleOwnerRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IVehicleOwnerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    
    // ----------- Get Schools Associated with Vehicle Owner -----------
    @GetMapping("/{ownerId}/schools")
    public ResponseEntity<ApiResponse> getAssociatedSchools(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getAssociatedSchools(ownerId));
    }
    
    // ----------- Get Vehicles by Owner -----------
    @GetMapping("/{ownerId}/vehicles")
    public ResponseEntity<ApiResponse> getVehiclesByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getVehiclesByOwner(ownerId));
    }
    
    // ----------- Get Drivers by Owner -----------
    @GetMapping("/{ownerId}/drivers")
    public ResponseEntity<ApiResponse> getDriversByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getDriversByOwner(ownerId));
    }
    
    // ----------- Get Vehicles in Transit by Owner -----------
    @GetMapping("/{ownerId}/vehicles-in-transit")
    public ResponseEntity<ApiResponse> getVehiclesInTransitByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getVehiclesInTransitByOwner(ownerId));
    }
    
    // ----------- Get Recent Activity by Owner -----------
    @GetMapping("/{ownerId}/recent-activity")
    public ResponseEntity<ApiResponse> getRecentActivityByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getRecentActivityByOwner(ownerId));
    }
    
    // ----------- Get Driver Assignments by Owner -----------
    @GetMapping("/{ownerId}/driver-assignments")
    public ResponseEntity<ApiResponse> getDriverAssignments(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getDriverAssignments(ownerId));
    }
    
    // ----------- Get Total Assignments by Owner -----------
    @GetMapping("/{ownerId}/total-assignments")
    public ResponseEntity<ApiResponse> getTotalAssignmentsByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getTotalAssignmentsByOwner(ownerId));
    }
    
    // ----------- Get Pending Driver Registrations by Owner -----------
    @GetMapping("/{ownerId}/pending-driver-registrations")
    public ResponseEntity<ApiResponse> getPendingDriverRegistrations(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getPendingDriverRegistrations(ownerId));
    }

    // ========== TRIP ASSIGNMENT ENDPOINTS ==========
    
    // ----------- Get Trips by Owner -----------
    @GetMapping("/{ownerId}/trips")
    public ResponseEntity<ApiResponse> getTripsByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getTripsByOwner(ownerId));
    }
    
    // ----------- Get Available Vehicles for Trip Assignment -----------
    @GetMapping("/{ownerId}/available-vehicles/{schoolId}")
    public ResponseEntity<ApiResponse> getAvailableVehiclesForTrip(
            @PathVariable Integer ownerId, 
            @PathVariable Integer schoolId) {
        return ResponseEntity.ok(vehicleOwnerService.getAvailableVehiclesForTrip(ownerId, schoolId));
    }
    
    // ----------- Assign Trip to Vehicle -----------
    @PutMapping("/{ownerId}/assign-trip/{tripId}/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse> assignTripToVehicle(
            @PathVariable Integer ownerId,
            @PathVariable Integer tripId,
            @PathVariable Integer vehicleId,
            @RequestParam String updatedBy) {
        return ResponseEntity.ok(vehicleOwnerService.assignTripToVehicle(tripId, vehicleId, updatedBy));
    }

    // ========== ENHANCED DRIVER MANAGEMENT ENDPOINTS ==========
    
    // ----------- Set Driver Availability -----------
    @PutMapping("/{ownerId}/driver-availability/{vehicleDriverId}")
    public ResponseEntity<ApiResponse> setDriverAvailability(
            @PathVariable Integer ownerId,
            @PathVariable Integer vehicleDriverId,
            @RequestParam Boolean isAvailable,
            @RequestParam(required = false) String reason,
            @RequestParam String updatedBy) {
        return ResponseEntity.ok(vehicleOwnerService.setDriverAvailability(
                vehicleDriverId, isAvailable, reason, updatedBy));
    }
    
    // ----------- Set Backup Driver -----------
    @PutMapping("/{ownerId}/backup-driver/{vehicleDriverId}")
    public ResponseEntity<ApiResponse> setBackupDriver(
            @PathVariable Integer ownerId,
            @PathVariable Integer vehicleDriverId,
            @RequestParam Boolean isBackup,
            @RequestParam String updatedBy) {
        return ResponseEntity.ok(vehicleOwnerService.setBackupDriver(
                vehicleDriverId, isBackup, updatedBy));
    }
    
    // ----------- Get Driver Rotation Schedule -----------
    @GetMapping("/{ownerId}/driver-rotation-schedule")
    public ResponseEntity<ApiResponse> getDriverRotationSchedule(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getDriverRotationSchedule(ownerId));
    }
    
    // ----------- Reassign Trip Driver -----------
    @PutMapping("/{ownerId}/reassign-trip/{tripId}/driver/{driverId}")
    public ResponseEntity<ApiResponse> reassignTripDriver(
            @PathVariable Integer ownerId,
            @PathVariable Integer tripId,
            @PathVariable Integer driverId,
            @RequestParam String updatedBy) {
        return ResponseEntity.ok(vehicleOwnerService.reassignTripDriver(tripId, driverId, updatedBy));
    }

}
