package com.app.service;

import com.app.payload.request.StudentParentRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;

public interface IParentService {

	ApiResponse createParent(UserRequestDto request);

	ApiResponse linkParentToStudent(StudentParentRequestDto request);

	ApiResponse updateParent(Integer parentId, UserRequestDto request);

	ApiResponse getParentById(Integer parentId);

	ApiResponse getAllParents(Integer schoolId);
	
	ApiResponse getParentByUserId(Integer userId);
	
	ApiResponse getStudentByParentUserId(Integer userId);

}
