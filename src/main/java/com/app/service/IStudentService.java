package com.app.service;

import com.app.payload.request.StudentRequestDto;
import com.app.payload.response.ApiResponse;

public interface IStudentService {

	ApiResponse createStudent(StudentRequestDto request);

	ApiResponse updateStudent(Integer studentId, StudentRequestDto request);

	ApiResponse deleteStudent(Integer studentId);

	ApiResponse getStudentById(Integer studentId);

	ApiResponse getStudentTrips(Integer studentId);

	ApiResponse getAllStudents(Integer schoolId);

	ApiResponse getStudentsByTrip(Integer tripId);
	
	long getStudentCountBySchool(Integer schoolId);
	
	//ApiResponse activateParent(Integer studentId, String activationCode);

}
