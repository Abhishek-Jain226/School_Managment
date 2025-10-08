package com.app.service;

import com.app.payload.request.TripStudentRequestDto;
import com.app.payload.response.ApiResponse;

public interface ITripStudentService {

	ApiResponse assignStudentToTrip(TripStudentRequestDto request);

	ApiResponse updateTripStudent(Integer tripStudentId, TripStudentRequestDto request);

	ApiResponse removeStudentFromTrip(Integer tripStudentId);

	ApiResponse getStudentsByTrip(Integer tripId);

	ApiResponse getAllAssignmentsBySchool(Integer schoolId);

}
