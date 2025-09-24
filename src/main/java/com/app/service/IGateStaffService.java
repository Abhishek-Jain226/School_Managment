package com.app.service;

import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;

public interface IGateStaffService {
	
	ApiResponse registerGateStaff(UserRequestDto request);

    ApiResponse getAllGateStaff(Integer schoolId);

}
