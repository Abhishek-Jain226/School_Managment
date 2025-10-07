package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entity.School;
import com.app.entity.SchoolVehicle;
import com.app.entity.Vehicle;
import com.app.entity.VehicleOwner;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.SchoolVehicleRequestDto;
import com.app.payload.request.VehicleRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.VehicleResponseDto;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolVehicleRepository;
import com.app.repository.VehicleOwnerRepository;
import com.app.repository.VehicleRepository;
import com.app.service.IVehicleService;

@Service
public class VehicleServiceImpl implements IVehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private SchoolVehicleRepository schoolVehicleRepository;
    
    @Autowired
    private VehicleOwnerRepository ownerRepository;

    @Override
    public ApiResponse createVehicle(VehicleRequestDto request) {
        System.out.println("🔍 Creating vehicle with data: " + request);
        
        // Duplicate registration check
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            return new ApiResponse(false, "Vehicle with this registration number already exists", null);
        }

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(request.getVehicleNumber())
                .registrationNumber(request.getRegistrationNumber())
                .vehiclePhoto(request.getVehiclePhoto())
                .vehicleType(request.getVehicleType())
                .isActive(request.getIsActive())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        System.out.println("🔍 Vehicle entity created with createdBy: " + vehicle.getCreatedBy());
        Vehicle saved = vehicleRepository.save(vehicle);
        System.out.println("🔍 Vehicle saved with ID: " + saved.getVehicleId());

        return new ApiResponse(true, "Vehicle created successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse updateVehicle(Integer vehicleId, VehicleRequestDto request) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        // If updating registration number, check duplicate
        if (!vehicle.getRegistrationNumber().equals(request.getRegistrationNumber()) &&
                vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            return new ApiResponse(false, "Vehicle with this registration number already exists", null);
        }

        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setVehiclePhoto(request.getVehiclePhoto());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setIsActive(request.getIsActive());
        vehicle.setUpdatedBy(request.getUpdatedBy());
        vehicle.setUpdatedDate(LocalDateTime.now());

        Vehicle updated = vehicleRepository.save(vehicle);

        return new ApiResponse(true, "Vehicle updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse deleteVehicle(Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        vehicleRepository.delete(vehicle);

        return new ApiResponse(true, "Vehicle deleted successfully", null);
    }

    @Override
    public ApiResponse getVehicleById(Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        return new ApiResponse(true, "Vehicle fetched successfully", mapToResponse(vehicle));
    }

    @Override
    public ApiResponse getAllVehicles(Integer schoolId) {
        // Validate school
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        // Use repository query with schoolId instead of school entity
        List<VehicleResponseDto> vehicles = vehicleRepository.findBySchoolId(schoolId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Vehicles fetched successfully", vehicles);
    }


    @Override
    public ApiResponse assignVehicleToSchool(SchoolVehicleRequestDto request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));
        
        VehicleOwner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Owner not found with ID: " + request.getOwnerId()
                ));

        // SchoolVehicle mapping create
        SchoolVehicle schoolVehicle = SchoolVehicle.builder()
                .school(school)
                .vehicle(vehicle)
                .owner(owner)  // agar owner bhi required hai
                .isActive(true)  // ✅ FIX
                .updatedBy(request.getUpdatedBy())
                .updatedDate(LocalDateTime.now())
                .build();

        schoolVehicleRepository.save(schoolVehicle);

        return new ApiResponse(true, "Vehicle assigned to school successfully", mapToResponse(vehicle));
    }

    // Mapper
    private VehicleResponseDto mapToResponse(Vehicle vehicle) {
        return VehicleResponseDto.builder()
                .vehicleId(vehicle.getVehicleId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .registrationNumber(vehicle.getRegistrationNumber())
                .vehiclePhoto(vehicle.getVehiclePhoto())
                .vehicleType(vehicle.getVehicleType())
                .isActive(vehicle.getIsActive())
                .createdBy(vehicle.getCreatedBy())
                .createdDate(vehicle.getCreatedDate())
                .updatedBy(vehicle.getUpdatedBy())
                .updatedDate(vehicle.getUpdatedDate())
                .build();
    }

    public long getVehicleCountBySchool(Integer schoolId) {
        return schoolVehicleRepository.countBySchool_SchoolId(schoolId);
    }
    
    // Removed commented code that used findByOwner_OwnerId
    @Override
    public ApiResponse getVehiclesByCreatedBy(String username) {
        List<Vehicle> vehicles = vehicleRepository.findByCreatedBy(username);
        List<VehicleResponseDto> response = vehicles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new ApiResponse(true, "Vehicles fetched successfully", response);
    }

	

}
