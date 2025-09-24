package com.app.service;

import com.app.payload.request.LoginRequestDTO;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;

public interface IUserService {

	// Registration
	ApiResponse registerUser(UserRequestDto request);

	// Authentication
	//ApiResponse login(LoginRequestDTO request);

	// Profile Updates
	ApiResponse updateUser(Integer userId, UserRequestDto request);

	// Deactivate / Delete
	ApiResponse deactivateUser(Integer userId);

	// Get Details
	ApiResponse getUserById(Integer userId);

	ApiResponse getAllUsers();

	// Role Management
	ApiResponse assignRoleToUser(Integer userId, Integer roleId);

	ApiResponse removeRoleFromUser(Integer userRoleId);

	ApiResponse getUserRoles(Integer userId);

}
