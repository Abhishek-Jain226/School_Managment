package com.app.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.app.entity.PendingUser;
import com.app.entity.School;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.SchoolResponseDto;
import com.app.repository.PendingUserRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolUserRepository;
import com.app.service.IAppAdminSchoolService;

import jakarta.mail.internet.MimeMessage;

@Service
public class AppAdminSchoolServiceImpl implements IAppAdminSchoolService {

    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private PendingUserRepository pendingUserRepository;
    
    @Autowired
    private SchoolUserRepository schoolUserRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.frontend.activation-url}")
    private String activationBaseUrl;

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

    @Override
    public ApiResponse resendActivationLink(Integer schoolId, String updatedBy) {
        try {
            // Find the school
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            // Find pending users for this school that are not used
            List<PendingUser> pendingUsers = pendingUserRepository
                    .findByEntityTypeAndEntityIdAndIsUsedFalse("SCHOOL", schoolId.longValue());

            if (pendingUsers.isEmpty()) {
                return new ApiResponse(false, "No pending activation found for this school", null);
            }

            // Get the most recent pending user (should be only one for school)
            PendingUser pendingUser = pendingUsers.get(0);

            // Check if the current token is still valid (not expired)
            if (pendingUser.getTokenExpiry().isAfter(LocalDateTime.now())) {
                // Token is still valid, just resend the email
                String activationUrl = activationBaseUrl + "?token=" + pendingUser.getToken();
                sendActivationEmail(pendingUser.getEmail(), activationUrl, school.getSchoolName());
                
                return new ApiResponse(true, 
                        "Activation link resent successfully to " + pendingUser.getEmail(), 
                        Map.of("email", pendingUser.getEmail(), "expiresAt", pendingUser.getTokenExpiry()));
            } else {
                // Token is expired, generate new token
                pendingUser.setToken(UUID.randomUUID().toString());
                pendingUser.setTokenExpiry(LocalDateTime.now().plusDays(1)); // 24 hours from now
                pendingUser.setUpdatedBy(updatedBy);
                pendingUser.setUpdatedDate(LocalDateTime.now());
                
                PendingUser updatedPendingUser = pendingUserRepository.save(pendingUser);
                
                String activationUrl = activationBaseUrl + "?token=" + updatedPendingUser.getToken();
                sendActivationEmail(updatedPendingUser.getEmail(), activationUrl, school.getSchoolName());
                
                return new ApiResponse(true, 
                        "New activation link generated and sent to " + updatedPendingUser.getEmail(), 
                        Map.of("email", updatedPendingUser.getEmail(), "expiresAt", updatedPendingUser.getTokenExpiry()));
            }
        } catch (Exception e) {
            return new ApiResponse(false, "Error resending activation link: " + e.getMessage(), null);
        }
    }

    private void sendActivationEmail(String to, String activationUrl, String schoolName) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background-color: #1E3A8A; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                        <h2 style="margin: 0;">Welcome to Kids Vehicle Tracking System</h2>
                    </div>
                    <div style="background-color: #f8f9fa; padding: 30px; border-radius: 0 0 8px 8px;">
                        <p style="font-size: 16px; color: #333;">Thank you for registering %s with us!</p>
                        <p style="color: #666;">To complete your registration and activate your school admin account, please click the button below:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #1E3A8A; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">Activate Account</a>
                        </div>
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <p style="margin: 0; color: #856404;"><strong>Important:</strong> This link is valid for 24 hours only.</p>
                        </div>
                        <p style="color: #666; font-size: 14px;">If you didn't request this registration, please ignore this email.</p>
                        <hr style="margin: 30px 0; border: none; border-top: 1px solid #dee2e6;">
                        <p style="color: #6c757d; font-size: 12px; text-align: center; margin: 0;">Kids Vehicle Tracking System - School Management</p>
                    </div>
                </div>
                """.formatted(schoolName, activationUrl);

            helper.setText(html, true);
            helper.setTo(to);
            helper.setSubject("Activate Your School Account - Kids Vehicle Tracking");
            helper.setFrom("noreply@kidsvt.com");
            mailSender.send(msg);

            System.out.println("Activation email sent successfully to: " + to);
        } catch (Exception ex) {
            System.err.println("Failed to send activation email to: " + to);
            ex.printStackTrace();
        }
    }

    /**
     * Map School entity to SchoolResponseDto
     */
    private SchoolResponseDto mapToResponse(School school) {
        // Check if school has any active users
        long activeUserCount = schoolUserRepository.countBySchoolAndIsActive(school, true);
        boolean hasActiveUser = activeUserCount > 0;
        
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
                .hasActiveUser(hasActiveUser)
                .startDate(school.getStartDate())
                .endDate(school.getEndDate())
                .createdBy(school.getCreatedBy())
                .createdDate(school.getCreatedDate())
                .updatedBy(school.getUpdatedBy())
                .updatedDate(school.getUpdatedDate())
                .build();
    }
}
