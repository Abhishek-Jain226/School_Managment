package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.User;
import com.app.repository.UserRepository;
import com.app.service.IAppAdminService;
import com.app.payload.response.ApiResponse;

@Service
@Transactional
public class AppAdminServiceImpl implements IAppAdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ApiResponse getDashboard() {
        try {
            // Get basic dashboard statistics
            Map<String, Object> dashboardData = new HashMap<>();
            
            // Get total users count
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.findAll().stream()
                    .filter(user -> user.getIsActive() != null && user.getIsActive())
                    .count();
            
            dashboardData.put("totalUsers", totalUsers);
            dashboardData.put("activeUsers", activeUsers);
            dashboardData.put("inactiveUsers", totalUsers - activeUsers);
            dashboardData.put("lastUpdated", LocalDateTime.now());
            
            // Add system info
            dashboardData.put("systemVersion", "1.0.0");
            dashboardData.put("environment", "development");
            
            return new ApiResponse(true, "Dashboard data retrieved successfully", dashboardData);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving dashboard: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getProfile() {
        try {
            // Get AppAdmin user (assuming user ID 1 is AppAdmin)
            User appAdmin = userRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("AppAdmin user not found"));
            
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("userId", appAdmin.getUId());
            profileData.put("userName", appAdmin.getUserName());
            profileData.put("email", appAdmin.getEmail());
            profileData.put("contactNumber", appAdmin.getContactNumber());
            profileData.put("isActive", appAdmin.getIsActive());
            profileData.put("createdDate", appAdmin.getCreatedDate());
            profileData.put("updatedDate", appAdmin.getUpdatedDate());
            
            return new ApiResponse(true, "Profile retrieved successfully", profileData);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving profile: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getSystemStats() {
        try {
            Map<String, Object> systemStats = new HashMap<>();
            
            // Get user statistics
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.findAll().stream()
                    .filter(user -> user.getIsActive() != null && user.getIsActive())
                    .count();
            
            systemStats.put("totalUsers", totalUsers);
            systemStats.put("activeUsers", activeUsers);
            systemStats.put("inactiveUsers", totalUsers - activeUsers);
            systemStats.put("lastUpdated", LocalDateTime.now());
            
            return new ApiResponse(true, "System stats retrieved successfully", systemStats);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving system stats: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getReports(String startDate, String endDate) {
        try {
            Map<String, Object> reportsData = new HashMap<>();
            
            // Basic reports structure
            reportsData.put("startDate", startDate);
            reportsData.put("endDate", endDate);
            reportsData.put("generatedAt", LocalDateTime.now());
            reportsData.put("reportType", "system_overview");
            
            // Add basic statistics
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.findAll().stream()
                    .filter(user -> user.getIsActive() != null && user.getIsActive())
                    .count();
            
            reportsData.put("totalUsers", totalUsers);
            reportsData.put("activeUsers", activeUsers);
            
            return new ApiResponse(true, "Reports generated successfully", reportsData);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error generating reports: " + e.getMessage(), null);
        }
    }
}
