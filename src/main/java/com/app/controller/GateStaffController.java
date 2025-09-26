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
//    @GetMapping("/students/{schoolId}")
//    public ResponseEntity<ApiResponse> getAssignedStudents(@PathVariable Integer schoolId) {
//        return ResponseEntity.ok(gateStaffService.getAssignedStudents(schoolId));
//    }
//
//    @PostMapping("/entry")
//    public ResponseEntity<ApiResponse> markGateEntry(@RequestBody GateEventRequestDto request) {
//        return ResponseEntity.ok(gateStaffService.markGateEntry(request));
//    }
//
//    @PostMapping("/exit")
//    public ResponseEntity<ApiResponse> markGateExit(@RequestBody GateEventRequestDto request) {
//        return ResponseEntity.ok(gateStaffService.markGateExit(request));
//    }
//}
