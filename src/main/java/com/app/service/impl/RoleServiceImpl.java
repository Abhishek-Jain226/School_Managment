package com.app.service.impl;

import com.app.entity.Role;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.RoleRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.RoleResponseDto;
import com.app.repository.RoleRepository;
import com.app.service.IRoleService;

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
public class RoleServiceImpl implements IRoleService {

	@Autowired
    private RoleRepository roleRepository;

    @Override
    public ApiResponse createRole(RoleRequestDto request) {
        Role role = Role.builder()
                .roleName(request.getRoleName())
                .description(request.getDescription())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .isActive(true)
                .build();

        Role saved = roleRepository.save(role);

        return new ApiResponse(true, "Role created successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse updateRole(Integer roleId, RoleRequestDto request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setUpdatedBy(request.getUpdatedBy());
        role.setUpdatedDate(LocalDateTime.now());

        Role updated = roleRepository.save(role);

        return new ApiResponse(true, "Role updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse deleteRole(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        roleRepository.delete(role);

        return new ApiResponse(true, "Role deleted successfully", null);
    }

    @Override
    public ApiResponse getRoleById(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        return new ApiResponse(true, "Role fetched successfully", mapToResponse(role));
    }

    @Override
    public ApiResponse getAllRoles() {
        List<RoleResponseDto> roles = roleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "All roles fetched successfully", roles);
    }

    // ----------------- Private Mapper -----------------
    private RoleResponseDto mapToResponse(Role role) {
        return RoleResponseDto.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .description(role.getDescription())
                .isActive(role.getIsActive())
                .createdBy(role.getCreatedBy())
                .createdDate(role.getCreatedDate())
                .updatedBy(role.getUpdatedBy())
                .updatedDate(role.getUpdatedDate())
                .build();
    }
}
