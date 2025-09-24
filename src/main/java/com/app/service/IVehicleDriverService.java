package com.app.service;

import com.app.payload.request.VehicleDriverRequestDto;
import com.app.payload.response.ApiResponse;

public interface IVehicleDriverService {

	ApiResponse assignDriverToVehicle(VehicleDriverRequestDto request);

	ApiResponse updateVehicleDriver(Integer vehicleDriverId, VehicleDriverRequestDto request);

	ApiResponse removeDriverFromVehicle(Integer vehicleDriverId);

	ApiResponse getDriversByVehicle(Integer vehicleId);

}
