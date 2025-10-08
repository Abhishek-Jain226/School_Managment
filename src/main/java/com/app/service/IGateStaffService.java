package com.app.service;

import com.app.payload.response.ApiResponse;

public interface IGateStaffService {

    // Get gate staff dashboard data
    ApiResponse getGateStaffDashboard(Integer userId);

    // Get students by trip for gate staff
    ApiResponse getStudentsByTrip(Integer userId, Integer tripId);

    // Mark gate entry
    ApiResponse markGateEntry(Integer userId, Integer studentId, Integer tripId, String remarks);

    // Mark gate exit
    ApiResponse markGateExit(Integer userId, Integer studentId, Integer tripId, String remarks);

    // Get recent dispatch logs for gate staff
    ApiResponse getRecentDispatchLogs(Integer userId);

    // Get gate staff by user ID
    ApiResponse getGateStaffByUserId(Integer userId);
}