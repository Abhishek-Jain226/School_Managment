package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.request.DispatchLogRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IDispatchLogService;
import com.app.service.IGateStaffService;

@RestController
@RequestMapping("/api/gate-staff")
public class GateStaffController {
	
	@Autowired
	private IDispatchLogService dispatchLogService;
	
	@Autowired
	private IGateStaffService gateStaffService;
	
	 // ----------- Mark Gate Entry  -----------
    @PostMapping("/mark-entry")
    public ResponseEntity<ApiResponse> markGateEntry(@RequestBody DispatchLogRequestDto request) {
        request.setEventType(com.app.Enum.EventType.GATE_ENTRY);
        return ResponseEntity.ok(dispatchLogService.createDispatchLog(request));
    }

    // ----------- Mark Gate Exit -----------
    @PostMapping("/mark-exit")
    public ResponseEntity<ApiResponse> markGateExit(@RequestBody DispatchLogRequestDto request) {
        request.setEventType(com.app.Enum.EventType.GATE_EXIT);
        return ResponseEntity.ok(dispatchLogService.createDispatchLog(request));
    }
    
    // -------- Register Gate Staff --------
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerGateStaff(@RequestBody UserRequestDto request) {
        return ResponseEntity.ok(gateStaffService.registerGateStaff(request));
    }

    // -------- Get All Gate Staff by School --------
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<ApiResponse> getAllGateStaff(@PathVariable Integer schoolId) {
        return ResponseEntity.ok(gateStaffService.getAllGateStaff(schoolId));
    }

}
