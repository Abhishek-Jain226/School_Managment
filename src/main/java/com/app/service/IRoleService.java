package com.app.service;

import com.app.payload.request.RoleRequestDto;
import com.app.payload.response.ApiResponse;

public interface IRoleService {

	ApiResponse createRole(RoleRequestDto request);

	ApiResponse updateRole(Integer roleId, RoleRequestDto request);

	ApiResponse deleteRole(Integer roleId);

	ApiResponse getRoleById(Integer roleId);

	ApiResponse getAllRoles();

}
