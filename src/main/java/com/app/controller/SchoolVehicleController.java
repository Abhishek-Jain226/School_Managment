package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.request.SchoolVehicleRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IVehicleService;

@RestController
@RequestMapping("/api/school-vehicles")
public class SchoolVehicleController {

	@Autowired
	private IVehicleService vehicleService;

	// 🔹 Assign Vehicle to School
	@PostMapping("/assign")
	public ResponseEntity<ApiResponse> assignVehicleToSchool(@RequestBody SchoolVehicleRequestDto request) {
		return ResponseEntity.ok(vehicleService.assignVehicleToSchool(request));
	}

}
