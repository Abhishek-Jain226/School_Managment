package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.entity.PasswordResetToken;
import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.User;
import com.app.entity.VehicleOwner;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.ForgotPasswordRequest;
import com.app.payload.request.LoginRequestDTO;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.ResetPasswordRequest;
import com.app.repository.PasswordResetTokenRepository;
import com.app.repository.SchoolUserRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import com.app.repository.VehicleOwnerRepository;
import com.app.security.JwtUtil;
import com.app.service.IAuthService;

import jakarta.mail.internet.MimeMessage;

@Service
public class AuthServiceImpl implements IAuthService{
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
    private PasswordResetTokenRepository tokenRepository;
	@Autowired
    private JavaMailSender mailSender;
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private SchoolUserRepository schoolUserRepository;
	
	@Autowired
	private VehicleOwnerRepository ownerRepository;
	
	
	@Override
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByUserNameOrContactNumberOrEmail(
                        request.getLoginId(), request.getLoginId(), request.getLoginId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Generate OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        PasswordResetToken token = PasswordResetToken.builder()
                .loginId(request.getLoginId())
                .otp(otp)
                .expiry(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build();
        tokenRepository.save(token);

        sendOtpMail(user.getEmail(), otp);

        return new ApiResponse(true, "OTP sent to registered email.", Map.of("loginId", request.getLoginId()));
    }
	
	private void sendOtpMail(String to, String otp) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(to);
            helper.setSubject("Password Reset OTP");
            helper.setText("<p>Your OTP for password reset is <b>" + otp + "</b>. It is valid for 10 minutes.</p>", true);
            mailSender.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
    public ApiResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = tokenRepository.findByLoginIdAndOtpAndUsedFalse(request.getLoginId(), request.getOtp())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid OTP"));

        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            return new ApiResponse(false, "OTP expired", null);
        }

        User user = userRepository.findByUserNameOrContactNumberOrEmail(
                        request.getLoginId(), request.getLoginId(), request.getLoginId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        token.setUsed(true);
        tokenRepository.save(token);

        return new ApiResponse(true, "Password reset successful", null);
    }

	@Override
	public ApiResponse login(LoginRequestDTO request) {
	   
	    User user = userRepository.findByUserNameOrContactNumber(
	            request.getLoginId(), request.getLoginId()
	    ).orElseThrow(() -> new BadCredentialsException("Invalid username or mobile"));

	    // Password verify
	    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	        throw new BadCredentialsException("Bad credentials");
	    }

	    // Roles 
	    var roles = userRoleRepository.findByUser(user).stream()
	            .map(ur -> ur.getRole().getRoleName())
	            .collect(Collectors.toList());

	    // JWT token generate
	    String token = jwtUtil.generateToken(user.getUserName(), roles);
	    
	 // 
	    Integer schoolId = null;
	    String schoolName = null;
	    Integer ownerId = null;
	    Optional<SchoolUser> schoolUserOpt = schoolUserRepository.findByUser(user);
	    if (schoolUserOpt.isPresent()) {
	        School school = schoolUserOpt.get().getSchool();
	        schoolId = school.getSchoolId();
	        schoolName = school.getSchoolName();
	    }
	    // ✅ Agar Vehicle Owner hai
	    if (roles.contains("VEHICLE_OWNER")) {
	        VehicleOwner owner = ownerRepository.findByUser(user)
	                .orElseThrow(() -> new ResourceNotFoundException("Owner not found for user: " + user.getUserName()));
	        ownerId = owner.getOwnerId();
	       
	        if (schoolUserOpt.isPresent()) {
	            School school = schoolUserOpt.get().getSchool();
	            schoolId = school.getSchoolId();
	            schoolName = school.getSchoolName();
	        }
	    }

	    Map<String, Object> data = new HashMap<>();
	    data.put("token", token);
	    data.put("userId", user.getUId());
	    data.put("userName", user.getUserName());
	    data.put("email", user.getEmail());
	    data.put("roles", roles);
	    data.put("schoolId", schoolId);
	    data.put("schoolName", schoolName);
	    data.put("ownerId", ownerId); 

	    return new ApiResponse(true, "Login successful", data);

	}

}
