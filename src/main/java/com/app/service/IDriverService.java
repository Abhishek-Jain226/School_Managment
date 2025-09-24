package com.app.service;

import com.app.payload.request.DriverRequestDto;
import com.app.payload.response.ApiResponse;

public interface IDriverService {

	ApiResponse createDriver(DriverRequestDto request);

	ApiResponse updateDriver(Integer driverId, DriverRequestDto request);

	ApiResponse deleteDriver(Integer driverId);

	ApiResponse getDriverById(Integer driverId);

	ApiResponse getAllDrivers(Integer ownerId);

}
