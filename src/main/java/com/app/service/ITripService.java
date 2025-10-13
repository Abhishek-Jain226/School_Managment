package com.app.service;

import com.app.payload.request.TripRequestDto;
import com.app.payload.response.ApiResponse;

public interface ITripService {

	ApiResponse createTrip(TripRequestDto request);

	ApiResponse updateTrip(Integer tripId, TripRequestDto request);

	ApiResponse deleteTrip(Integer tripId);

	ApiResponse getTripById(Integer tripId);

	ApiResponse getAllTrips(Integer schoolId);

	ApiResponse getTripsByDriver(Integer driverId);

	ApiResponse getTodayTripsByDriver(Integer driverId);

	ApiResponse getTripStatusHistory(Integer tripId);

}
