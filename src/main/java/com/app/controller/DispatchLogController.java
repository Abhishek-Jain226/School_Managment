package com.app.controller;

import com.app.payload.request.DispatchLogRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IDispatchLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch-logs")
public class DispatchLogController {

	@Autowired
    private IDispatchLogService dispatchLogService;

    // ----------- Create Dispatch Log -----------
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createDispatchLog(@RequestBody DispatchLogRequestDto request) {
        return ResponseEntity.ok(dispatchLogService.createDispatchLog(request));
    }

    // ----------- Update Dispatch Log -----------
    @PutMapping("/{dispatchLogId}")
    public ResponseEntity<ApiResponse> updateDispatchLog(
            @PathVariable Integer dispatchLogId,
            @RequestBody DispatchLogRequestDto request) {
        return ResponseEntity.ok(dispatchLogService.updateDispatchLog(dispatchLogId, request));
    }

    // ----------- Get Dispatch Log By Id -----------
    @GetMapping("/{dispatchLogId}")
    public ResponseEntity<ApiResponse> getDispatchLogById(@PathVariable Integer dispatchLogId) {
        return ResponseEntity.ok(dispatchLogService.getDispatchLogById(dispatchLogId));
    }

    // ----------- Get Dispatch Logs By Trip -----------
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<ApiResponse> getDispatchLogsByTrip(@PathVariable Integer tripId) {
        return ResponseEntity.ok(dispatchLogService.getDispatchLogsByTrip(tripId));
    }

    // ----------- Get Dispatch Logs By Vehicle -----------
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<ApiResponse> getDispatchLogsByVehicle(@PathVariable Integer vehicleId) {
        return ResponseEntity.ok(dispatchLogService.getDispatchLogsByVehicle(vehicleId));
    }
}
