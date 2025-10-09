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
	
	ApiResponse getAllStaffBySchool(Integer schoolId);
	
	ApiResponse updateStaffStatus(Integer staffId, Boolean isActive, String updatedBy);
	
	ApiResponse deleteStaff(Integer staffId, String updatedBy);
	
	ApiResponse getStaffByName(Integer schoolId, String name);
	
	ApiResponse updateStaffRole(Integer staffId, Integer newRoleId, String updatedBy);
	
	ApiResponse getAllUsersBySchool(Integer schoolId);

}
