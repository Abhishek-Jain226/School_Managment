package com.app.service;

import com.app.payload.response.ApiResponse;

public interface IReportService {

    ApiResponse getAttendanceReport(Integer schoolId, String filterType); // student-wise, class-wise

    ApiResponse getDispatchLogsReport(Integer schoolId, String filterType); // trip-wise, vehicle-wise

    ApiResponse getNotificationLogsReport(Integer schoolId, String filterType); // sent, failed, pending

    ApiResponse exportReport(Integer schoolId, String type, String format); // PDF/CSV

    byte[] generateReportFile(Integer schoolId, String type, String format); // Generate actual file content

}
