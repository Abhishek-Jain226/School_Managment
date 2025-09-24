package com.app.service;

import com.app.payload.request.DispatchLogRequestDto;
import com.app.payload.response.ApiResponse;

public interface IDispatchLogService {

	ApiResponse createDispatchLog(DispatchLogRequestDto request);

	ApiResponse updateDispatchLog(Integer dispatchLogId, DispatchLogRequestDto request);

	ApiResponse getDispatchLogById(Integer dispatchLogId);

	ApiResponse getDispatchLogsByTrip(Integer tripId);

	ApiResponse getDispatchLogsByVehicle(Integer vehicleId);

}
