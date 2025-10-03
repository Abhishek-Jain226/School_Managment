package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entity.Role;
import com.app.entity.School;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.SchoolRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.SchoolResponseDto;
import com.app.repository.RoleRepository;
import com.app.repository.SchoolRepository;
import com.app.service.IPendingUserService;
import com.app.service.ISchoolService;

@Service
public class SchoolServiceImpl implements ISchoolService {

    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private IPendingUserService pendingUserService;
    
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public ApiResponse registerSchool(SchoolRequestDto request) {
        // Check for duplicate email
        if (schoolRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "School with this email already exists", null);
        }
        
        // Check for duplicate registration number
        if (schoolRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            return new ApiResponse(false, "School with this registration number already exists", null);
        }
        
        // Generate unique school code
        String schoolCode = "SCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        School school = School.builder()
                .schoolCode(schoolCode)
                .schoolName(request.getSchoolName())
                .schoolType(request.getSchoolType())
                .affiliationBoard(request.getAffiliationBoard())
                .registrationNumber(request.getRegistrationNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .district(request.getDistrict())
                .state(request.getState())
                .pincode(request.getPincode())
                .contactNo(request.getContactNo())
                .email(request.getEmail())
                .schoolPhoto(request.getSchoolPhoto())
                .isActive(false) // new school inactive by default
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        School saved = schoolRepository.save(school);
     // -------- Get Role for School Admin --------
        Role schoolAdminRole = roleRepository.findByRoleName("SCHOOL_ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role SCHOOL_ADMIN not found"));

        // -------- Create Pending User --------
        com.app.payload.request.PendingUserRequestDTO pendingUserReq =
                com.app.payload.request.PendingUserRequestDTO.builder()
                        .entityType("SCHOOL")
                        .entityId(saved.getSchoolId().longValue())
                        .email(saved.getEmail())
                        .contactNumber(saved.getContactNo())
                        .roleId(schoolAdminRole.getRoleId())
                        .createdBy(request.getCreatedBy())
                        .build();

        ApiResponse pendingUserResponse = pendingUserService.createPendingUser(pendingUserReq);

        return new ApiResponse(true, "School registered successfully. Activation link sent to email.",
                Map.of("schoolId", saved.getSchoolId(), "pendingUser", pendingUserResponse.getData()));
    }

    @Override
    public ApiResponse activateSchool(Integer schoolId, String activationCode) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        // Simple check: match with existing schoolCode
        if (!school.getSchoolCode().equalsIgnoreCase(activationCode)) {
            return new ApiResponse(false, "Invalid activation code", null);
        }

        school.setIsActive(true);
        school.setUpdatedDate(LocalDateTime.now());

        School updated = schoolRepository.save(school);

        return new ApiResponse(true, "School activated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse activateSchoolAccount(Integer schoolId, String activationToken) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        // Verify activation token from pending user
        try {
            ApiResponse tokenResponse = pendingUserService.verifyPendingUser(activationToken);
            if (!tokenResponse.isSuccess()) {
                return new ApiResponse(false, "Invalid activation token", null);
            }
            
            // Check if token is for this school
            Map<String, Object> tokenData = (Map<String, Object>) tokenResponse.getData();
            if (!"SCHOOL".equalsIgnoreCase((String) tokenData.get("entityType")) ||
                !schoolId.equals(((Number) tokenData.get("entityId")).intValue())) {
                return new ApiResponse(false, "Token does not match this school", null);
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Invalid activation token", null);
        }

        school.setIsActive(true);
        school.setUpdatedDate(LocalDateTime.now());
        School updated = schoolRepository.save(school);

        return new ApiResponse(true, "School account activated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse updateSchool(Integer schoolId, SchoolRequestDto request) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        school.setSchoolName(request.getSchoolName());
        school.setSchoolType(request.getSchoolType());
        school.setAffiliationBoard(request.getAffiliationBoard());
        school.setRegistrationNumber(request.getRegistrationNumber());
        school.setAddress(request.getAddress());
        school.setCity(request.getCity());
        school.setDistrict(request.getDistrict());
        school.setState(request.getState());
        school.setPincode(request.getPincode());
        school.setContactNo(request.getContactNo());
        school.setEmail(request.getEmail());
        school.setSchoolPhoto(request.getSchoolPhoto());
        school.setUpdatedBy(request.getUpdatedBy());
        school.setUpdatedDate(LocalDateTime.now());

        School updated = schoolRepository.save(school);

        return new ApiResponse(true, "School updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse deleteSchool(Integer schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        schoolRepository.delete(school);

        return new ApiResponse(true, "School deleted successfully", null);
    }

    @Override
    public ApiResponse getSchoolById(Integer schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        return new ApiResponse(true, "School fetched successfully", mapToResponse(school));
    }

    @Override
    public ApiResponse getAllSchools() {
        List<SchoolResponseDto> schools = schoolRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "All schools fetched successfully", schools);
    }

    // Mapper
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
                .createdBy(school.getCreatedBy())
                .createdDate(school.getCreatedDate())
                .updatedBy(school.getUpdatedBy())
                .updatedDate(school.getUpdatedDate())
                .build();
    }
}
