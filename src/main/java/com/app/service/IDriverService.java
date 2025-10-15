package com.app.service;

import com.app.payload.request.DriverRequestDto;
import com.app.payload.request.StudentAttendanceRequestDto;
import com.app.payload.request.NotificationRequestDto;
import com.app.payload.response.ApiResponse;
import java.util.Map;

public interface IDriverService {

	ApiResponse createDriver(DriverRequestDto request);

	ApiResponse updateDriver(Integer driverId, DriverRequestDto request);

	ApiResponse deleteDriver(Integer driverId);

	ApiResponse getDriverById(Integer driverId);
	
	ApiResponse getDriverByUserId(Integer userId);

	ApiResponse getAllDrivers(Integer ownerId);

	ApiResponse getAllDriversForAdmin();

	// Driver Dashboard Methods
	ApiResponse getDriverDashboard(Integer driverId);

	ApiResponse getAssignedTrips(Integer driverId);

	ApiResponse getTripStudents(Integer driverId, Integer tripId);

	ApiResponse markStudentAttendance(Integer driverId, StudentAttendanceRequestDto request);

	ApiResponse sendParentNotification(Integer driverId, NotificationRequestDto request);

	ApiResponse startTrip(Integer driverId, Integer tripId);

	ApiResponse endTrip(Integer driverId, Integer tripId);

	// Enhanced Driver Dashboard Methods
	ApiResponse getTimeBasedTrips(Integer driverId);

	ApiResponse getDriverProfile(Integer driverId);

	ApiResponse updateDriverProfile(Integer driverId, DriverRequestDto requestDto);

	ApiResponse getDriverReports(Integer driverId);

	ApiResponse send5MinuteAlert(Integer driverId, Integer tripId);

	// Context-Sensitive Student Actions
	ApiResponse markPickupFromHome(Integer driverId, Integer tripId, Integer studentId);

	ApiResponse markDropToSchool(Integer driverId, Integer tripId, Integer studentId);

	ApiResponse markPickupFromSchool(Integer driverId, Integer tripId, Integer studentId);

	ApiResponse markDropToHome(Integer driverId, Integer tripId, Integer studentId);

	ApiResponse getDriverLocation(Integer driverId);

	// Location Tracking
	ApiResponse updateDriverLocation(Integer driverId, Map<String, Object> locationData);

}
