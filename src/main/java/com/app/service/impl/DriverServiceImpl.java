package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entity.Driver;
import com.app.entity.Role;
import com.app.entity.User;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.DriverRequestDto;
import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.DriverResponseDto;
import com.app.repository.DriverRepository;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import com.app.service.IDriverService;
import com.app.service.IPendingUserService;

@Service
public class DriverServiceImpl implements IDriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private IPendingUserService pendingUserService;

    @Override
    public ApiResponse createDriver(DriverRequestDto request) {
        // Check if user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        // Check duplicate driver contact
        if (driverRepository.existsByDriverContactNumber(request.getDriverContactNumber())) {
            return new ApiResponse(false, "Driver with this contact number already exists", null);
        }

        Driver driver = Driver.builder()
                .driverName(request.getDriverName())
                .driverPhoto(request.getDriverPhoto())
                .driverContactNumber(request.getDriverContactNumber())
                .driverAddress(request.getDriverAddress())
                .isActive(request.getIsActive())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .user(user) // assuming Driver has ManyToOne relation with User
                .build();

        Driver savedDriver  = driverRepository.save(driver);
        // Find DRIVER role
        Role role = roleRepository.findByRoleName("DRIVER")
                .orElseThrow(() -> new ResourceNotFoundException("Role DRIVER not found"));

        // Create PendingUser entry for activation link
        PendingUserRequestDTO pendingReq = PendingUserRequestDTO.builder()
                .entityType("DRIVER")
                .entityId(savedDriver.getDriverId().longValue())
                .email(request.getEmail())
                .contactNumber(request.getDriverContactNumber())
                .roleId(role.getRoleId())
                .createdBy(request.getCreatedBy())
                .build();

        pendingUserService.createPendingUser(pendingReq);

        return new ApiResponse(true, "Driver registered successfully. Activation link sent to email.",
                savedDriver.getDriverId());
    }

    @Override
    public ApiResponse updateDriver(Integer driverId, DriverRequestDto request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

        // If updating contact number, check duplicates
        if (!driver.getDriverContactNumber().equals(request.getDriverContactNumber()) &&
                driverRepository.existsByDriverContactNumber(request.getDriverContactNumber())) {
            return new ApiResponse(false, "Driver with this contact number already exists", null);
        }

        // Update fields
        driver.setDriverName(request.getDriverName());
        driver.setDriverPhoto(request.getDriverPhoto());
        driver.setDriverContactNumber(request.getDriverContactNumber());
        driver.setDriverAddress(request.getDriverAddress());
        driver.setIsActive(request.getIsActive());
        driver.setUpdatedBy(request.getUpdatedBy());
        driver.setUpdatedDate(LocalDateTime.now());

        Driver updated = driverRepository.save(driver);

        return new ApiResponse(true, "Driver updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse deleteDriver(Integer driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

        driverRepository.delete(driver);

        return new ApiResponse(true, "Driver deleted successfully", null);
    }

    @Override
    public ApiResponse getDriverById(Integer driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

        return new ApiResponse(true, "Driver fetched successfully", mapToResponse(driver));
    }

    @Override
    public ApiResponse getAllDrivers(Integer ownerId) {
        // Assuming ownerId is actually userId to filter drivers
        List<DriverResponseDto> drivers = driverRepository.findAll().stream()
                .filter(d -> d.getUser() != null && d.getUser().getUId().equals(ownerId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Drivers fetched successfully", drivers);
    }

    // Helper method to map entity to response DTO
    private DriverResponseDto mapToResponse(Driver driver) {
        return DriverResponseDto.builder()
                .driverId(driver.getDriverId())
                .userId(driver.getUser() != null ? driver.getUser().getUId() : null)
               // .userName(driver.getUser() != null ? driver.getUser().getUserName() : null)
                .driverName(driver.getDriverName())
                .driverPhoto(driver.getDriverPhoto())
                .driverContactNumber(driver.getDriverContactNumber())
                .driverAddress(driver.getDriverAddress())
                .isActive(driver.getIsActive())
                .createdBy(driver.getCreatedBy())
                .createdDate(driver.getCreatedDate())
                .updatedBy(driver.getUpdatedBy())
                .updatedDate(driver.getUpdatedDate())
                .build();
    }
}
