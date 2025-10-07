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

    // ================ STUDENT TRIP ASSIGNMENT ENDPOINTS ================
    
    // ----------- Get Students for Trip Assignment -----------
    @GetMapping("/{ownerId}/students")
    public ResponseEntity<ApiResponse> getStudentsForTripAssignment(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getStudentsForTripAssignment(ownerId));
    }
    
    // ----------- Get Trips for Owner -----------
    @GetMapping("/{ownerId}/trips")
    public ResponseEntity<ApiResponse> getTripsByOwner(@PathVariable Integer ownerId) {
        return ResponseEntity.ok(vehicleOwnerService.getTripsByOwner(ownerId));
    }
    
    // ----------- Assign Student to Trip -----------
    @PostMapping("/{ownerId}/trips/{tripId}/assign-student")
    public ResponseEntity<ApiResponse> assignStudentToTrip(
            @PathVariable Integer ownerId,
            @PathVariable Integer tripId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(vehicleOwnerService.assignStudentToTrip(ownerId, tripId, request));
    }
    
    // ----------- Remove Student from Trip -----------
    @DeleteMapping("/{ownerId}/trips/{tripId}/students/{studentId}")
    public ResponseEntity<ApiResponse> removeStudentFromTrip(
            @PathVariable Integer ownerId,
            @PathVariable Integer tripId,
            @PathVariable Integer studentId) {
        return ResponseEntity.ok(vehicleOwnerService.removeStudentFromTrip(ownerId, tripId, studentId));
    }
    
    // ----------- Get Trip Students -----------
    @GetMapping("/{ownerId}/trips/{tripId}/students")
    public ResponseEntity<ApiResponse> getTripStudents(
            @PathVariable Integer ownerId,
            @PathVariable Integer tripId) {
        return ResponseEntity.ok(vehicleOwnerService.getTripStudents(ownerId, tripId));
    }
}
