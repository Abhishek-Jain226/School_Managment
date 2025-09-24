package com.app.controller;

import com.app.payload.request.LoginRequestDTO;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.service.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private IUserService userService;

	// ----------- Registration -----------
	@PostMapping("/register")
	public ResponseEntity<ApiResponse> registerUser(@RequestBody UserRequestDto request) {
		return ResponseEntity.ok(userService.registerUser(request));
	}

	// ----------- Login -----------
//	@PostMapping("/login")
//	public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDTO request) {
//		return ResponseEntity.ok(userService.login(request));
//	}

	// ----------- Update Profile -----------
	@PutMapping("/{userId}")
	public ResponseEntity<ApiResponse> updateUser(@PathVariable Integer userId, @RequestBody UserRequestDto request) {
		return ResponseEntity.ok(userService.updateUser(userId, request));
	}

	// ----------- Deactivate -----------
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Integer userId) {
		return ResponseEntity.ok(userService.deactivateUser(userId));
	}

	// ----------- Get User By Id -----------
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse> getUserById(@PathVariable Integer userId) {
		return ResponseEntity.ok(userService.getUserById(userId));
	}

	// ----------- Get All Users -----------
	@GetMapping
	public ResponseEntity<ApiResponse> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	// ----------- Assign Role -----------
	@PostMapping("/{userId}/roles/{roleId}")
	public ResponseEntity<ApiResponse> assignRoleToUser(@PathVariable Integer userId, @PathVariable Integer roleId) {
		return ResponseEntity.ok(userService.assignRoleToUser(userId, roleId));
	}

	// ----------- Remove Role -----------
	@DeleteMapping("/roles/{userRoleId}")
	public ResponseEntity<ApiResponse> removeRoleFromUser(@PathVariable Integer userRoleId) {
		return ResponseEntity.ok(userService.removeRoleFromUser(userRoleId));
	}

	// ----------- Get Roles By User -----------
	@GetMapping("/{userId}/roles")
	public ResponseEntity<ApiResponse> getUserRoles(@PathVariable Integer userId) {
		return ResponseEntity.ok(userService.getUserRoles(userId));
	}
}
