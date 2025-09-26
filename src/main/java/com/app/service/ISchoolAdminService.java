package com.app.service;

import com.app.payload.request.SchoolUserRequestDto;
import com.app.payload.request.StaffCreateRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;

public interface ISchoolAdminService {

	ApiResponse createSuperAdmin(UserRequestDto request); // after activation

	//ApiResponse login(LoginRequestDTO request);

	ApiResponse updateProfile(Integer userId, UserRequestDto request);

	ApiResponse assignStaffToSchool(SchoolUserRequestDto request);

	ApiResponse getDashboardStats(Integer schoolId);
	
	ApiResponse createStaffAndAssign(StaffCreateRequestDto request);

}
