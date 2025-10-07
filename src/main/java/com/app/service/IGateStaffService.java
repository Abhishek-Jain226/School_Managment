//package com.app.service;
//
//import com.app.payload.request.GateEventRequestDto;
//import com.app.payload.response.ApiResponse;
//
//public interface IGateStaffService {
//
//	// Get gate staff dashboard data
//	ApiResponse getGateStaffDashboard(Integer gateStaffId);
//
//	// Get assigned students for gate staff
//	ApiResponse getAssignedStudents(Integer gateStaffId);
//
//	// Get students by specific trip
//	ApiResponse getStudentsByTrip(Integer gateStaffId, Integer tripId);
//
//	// Mark gate entry
//	ApiResponse markGateEntry(Integer gateStaffId, GateEventRequestDto request);
//
//	// Mark gate exit
//	ApiResponse markGateExit(Integer gateStaffId, GateEventRequestDto request);
//
//	// Get recent dispatch logs
//	ApiResponse getRecentDispatchLogs(Integer gateStaffId);
//
//}
