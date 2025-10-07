package com.app.service;

import com.app.payload.request.ClassMasterRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.ClassMasterResponseDto;

import java.util.List;

public interface IClassMasterService {

    // Create new class
    ApiResponse createClass(ClassMasterRequestDto request);

    // Update existing class
    ApiResponse updateClass(Integer classId, ClassMasterRequestDto request);

    // Delete class
    ApiResponse deleteClass(Integer classId);

    // Get class by ID
    ApiResponse getClassById(Integer classId);

    // Get all classes for a school
    ApiResponse getAllClasses(Integer schoolId);

    // Get all active classes for a school
    ApiResponse getAllActiveClasses(Integer schoolId);

    // Toggle class active status
    ApiResponse toggleClassStatus(Integer classId);

    // Check if class name exists for a school
    ApiResponse checkClassNameExists(String className, Integer schoolId, Integer excludeClassId);
}
