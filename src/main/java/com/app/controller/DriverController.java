package com.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.payload.request.DriverRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IDriverService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

	@Autowired
	private IDriverService driverService;

	//  Create Driver
	@PostMapping("/create")
	public ResponseEntity<ApiResponse> createDriver(@Valid @RequestBody DriverRequestDto requestDto) {
		ApiResponse response = driverService.createDriver(requestDto);
		return ResponseEntity.status(response.isSuccess() ? 201 : 400).body(response);
	}

}
