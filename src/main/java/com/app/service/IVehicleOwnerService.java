package com.app.service;

import com.app.payload.request.VehicleOwnerRequestDto;
import com.app.payload.response.ApiResponse;

public interface IVehicleOwnerService {

	ApiResponse registerVehicleOwner(VehicleOwnerRequestDto request);

	ApiResponse activateOwner(Integer ownerId, String activationCode);

	ApiResponse updateVehicleOwner(Integer ownerId, VehicleOwnerRequestDto request);

	ApiResponse deleteVehicleOwner(Integer ownerId);

	ApiResponse getVehicleOwnerById(Integer ownerId);

	ApiResponse getAllVehicleOwners(Integer schoolId);
	
	ApiResponse getVehicleOwnerByUserId(Integer userId);
	
	ApiResponse associateOwnerWithSchool(Integer ownerId, Integer schoolId, String createdBy);
	
	ApiResponse getAssociatedSchools(Integer ownerId);
	
	ApiResponse getVehiclesByOwner(Integer ownerId);
	
	ApiResponse getDriversByOwner(Integer ownerId);

	ApiResponse getVehiclesInTransitByOwner(Integer ownerId);

	ApiResponse getRecentActivityByOwner(Integer ownerId);

	// ================ STUDENT TRIP ASSIGNMENT METHODS ================
	
	ApiResponse getStudentsForTripAssignment(Integer ownerId);
	
	ApiResponse getTripsByOwner(Integer ownerId);
	
	ApiResponse assignStudentToTrip(Integer ownerId, Integer tripId, java.util.Map<String, Object> request);
	
	ApiResponse removeStudentFromTrip(Integer ownerId, Integer tripId, Integer studentId);
	
	ApiResponse getTripStudents(Integer ownerId, Integer tripId);

}
