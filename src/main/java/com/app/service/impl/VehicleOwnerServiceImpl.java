package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.Role;
import com.app.entity.User;
import com.app.entity.VehicleOwner;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.request.VehicleOwnerRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.VehicleOwnerResponseDto;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import com.app.repository.VehicleOwnerRepository;
import com.app.service.IPendingUserService;
import com.app.service.IVehicleOwnerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class VehicleOwnerServiceImpl implements IVehicleOwnerService {

	@Autowired
    private VehicleOwnerRepository vehicleOwnerRepository;
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private IPendingUserService pendingUserService;
	
	
    @Override
    public ApiResponse registerVehicleOwner(VehicleOwnerRequestDto request) {
    	 // 1. Check if user already exists by email
//        if (userRepository.existsByEmail(request.getEmail())) {
//            return new ApiResponse(false, "Email already registered", null);
//        }
    	
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        VehicleOwner owner = VehicleOwner.builder()
                .user(user)
                .name(request.getName())
                .contactNumber(request.getContactNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        VehicleOwner saved = vehicleOwnerRepository.save(owner);
        // Find OWNER role
        Role role = roleRepository.findByRoleName("VEHICLE_OWNER")
                .orElseThrow(() -> new ResourceNotFoundException("Role OWNER not found"));
        // Create PendingUser entry (activation email)
        PendingUserRequestDTO pendingReq = PendingUserRequestDTO.builder()
                .entityType("VEHICLE_OWNER")
                .entityId(saved.getOwnerId().longValue())
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .roleId(role.getRoleId())
                .createdBy(request.getCreatedBy())
                .build();

        pendingUserService.createPendingUser(pendingReq);

        return new ApiResponse(true,
                "Vehicle Owner registered successfully. Activation link sent to email.",
                saved.getOwnerId());
    }

    @Override
    public ApiResponse activateOwner(Integer ownerId, String activationCode) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

       
        User user = owner.getUser();
        user.setIsActive(true);
        user.setUpdatedDate(LocalDateTime.now());
        userRepository.save(user);

        return new ApiResponse(true, "Vehicle owner activated successfully", mapToResponse(owner));
    }

    @Override
    public ApiResponse updateVehicleOwner(Integer ownerId, VehicleOwnerRequestDto request) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
            owner.setUser(user);
        }

        owner.setName(request.getName());
        owner.setContactNumber(request.getContactNumber());
        owner.setAddress(request.getAddress());
        owner.setEmail(request.getEmail());
        owner.setUpdatedBy(request.getUpdatedBy());
        owner.setUpdatedDate(LocalDateTime.now());

        VehicleOwner updated = vehicleOwnerRepository.save(owner);

        return new ApiResponse(true, "Vehicle owner updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse deleteVehicleOwner(Integer ownerId) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

        vehicleOwnerRepository.delete(owner);

        return new ApiResponse(true, "Vehicle owner deleted successfully", null);
    }

    @Override
    public ApiResponse getVehicleOwnerById(Integer ownerId) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

        return new ApiResponse(true, "Vehicle owner fetched successfully", mapToResponse(owner));
    }

    @Override
    public ApiResponse getAllVehicleOwners(Integer schoolId) {
        // मान लेते हैं कि filter schoolId से user जुड़े होंगे
        List<VehicleOwnerResponseDto> owners = vehicleOwnerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Vehicle owners fetched successfully", owners);
    }

    // ------------------ Private Mapper ------------------
    private VehicleOwnerResponseDto mapToResponse(VehicleOwner owner) {
        return VehicleOwnerResponseDto.builder()
                .ownerId(owner.getOwnerId())
                .userId(owner.getUser().getUId())
                .name(owner.getName())
                .contactNumber(owner.getContactNumber())
                .address(owner.getAddress())
                .email(owner.getEmail())
                .createdBy(owner.getCreatedBy())
                .createdDate(owner.getCreatedDate())
                .updatedBy(owner.getUpdatedBy())
                .updatedDate(owner.getUpdatedDate())
                .build();
    }
}
