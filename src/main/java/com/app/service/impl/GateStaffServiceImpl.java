package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.entity.Role;
import com.app.entity.School;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.repository.RoleRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import com.app.service.IGateStaffService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class GateStaffServiceImpl implements IGateStaffService{
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private SchoolRepository schoolRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
    public ApiResponse registerGateStaff(UserRequestDto request) {
        // Check school
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));

        // Create user
        User user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .password(passwordEncoder.encode(request.getPassword())) // later encode with BCrypt
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();
        User savedUser = userRepository.save(user);

        // Assign Gate Staff role
        Role role = roleRepository.findByRoleName("GATE_STAFF")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(role)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();
        userRoleRepository.save(userRole);

        return new ApiResponse(true, "Gate Staff registered successfully", savedUser.getUId());
    }

    @Override
    public ApiResponse getAllGateStaff(Integer schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));

        List<User> staffList = userRepository.findAll(); // later filter by school
        List<String> names = staffList.stream().map(User::getUserName).collect(Collectors.toList());

        return new ApiResponse(true, "Gate Staff fetched successfully", names);
    }

}
