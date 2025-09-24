package com.app.service.impl;

import com.app.entity.Role;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.LoginRequestDTO;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.UserResponseDto;
import com.app.payload.response.UserRoleResponseDto;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import com.app.service.IUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements IUserService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
    private RoleRepository roleRepository;
	@Autowired
    private UserRoleRepository userRoleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

    // ---------------- Register ----------------
    @Override
    public ApiResponse registerUser(UserRequestDto request) {
//        if (userRepository.existsByEmail(request.getEmail())) {
//            return new ApiResponse(false, "Email already registered", null);
//        }
        
        if (userRepository.existsByUserName(request.getUserName())) {
            return new ApiResponse(false, "Username already taken", null);
        }

        User user = User.builder()
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))    // ⚠ No encoding as per your instruction
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);

        return new ApiResponse(true, "User registered successfully", mapToResponse(saved));
    }

    // ---------------- Login ----------------
//    @Override
//    public ApiResponse login(LoginRequestDTO request) {
//    	User user = userRepository.findByUserNameOrContactNumber(request.getLoginId(), request.getLoginId())
//    	        .orElseThrow(() -> new ResourceNotFoundException("User not found with: " + request.getLoginId()));
//
//        if (!user.getPassword().equals(request.getPassword())) {
//            return new ApiResponse(false, "Invalid credentials", null);
//        }
//
//        if (!user.getIsActive()) {
//            return new ApiResponse(false, "User account is inactive", null);
//        }
//
//        return new ApiResponse(true, "Login successful", mapToResponse(user));
//    }

    // ---------------- Update Profile ----------------
    @Override
    public ApiResponse updateUser(Integer userId, UserRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setUserName(request.getUserName());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
        user.setContactNumber(request.getContactNumber());
        user.setUpdatedBy(request.getUpdatedBy());
        user.setUpdatedDate(LocalDateTime.now());

        User updated = userRepository.save(user);

        return new ApiResponse(true, "User updated successfully", mapToResponse(updated));
    }

    // ---------------- Deactivate ----------------
    @Override
    public ApiResponse deactivateUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setIsActive(false);
        user.setUpdatedDate(LocalDateTime.now());

        userRepository.save(user);

        return new ApiResponse(true, "User deactivated successfully", null);
    }

    // ---------------- Get By Id ----------------
    @Override
    public ApiResponse getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return new ApiResponse(true, "User fetched successfully", mapToResponse(user));
    }

    // ---------------- Get All ----------------
    @Override
    public ApiResponse getAllUsers() {
        List<UserResponseDto> users = userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "All users fetched successfully", users);
    }

    // ---------------- Assign Role ----------------
    @Override
    public ApiResponse assignRoleToUser(Integer userId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .isActive(true)
                .createdBy("SYSTEM")
                .createdDate(LocalDateTime.now())
                .build();

        userRoleRepository.save(userRole);

        return new ApiResponse(true, "Role assigned to user successfully", null);
    }

    // ---------------- Remove Role ----------------
    @Override
    public ApiResponse removeRoleFromUser(Integer userRoleId) {
        UserRole userRole = userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole not found with ID: " + userRoleId));

        userRoleRepository.delete(userRole);

        return new ApiResponse(true, "Role removed from user successfully", null);
    }

    // ---------------- Get User Roles ----------------
    @Override
    public ApiResponse getUserRoles(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<UserRoleResponseDto> roles = userRoleRepository.findByUser(user).stream()
                .map(role -> UserRoleResponseDto.builder()
                        .userRoleId(role.getUserRoleId())
                        .userId(user.getUId())
                        .userName(user.getUserName())
                        .roleId(role.getRole().getRoleId())
                        .roleName(role.getRole().getRoleName())
                        .isActive(role.getIsActive())
                        .createdBy(role.getCreatedBy())
                        .createdDate(role.getCreatedDate())
                        .updatedBy(role.getUpdatedBy())
                        .updatedDate(role.getUpdatedDate())
                        .build())
                .collect(Collectors.toList());

        return new ApiResponse(true, "User roles fetched successfully", roles);
    }

    // ---------------- Mapper ----------------
    private UserResponseDto mapToResponse(User user) {
        return UserResponseDto.builder()
                .uId(user.getUId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .isActive(user.getIsActive())
                .createdBy(user.getCreatedBy())
                .createdDate(user.getCreatedDate())
                .updatedBy(user.getUpdatedBy())
                .updatedDate(user.getUpdatedDate())
                .build();
    }
}
