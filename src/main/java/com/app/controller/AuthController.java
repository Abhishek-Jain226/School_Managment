package com.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.User;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.ForgotPasswordRequest;
import com.app.payload.request.LoginRequestDTO;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.ResetPasswordRequest;
import com.app.repository.SchoolUserRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import com.app.security.JwtUtil;
import com.app.service.IAuthService;
import com.app.service.IPendingUserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private IPendingUserService pendingUserService;
	
	@Autowired
	private IAuthService authService;
	
	@Autowired
	private SchoolUserRepository schoolUserRepository;
	
	
	// ---------- LOGIN ----------
//	@PostMapping("/login")
//	public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDTO request) {
//	   
//	    String loginId = request.getLoginId();
//
//
//	    Authentication authentication = authenticationManager.authenticate(
//	            new UsernamePasswordAuthenticationToken(loginId, request.getPassword())
//	    );
//
//	    
//	    User user = userRepository.findByUserNameOrContactNumber(loginId, loginId)
//	            .orElseThrow(() -> new RuntimeException("User not found with: " + loginId));
//
//	    var roles = userRoleRepository.findByUser(user).stream()
//	            .map(ur -> ur.getRole().getRoleName())
//	            .collect(Collectors.toList());
//
//	    String token = jwtUtil.generateToken(user.getUserName(), roles);
//
//	    return ResponseEntity.ok(new ApiResponse(true, "Login successful",
//	            Map.of("token", token, "userId", user.getUId(), "roles", roles)));
//	}

	
	// ---------- LOGIN ----------
	@PostMapping("/login")
	public ResponseEntity<ApiResponse> login(@RequestBody LoginRequestDTO request) {
	    return ResponseEntity.ok(authService.login(request));
	}

    // ---------- COMPLETE REGISTRATION ----------
    @PostMapping("/complete-registration")
    public ResponseEntity<ApiResponse> completeRegistration(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String password = body.get("password");
        String userName = body.get("userName");
        return ResponseEntity.ok(pendingUserService.completeRegistration(token, password, userName));
    }
    
 // ---------- FORGOT PASSWORD ----------
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    // ---------- RESET PASSWORD ----------
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
