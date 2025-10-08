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


}
