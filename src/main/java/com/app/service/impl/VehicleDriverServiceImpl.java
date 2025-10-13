package com.app.service.impl;

import com.app.entity.Driver;
import com.app.entity.Vehicle;
import com.app.entity.VehicleDriver;
import com.app.entity.School;
import com.app.entity.VehicleOwner;
import com.app.entity.User;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.VehicleDriverRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.VehicleDriverResponseDto;
import com.app.repository.DriverRepository;
import com.app.repository.VehicleDriverRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.VehicleRepository;
import com.app.repository.SchoolVehicleRepository;
import com.app.repository.VehicleOwnerRepository;
import com.app.service.IVehicleDriverService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
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
    @Autowired
    private VehicleOwnerRepository vehicleOwnerRepository;

    @Override
    public ApiResponse assignDriverToVehicle(VehicleDriverRequestDto request) {
        System.out.println("🔍 assignDriverToVehicle: request = " + request);
        
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));
        System.out.println("🔍 assignDriverToVehicle: vehicle found = " + vehicle.getVehicleNumber());

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + request.getDriverId()));
        System.out.println("🔍 assignDriverToVehicle: driver found = " + driver.getDriverName());

        // Validate that the driver has user credentials (is activated)
        if (driver.getUser() == null) {
            System.out.println("🔍 assignDriverToVehicle: ERROR - Driver has not completed user activation");
            return new ApiResponse(false, "Cannot assign vehicle to driver. Driver must complete user activation first.", null);
        }
        System.out.println("🔍 assignDriverToVehicle: driver is activated with user ID = " + driver.getUser().getUId());

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));
        System.out.println("🔍 assignDriverToVehicle: school found = " + school.getSchoolName());

        // Validate that the vehicle is mapped to the school
        boolean vehicleMappedToSchool = schoolVehicleRepository.existsBySchoolAndVehicle(school, vehicle);
        System.out.println("🔍 assignDriverToVehicle: vehicleMappedToSchool = " + vehicleMappedToSchool);
        if (!vehicleMappedToSchool) {
            System.out.println("🔍 assignDriverToVehicle: ERROR - Vehicle is not assigned to the provided school");
            throw new ResourceNotFoundException("Vehicle is not assigned to the provided school");
        }

        // Prevent duplicate active assignment for same (vehicle, driver, school)
        boolean activeAssignmentExists = vehicleDriverRepository.existsActiveAssignment(vehicle, driver, school);
        System.out.println("🔍 assignDriverToVehicle: activeAssignmentExists = " + activeAssignmentExists);
        if (activeAssignmentExists) {
            System.out.println("🔍 assignDriverToVehicle: ERROR - Active assignment already exists");
            return new ApiResponse(false, "Active assignment already exists for this vehicle, driver and school", null);
        }

        // If primary is requested, ensure no other primary for this vehicle in the same school
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            boolean primaryExists = vehicleDriverRepository.existsByVehicleAndIsPrimaryTrue(vehicle);
            System.out.println("🔍 assignDriverToVehicle: primaryExists = " + primaryExists);
            if (primaryExists) {
                System.out.println("🔍 assignDriverToVehicle: ERROR - Another primary driver already exists");
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

        System.out.println("🔍 assignDriverToVehicle: vehicleDriver entity created = " + vehicleDriver);
        VehicleDriver saved = vehicleDriverRepository.save(vehicleDriver);
        System.out.println("🔍 assignDriverToVehicle: vehicleDriver saved with ID = " + saved.getVehicleDriverId());

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

    @Override
    public ApiResponse getDriverAssignmentsByOwner(Integer ownerId) {
        try {
            System.out.println("🔍 Getting driver assignments for owner ID: " + ownerId);
            
            // Get all vehicles for the owner - try multiple methods
            List<Vehicle> ownerVehicles = new ArrayList<>(); // Start with empty list
            System.out.println("🔍 Starting vehicle search for owner ID: " + ownerId);
            
            // Get owner and try with username first
            VehicleOwner owner = vehicleOwnerRepository.findById(ownerId).orElse(null);
            if (owner != null) {
                // Get username from user table
                User ownerUser = owner.getUser();
                String username = ownerUser != null ? ownerUser.getUserName() : null;
                
                System.out.println("🔍 Owner username: " + username);
                
                if (username != null) {
                    ownerVehicles = vehicleRepository.findByCreatedBy(username);
                    System.out.println("🔍 Found " + ownerVehicles.size() + " vehicles using username: " + username);
                }
                
                // Fallback to owner name
                if (ownerVehicles.isEmpty()) {
                    System.out.println("🔍 Trying findByCreatedBy with owner name: " + owner.getName());
                    ownerVehicles = vehicleRepository.findByCreatedBy(owner.getName());
                    System.out.println("🔍 Found " + ownerVehicles.size() + " vehicles using findByCreatedBy with name");
                }
                
                // If still no vehicles, try case-insensitive search
                if (ownerVehicles.isEmpty()) {
                    System.out.println("🔍 Trying case-insensitive search for: " + owner.getName());
                    List<Vehicle> allVehicles = vehicleRepository.findAll();
                    System.out.println("🔍 Total vehicles in database: " + allVehicles.size());
                    
                    // Print all vehicles with their createdBy field
                    for (Vehicle v : allVehicles) {
                        System.out.println("🔍 Vehicle ID: " + v.getVehicleId() + ", Number: " + v.getVehicleNumber() + ", CreatedBy: " + v.getCreatedBy());
                    }
                    
                    ownerVehicles = allVehicles.stream()
                        .filter(v -> v.getCreatedBy() != null && 
                                   v.getCreatedBy().toLowerCase().contains(owner.getName().toLowerCase()))
                        .collect(Collectors.toList());
                    System.out.println("🔍 Found " + ownerVehicles.size() + " vehicles using case-insensitive search");
                }
            }
            
            // Get all driver assignments for these vehicles
            List<VehicleDriver> allAssignments = new ArrayList<>();
            for (Vehicle vehicle : ownerVehicles) {
                List<VehicleDriver> vehicleAssignments = vehicleDriverRepository.findByVehicle(vehicle);
                System.out.println("🔍 Vehicle " + vehicle.getVehicleNumber() + " has " + vehicleAssignments.size() + " assignments");
                allAssignments.addAll(vehicleAssignments);
            }
            
            // Map to response DTOs
            List<VehicleDriverResponseDto> assignments = allAssignments.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            
            System.out.println("🔍 Total assignments found: " + assignments.size());
            return new ApiResponse(true, "Driver assignments fetched successfully", assignments);
        } catch (Exception e) {
            System.out.println("🔍 Error fetching driver assignments: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Failed to fetch driver assignments: " + e.getMessage(), null);
        }
    }

    // ------------------ Private Mapper ------------------
    private VehicleDriverResponseDto mapToResponse(VehicleDriver vehicleDriver) {
        return VehicleDriverResponseDto.builder()
                .vehicleDriverId(vehicleDriver.getVehicleDriverId())
                .vehicleId(vehicleDriver.getVehicle().getVehicleId())
                .vehicleNumber(vehicleDriver.getVehicle().getVehicleNumber())
                .vehicleType(vehicleDriver.getVehicle().getVehicleType())
                .schoolId(vehicleDriver.getSchool() != null ? vehicleDriver.getSchool().getSchoolId() : null)
                .schoolName(vehicleDriver.getSchool() != null ? vehicleDriver.getSchool().getSchoolName() : null)
                .driverId(vehicleDriver.getDriver().getDriverId())
                .driverName(vehicleDriver.getDriver().getDriverName())
                .driverPhone(vehicleDriver.getDriver().getDriverContactNumber())
                .driverContactNumber(vehicleDriver.getDriver().getDriverContactNumber())
                .isPrimary(vehicleDriver.getIsPrimary())
                .isActive(vehicleDriver.getIsActive())
                .startDate(vehicleDriver.getStartDate())
                .endDate(vehicleDriver.getEndDate())
                .createdBy(vehicleDriver.getCreatedBy())
                .createdDate(vehicleDriver.getCreatedDate())
                .updatedBy(vehicleDriver.getUpdatedBy())
                .updatedDate(vehicleDriver.getUpdatedDate())
                .build();
    }
}
