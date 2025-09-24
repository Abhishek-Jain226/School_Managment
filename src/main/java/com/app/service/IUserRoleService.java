package com.app.service;

import com.app.payload.request.UserRoleRequestDto;
import com.app.payload.response.ApiResponse;

public interface IUserRoleService {

	ApiResponse assignUserRole(UserRoleRequestDto request);

	ApiResponse updateUserRole(Integer userRoleId, UserRoleRequestDto request);

	ApiResponse removeUserRole(Integer userRoleId);

	ApiResponse getUserRolesByUser(Integer userId);

	ApiResponse getAllUserRoles();

}
