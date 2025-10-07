package com.app.service;

import com.app.payload.request.DriverRequestDto;
import com.app.payload.request.StudentAttendanceRequestDto;
import com.app.payload.request.NotificationRequestDto;
import com.app.payload.response.ApiResponse;

public interface IDriverService {

	ApiResponse createDriver(DriverRequestDto request);

	ApiResponse updateDriver(Integer driverId, DriverRequestDto request);

	ApiResponse deleteDriver(Integer driverId);

	ApiResponse getDriverById(Integer driverId);
	
	ApiResponse getDriverByUserId(Integer userId);

	ApiResponse getAllDrivers(Integer ownerId);

	// Driver Dashboard Methods
	ApiResponse getDriverDashboard(Integer driverId);

	ApiResponse getAssignedTrips(Integer driverId);

	ApiResponse getTripStudents(Integer driverId, Integer tripId);

	ApiResponse markStudentAttendance(Integer driverId, StudentAttendanceRequestDto request);

	ApiResponse sendParentNotification(Integer driverId, NotificationRequestDto request);

	ApiResponse startTrip(Integer driverId, Integer tripId);

	ApiResponse endTrip(Integer driverId, Integer tripId);

}
