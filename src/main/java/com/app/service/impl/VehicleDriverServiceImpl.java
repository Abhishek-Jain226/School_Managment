package com.app.service.impl;

import com.app.entity.Driver;
import com.app.entity.Vehicle;
import com.app.entity.VehicleDriver;
import com.app.entity.School;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.VehicleDriverRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.VehicleDriverResponseDto;
import com.app.repository.DriverRepository;
import com.app.repository.VehicleDriverRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.VehicleRepository;
import com.app.repository.SchoolVehicleRepository;
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
    @Autowired
    private SchoolRepository schoolRepository;
    @Autowired
    private SchoolVehicleRepository schoolVehicleRepository;

    @Override
    public ApiResponse assignDriverToVehicle(VehicleDriverRequestDto request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + request.getDriverId()));

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

        // Validate that the vehicle is mapped to the school
        if (!schoolVehicleRepository.existsBySchoolAndVehicle(school, vehicle)) {
            throw new ResourceNotFoundException("Vehicle is not assigned to the provided school");
        }

        // Prevent duplicate active assignment for same (vehicle, driver, school)
        if (vehicleDriverRepository.existsActiveAssignment(vehicle, driver, school)) {
            return new ApiResponse(false, "Active assignment already exists for this vehicle, driver and school", null);
        }

        // If primary is requested, ensure no other primary for this vehicle in the same school
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            // Reuse existing query that checks primary at vehicle level; if you need school-scoped, add a repo method
            if (vehicleDriverRepository.existsByVehicleAndIsPrimaryTrue(vehicle)) {
                return new ApiResponse(false, "Another primary driver already exists for this vehicle", null);
            }
        }

        VehicleDriver vehicleDriver = VehicleDriver.builder()
                .vehicle(vehicle)
                .driver(driver)
                .school(school)
                .isPrimary(request.getIsPrimary())
                .isActive(true)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
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

        if (request.getSchoolId() != null) {
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));
            vehicleDriver.setSchool(school);
        }

        // If primary is toggled on, validate no other primary exists at vehicle level (or add school-scoped method if needed)
        if (Boolean.TRUE.equals(request.getIsPrimary()) && !Boolean.TRUE.equals(vehicleDriver.getIsPrimary())) {
            if (vehicleDriverRepository.existsByVehicleAndIsPrimaryTrue(vehicleDriver.getVehicle())) {
                return new ApiResponse(false, "Another primary driver already exists for this vehicle", null);
            }
        }
        vehicleDriver.setIsPrimary(request.getIsPrimary());
        vehicleDriver.setIsActive(request.getIsActive());
        vehicleDriver.setStartDate(request.getStartDate() != null ? request.getStartDate() : vehicleDriver.getStartDate());
        vehicleDriver.setEndDate(request.getEndDate());
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
                .schoolId(vehicleDriver.getSchool() != null ? vehicleDriver.getSchool().getSchoolId() : null)
                .schoolName(vehicleDriver.getSchool() != null ? vehicleDriver.getSchool().getSchoolName() : null)
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
