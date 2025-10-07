package com.app.service;

import com.app.payload.request.SchoolVehicleRequestDto;
import com.app.payload.request.VehicleRequestDto;
import com.app.payload.response.ApiResponse;

public interface IVehicleService {

	ApiResponse createVehicle(VehicleRequestDto request);

	ApiResponse updateVehicle(Integer vehicleId, VehicleRequestDto request);

	ApiResponse deleteVehicle(Integer vehicleId);

	ApiResponse getVehicleById(Integer vehicleId);

	ApiResponse getAllVehicles(Integer schoolId);

	ApiResponse assignVehicleToSchool(SchoolVehicleRequestDto request);
	
	long getVehicleCountBySchool(Integer schoolId);
	
	// Removed commented method that used findByOwner_OwnerId
	
	ApiResponse getVehiclesByCreatedBy(String username);
	
	

}
