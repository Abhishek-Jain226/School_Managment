package com.app.service;

import com.app.payload.request.SchoolRequestDto;
import com.app.payload.response.ApiResponse;

public interface ISchoolService {

	ApiResponse registerSchool(SchoolRequestDto request);

	ApiResponse activateSchool(Integer schoolId, String activationCode);

	ApiResponse updateSchool(Integer schoolId, SchoolRequestDto request);

	ApiResponse deleteSchool(Integer schoolId);

	ApiResponse getSchoolById(Integer schoolId);

	ApiResponse getAllSchools();

}
