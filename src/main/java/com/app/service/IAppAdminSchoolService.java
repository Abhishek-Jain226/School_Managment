package com.app.service;

import java.util.Map;

import com.app.payload.response.ApiResponse;

public interface IAppAdminSchoolService {
    
    /**
     * Get all schools with complete details for AppAdmin
     */
    ApiResponse getAllSchools();
    
    /**
     * Get school details by ID
     */
    ApiResponse getSchoolById(Integer schoolId);
    
    /**
     * Update school active/inactive status
     */
    ApiResponse updateSchoolStatus(Integer schoolId, Boolean isActive, String updatedBy);
    
    /**
     * Update school start and end dates
     */
    ApiResponse updateSchoolDates(Integer schoolId, Map<String, Object> dateRequest);
    
    /**
     * Get school statistics for AppAdmin dashboard
     */
    ApiResponse getSchoolStatistics();
    
    /**
     * Search schools by name, city, or state
     */
    ApiResponse searchSchools(String query);
    
    /**
     * Resend activation link for school admin
     */
    ApiResponse resendActivationLink(Integer schoolId, String updatedBy);
}
