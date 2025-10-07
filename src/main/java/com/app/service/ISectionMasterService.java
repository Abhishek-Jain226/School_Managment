package com.app.service;

import com.app.entity.SectionMaster;
import com.app.payload.request.SectionMasterRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.SectionMasterResponseDto;

import java.util.List;

public interface ISectionMasterService {

    // Create new section
    ApiResponse createSection(SectionMasterRequestDto request);

    // Update existing section
    ApiResponse updateSection(Integer sectionId, SectionMasterRequestDto request);

    // Delete section
    ApiResponse deleteSection(Integer sectionId);

    // Get section by ID
    ApiResponse getSectionById(Integer sectionId);

    // Get all sections for a school
    ApiResponse getAllSections(Integer schoolId);

    // Get all active sections for a school
    ApiResponse getAllActiveSections(Integer schoolId);


    // Toggle section active status
    ApiResponse toggleSectionStatus(Integer sectionId);

    // Check if section name exists for a school
    ApiResponse checkSectionNameExists(String sectionName, Integer schoolId, Integer excludeSectionId);
}
