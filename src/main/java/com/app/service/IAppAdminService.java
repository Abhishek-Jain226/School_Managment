package com.app.service;

import com.app.payload.response.ApiResponse;

public interface IAppAdminService {
    
    ApiResponse getDashboard();
    
    ApiResponse getProfile();
    
    ApiResponse getSystemStats();
    
    ApiResponse getReports(String startDate, String endDate);
}
