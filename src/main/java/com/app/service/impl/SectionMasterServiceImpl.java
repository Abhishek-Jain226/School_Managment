package com.app.service.impl;

import com.app.entity.SectionMaster;
import com.app.entity.School;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.SectionMasterRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.SectionMasterResponseDto;
import com.app.repository.SectionMasterRepository;
import com.app.repository.SchoolRepository;
import com.app.service.ISectionMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionMasterServiceImpl implements ISectionMasterService {

    @Autowired
    private SectionMasterRepository sectionMasterRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public ApiResponse createSection(SectionMasterRequestDto request) {
        try {
            // Validate school exists
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

            // Check if section name already exists for this school
            if (sectionMasterRepository.existsBySectionNameAndSchool(request.getSectionName(), school)) {
                return new ApiResponse(false, "Section with this name already exists for this school", null);
            }

            SectionMaster sectionMaster = SectionMaster.builder()
                    .sectionName(request.getSectionName())
                    .description(request.getDescription())
                    .school(school)
                    .isActive(request.getIsActive())
                    .createdBy(request.getCreatedBy())
                    .createdDate(LocalDateTime.now())
                    .build();

            SectionMaster savedSection = sectionMasterRepository.save(sectionMaster);
            SectionMasterResponseDto response = convertToResponseDto(savedSection);

            return new ApiResponse(true, "Section created successfully", response);
        } catch (Exception e) {
            return new ApiResponse(false, "Error creating section: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse updateSection(Integer sectionId, SectionMasterRequestDto request) {
        try {
            SectionMaster sectionMaster = sectionMasterRepository.findById(sectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with ID: " + sectionId));

            // Validate school exists
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

            // Check if section name already exists for this school (excluding current section)
            if (sectionMasterRepository.existsBySectionNameAndSchoolAndSectionIdNot(request.getSectionName(), school, sectionId)) {
                return new ApiResponse(false, "Section with this name already exists for this school", null);
            }

            sectionMaster.setSectionName(request.getSectionName());
            sectionMaster.setDescription(request.getDescription());
            sectionMaster.setSchool(school);
            sectionMaster.setIsActive(request.getIsActive());
            sectionMaster.setUpdatedBy(request.getUpdatedBy());
            sectionMaster.setUpdatedDate(LocalDateTime.now());

            SectionMaster updatedSection = sectionMasterRepository.save(sectionMaster);
            SectionMasterResponseDto response = convertToResponseDto(updatedSection);

            return new ApiResponse(true, "Section updated successfully", response);
        } catch (Exception e) {
            return new ApiResponse(false, "Error updating section: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse deleteSection(Integer sectionId) {
        try {
            SectionMaster sectionMaster = sectionMasterRepository.findById(sectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with ID: " + sectionId));

            sectionMasterRepository.delete(sectionMaster);
            return new ApiResponse(true, "Section deleted successfully", null);
        } catch (Exception e) {
            return new ApiResponse(false, "Error deleting section: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getSectionById(Integer sectionId) {
        try {
            SectionMaster sectionMaster = sectionMasterRepository.findById(sectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with ID: " + sectionId));

            SectionMasterResponseDto response = convertToResponseDto(sectionMaster);
            return new ApiResponse(true, "Section retrieved successfully", response);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving section: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getAllSections(Integer schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));
            
            List<SectionMaster> sections = sectionMasterRepository.findAllBySchoolOrderBySectionName(school);
            List<SectionMasterResponseDto> responses = sections.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());

            return new ApiResponse(true, "Sections retrieved successfully", responses);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving sections: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getAllActiveSections(Integer schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));
            
            List<SectionMaster> sections = sectionMasterRepository.findAllActiveBySchoolOrderBySectionName(school);
            List<SectionMasterResponseDto> responses = sections.stream()
                    .map(this::convertToResponseDto)
                    .collect(Collectors.toList());

            return new ApiResponse(true, "Active sections retrieved successfully", responses);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving active sections: " + e.getMessage(), null);
        }
    }


    @Override
    public ApiResponse toggleSectionStatus(Integer sectionId) {
        try {
            SectionMaster sectionMaster = sectionMasterRepository.findById(sectionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with ID: " + sectionId));

            sectionMaster.setIsActive(!sectionMaster.getIsActive());
            sectionMaster.setUpdatedDate(LocalDateTime.now());

            SectionMaster updatedSection = sectionMasterRepository.save(sectionMaster);
            SectionMasterResponseDto response = convertToResponseDto(updatedSection);

            return new ApiResponse(true, "Section status updated successfully", response);
        } catch (Exception e) {
            return new ApiResponse(false, "Error updating section status: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse checkSectionNameExists(String sectionName, Integer schoolId, Integer excludeSectionId) {
        try {
            // Validate school exists
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            boolean exists;
            if (excludeSectionId != null) {
                exists = sectionMasterRepository.existsBySectionNameAndSchoolAndSectionIdNot(sectionName, school, excludeSectionId);
            } else {
                exists = sectionMasterRepository.existsBySectionNameAndSchool(sectionName, school);
            }

            return new ApiResponse(true, "Section name check completed", exists);
        } catch (Exception e) {
            return new ApiResponse(false, "Error checking section name: " + e.getMessage(), null);
        }
    }

    private SectionMasterResponseDto convertToResponseDto(SectionMaster sectionMaster) {
        return SectionMasterResponseDto.builder()
                .sectionId(sectionMaster.getSectionId())
                .sectionName(sectionMaster.getSectionName())
                .description(sectionMaster.getDescription())
                .schoolId(sectionMaster.getSchool() != null ? sectionMaster.getSchool().getSchoolId() : null)
                .schoolName(sectionMaster.getSchool() != null ? sectionMaster.getSchool().getSchoolName() : null)
                .isActive(sectionMaster.getIsActive())
                .createdBy(sectionMaster.getCreatedBy())
                .createdDate(sectionMaster.getCreatedDate())
                .updatedBy(sectionMaster.getUpdatedBy())
                .updatedDate(sectionMaster.getUpdatedDate())
                .build();
    }
}
