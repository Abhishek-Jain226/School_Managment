package com.app.service.impl;

import com.app.entity.Driver;
import com.app.entity.Vehicle;
import com.app.entity.VehicleDriver;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.VehicleDriverRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.VehicleDriverResponseDto;
import com.app.repository.DriverRepository;
import com.app.repository.VehicleDriverRepository;
import com.app.repository.VehicleRepository;
import com.app.service.IVehicleDriverService;

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
public class VehicleDriverServiceImpl implements IVehicleDriverService {

	@Autowired
    private VehicleDriverRepository vehicleDriverRepository;
	@Autowired
    private VehicleRepository vehicleRepository;
	@Autowired
    private DriverRepository driverRepository;

    @Override
    public ApiResponse assignDriverToVehicle(VehicleDriverRequestDto request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + request.getDriverId()));

        VehicleDriver vehicleDriver = VehicleDriver.builder()
                .vehicle(vehicle)
                .driver(driver)
                .isPrimary(request.getIsPrimary())
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        VehicleDriver saved = vehicleDriverRepository.save(vehicleDriver);

        return new ApiResponse(true, "Driver assigned to vehicle successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse updateVehicleDriver(Integer vehicleDriverId, VehicleDriverRequestDto request) {
        VehicleDriver vehicleDriver = vehicleDriverRepository.findById(vehicleDriverId)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleDriver not found with ID: " + vehicleDriverId));

        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));
            vehicleDriver.setVehicle(vehicle);
        }

        if (request.getDriverId() != null) {
            Driver driver = driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + request.getDriverId()));
            vehicleDriver.setDriver(driver);
        }

        vehicleDriver.setIsPrimary(request.getIsPrimary());
        vehicleDriver.setIsActive(request.getIsActive());
        vehicleDriver.setUpdatedBy(request.getUpdatedBy());
        vehicleDriver.setUpdatedDate(LocalDateTime.now());

        VehicleDriver updated = vehicleDriverRepository.save(vehicleDriver);

        return new ApiResponse(true, "VehicleDriver updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse removeDriverFromVehicle(Integer vehicleDriverId) {
        VehicleDriver vehicleDriver = vehicleDriverRepository.findById(vehicleDriverId)
                .orElseThrow(() -> new ResourceNotFoundException("VehicleDriver not found with ID: " + vehicleDriverId));

        vehicleDriverRepository.delete(vehicleDriver);

        return new ApiResponse(true, "Driver removed from vehicle successfully", null);
    }

    @Override
    public ApiResponse getDriversByVehicle(Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        List<VehicleDriverResponseDto> drivers = vehicleDriverRepository.findByVehicle(vehicle).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Drivers fetched successfully for vehicle", drivers);
    }

    // ------------------ Private Mapper ------------------
    private VehicleDriverResponseDto mapToResponse(VehicleDriver vehicleDriver) {
        return VehicleDriverResponseDto.builder()
                .vehicleDriverId(vehicleDriver.getVehicleDriverId())
                .vehicleId(vehicleDriver.getVehicle().getVehicleId())
                .driverId(vehicleDriver.getDriver().getDriverId())
                .isPrimary(vehicleDriver.getIsPrimary())
                .isActive(vehicleDriver.getIsActive())
                .createdBy(vehicleDriver.getCreatedBy())
                .createdDate(vehicleDriver.getCreatedDate())
                .updatedBy(vehicleDriver.getUpdatedBy())
                .updatedDate(vehicleDriver.getUpdatedDate())
                .build();
    }
}
