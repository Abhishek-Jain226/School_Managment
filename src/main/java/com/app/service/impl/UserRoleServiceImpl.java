package com.app.service.impl;

import com.app.entity.Role;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.UserRoleRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.UserRoleResponseDto;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import com.app.service.IUserRoleService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleServiceImpl implements IUserRoleService {

	@Autowired
    private UserRoleRepository userRoleRepository;
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private RoleRepository roleRepository;

    @Override
    public ApiResponse assignUserRole(UserRoleRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        UserRole saved = userRoleRepository.save(userRole);

        return new ApiResponse(true, "User role assigned successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse updateUserRole(Integer userRoleId, UserRoleRequestDto request) {
        UserRole userRole = userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole not found with ID: " + userRoleId));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));

        userRole.setRole(role);
        userRole.setUpdatedBy(request.getUpdatedBy());
        userRole.setUpdatedDate(LocalDateTime.now());

        UserRole updated = userRoleRepository.save(userRole);

        return new ApiResponse(true, "User role updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse removeUserRole(Integer userRoleId) {
        UserRole userRole = userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole not found with ID: " + userRoleId));

        userRoleRepository.delete(userRole);

        return new ApiResponse(true, "User role removed successfully", null);
    }

    @Override
    public ApiResponse getUserRolesByUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<UserRoleResponseDto> roles = userRoleRepository.findByUser(user).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "User roles fetched successfully", roles);
    }

    @Override
    public ApiResponse getAllUserRoles() {
        List<UserRoleResponseDto> roles = userRoleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "All user roles fetched successfully", roles);
    }

    // ------------------ Private Mapper ------------------
    private UserRoleResponseDto mapToResponse(UserRole userRole) {
        return UserRoleResponseDto.builder()
                .userRoleId(userRole.getUserRoleId())
                .userId(userRole.getUser().getUId())
                .userName(userRole.getUser().getUserName())
                .roleId(userRole.getRole().getRoleId())
                .roleName(userRole.getRole().getRoleName())
                .isActive(userRole.getIsActive())
                .createdBy(userRole.getCreatedBy())
                .createdDate(userRole.getCreatedDate())
                .updatedBy(userRole.getUpdatedBy())
                .updatedDate(userRole.getUpdatedDate())
                .build();
    }
}
