package com.app.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entity.School;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.SchoolResponseDto;
import com.app.repository.SchoolRepository;
import com.app.service.IAppAdminSchoolService;

@Service
public class AppAdminSchoolServiceImpl implements IAppAdminSchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Override
    public ApiResponse getAllSchools() {
        try {
            List<School> schools = schoolRepository.findAll();
            List<SchoolResponseDto> schoolDtos = schools.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("schools", schoolDtos);
            responseData.put("totalCount", schoolDtos.size());
            responseData.put("activeCount", schoolDtos.stream()
                    .mapToInt(school -> Boolean.TRUE.equals(school.getIsActive()) ? 1 : 0)
                    .sum());
            responseData.put("inactiveCount", schoolDtos.stream()
                    .mapToInt(school -> Boolean.FALSE.equals(school.getIsActive()) ? 1 : 0)
                    .sum());

            return new ApiResponse(true, "All schools retrieved successfully", responseData);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving schools: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getSchoolById(Integer schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            return new ApiResponse(true, "School retrieved successfully", mapToResponse(school));
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving school: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse updateSchoolStatus(Integer schoolId, Boolean isActive, String updatedBy) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            school.setIsActive(isActive);
            school.setUpdatedBy(updatedBy);
            school.setUpdatedDate(LocalDateTime.now());

            School updatedSchool = schoolRepository.save(school);

            String statusMessage = isActive ? "activated" : "deactivated";
            return new ApiResponse(true, 
                    "School " + school.getSchoolName() + " has been " + statusMessage + " successfully", 
                    mapToResponse(updatedSchool));
        } catch (Exception e) {
            return new ApiResponse(false, "Error updating school status: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse updateSchoolDates(Integer schoolId, Map<String, Object> dateRequest) {
        try {
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            // Extract dates from request
            LocalDate startDate = null;
            LocalDate endDate = null;

            if (dateRequest.get("startDate") != null) {
                if (dateRequest.get("startDate") instanceof String) {
                    startDate = LocalDate.parse((String) dateRequest.get("startDate"));
                } else if (dateRequest.get("startDate") instanceof LocalDate) {
                    startDate = (LocalDate) dateRequest.get("startDate");
                }
            }

            if (dateRequest.get("endDate") != null) {
                if (dateRequest.get("endDate") instanceof String) {
                    endDate = LocalDate.parse((String) dateRequest.get("endDate"));
                } else if (dateRequest.get("endDate") instanceof LocalDate) {
                    endDate = (LocalDate) dateRequest.get("endDate");
                }
            }

            // Validate dates
            if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
                return new ApiResponse(false, "End date cannot be before start date", null);
            }

            // Update dates
            school.setStartDate(startDate);
            school.setEndDate(endDate);
            school.setUpdatedBy((String) dateRequest.get("updatedBy"));
            school.setUpdatedDate(LocalDateTime.now());

            School updatedSchool = schoolRepository.save(school);

            return new ApiResponse(true, 
                    "School dates updated successfully for " + school.getSchoolName(), 
                    mapToResponse(updatedSchool));
        } catch (Exception e) {
            return new ApiResponse(false, "Error updating school dates: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getSchoolStatistics() {
        try {
            List<School> schools = schoolRepository.findAll();
            
            long totalSchools = schools.size();
            long activeSchools = schools.stream()
                    .mapToLong(school -> Boolean.TRUE.equals(school.getIsActive()) ? 1 : 0)
                    .sum();
            long inactiveSchools = totalSchools - activeSchools;
            
            long schoolsWithDates = schools.stream()
                    .mapToLong(school -> (school.getStartDate() != null && school.getEndDate() != null) ? 1 : 0)
                    .sum();
            
            long schoolsWithoutDates = totalSchools - schoolsWithDates;

            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalSchools", totalSchools);
            statistics.put("activeSchools", activeSchools);
            statistics.put("inactiveSchools", inactiveSchools);
            statistics.put("schoolsWithDates", schoolsWithDates);
            statistics.put("schoolsWithoutDates", schoolsWithoutDates);
            statistics.put("activationRate", totalSchools > 0 ? (double) activeSchools / totalSchools * 100 : 0);

            return new ApiResponse(true, "School statistics retrieved successfully", statistics);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving school statistics: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse searchSchools(String query) {
        try {
            List<School> schools = schoolRepository.findAll();
            
            // Filter schools by name, city, or state (case-insensitive)
            List<SchoolResponseDto> filteredSchools = schools.stream()
                    .filter(school -> 
                        (school.getSchoolName() != null && school.getSchoolName().toLowerCase().contains(query.toLowerCase())) ||
                        (school.getCity() != null && school.getCity().toLowerCase().contains(query.toLowerCase())) ||
                        (school.getState() != null && school.getState().toLowerCase().contains(query.toLowerCase()))
                    )
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("schools", filteredSchools);
            responseData.put("totalCount", filteredSchools.size());
            responseData.put("query", query);

            return new ApiResponse(true, "Search results retrieved successfully", responseData);
        } catch (Exception e) {
            return new ApiResponse(false, "Error searching schools: " + e.getMessage(), null);
        }
    }

    /**
     * Map School entity to SchoolResponseDto
     */
    private SchoolResponseDto mapToResponse(School school) {
        return SchoolResponseDto.builder()
                .schoolId(school.getSchoolId())
                .schoolCode(school.getSchoolCode())
                .schoolName(school.getSchoolName())
                .schoolType(school.getSchoolType())
                .affiliationBoard(school.getAffiliationBoard())
                .registrationNumber(school.getRegistrationNumber())
                .address(school.getAddress())
                .city(school.getCity())
                .district(school.getDistrict())
                .state(school.getState())
                .pincode(school.getPincode())
                .contactNo(school.getContactNo())
                .email(school.getEmail())
                .schoolPhoto(school.getSchoolPhoto())
                .isActive(school.getIsActive())
                .startDate(school.getStartDate())
                .endDate(school.getEndDate())
                .createdBy(school.getCreatedBy())
                .createdDate(school.getCreatedDate())
                .updatedBy(school.getUpdatedBy())
                .updatedDate(school.getUpdatedDate())
                .build();
    }
}
