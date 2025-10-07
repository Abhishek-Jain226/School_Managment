//package com.app.controller;
//
//import com.app.payload.request.GateEventRequestDto;
//import com.app.payload.response.ApiResponse;
//import com.app.service.IGateStaffService;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/gate-staff")
//public class GateStaffController {
//
//    @Autowired
//    private IGateStaffService gateStaffService;
//
//    // Get gate staff dashboard data
//    @GetMapping("/dashboard/{gateStaffId}")
//    public ResponseEntity<ApiResponse> getGateStaffDashboard(@PathVariable Integer gateStaffId) {
//        return ResponseEntity.ok(gateStaffService.getGateStaffDashboard(gateStaffId));
//    }
//
//    // Get students by trip for gate staff
//    @GetMapping("/{gateStaffId}/students")
//    public ResponseEntity<ApiResponse> getAssignedStudents(@PathVariable Integer gateStaffId) {
//        return ResponseEntity.ok(gateStaffService.getAssignedStudents(gateStaffId));
//    }
//
//    // Get students by specific trip
//    @GetMapping("/{gateStaffId}/trip/{tripId}/students")
//    public ResponseEntity<ApiResponse> getStudentsByTrip(@PathVariable Integer gateStaffId, @PathVariable Integer tripId) {
//        return ResponseEntity.ok(gateStaffService.getStudentsByTrip(gateStaffId, tripId));
//    }
//
//    // Mark gate entry
//    @PostMapping("/{gateStaffId}/entry")
//    public ResponseEntity<ApiResponse> markGateEntry(@PathVariable Integer gateStaffId, @RequestBody GateEventRequestDto request) {
//        return ResponseEntity.ok(gateStaffService.markGateEntry(gateStaffId, request));
//    }
//
//    // Mark gate exit
//    @PostMapping("/{gateStaffId}/exit")
//    public ResponseEntity<ApiResponse> markGateExit(@PathVariable Integer gateStaffId, @RequestBody GateEventRequestDto request) {
//        return ResponseEntity.ok(gateStaffService.markGateExit(gateStaffId, request));
//    }
//
//    // Get recent dispatch logs
//    @GetMapping("/{gateStaffId}/dispatch-logs")
//    public ResponseEntity<ApiResponse> getRecentDispatchLogs(@PathVariable Integer gateStaffId) {
//        return ResponseEntity.ok(gateStaffService.getRecentDispatchLogs(gateStaffId));
//    }
//}
