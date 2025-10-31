package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        System.out.println("🔍 Vehicle capacity from request: " + request.getCapacity());
        
        // Duplicate registration check
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            return new ApiResponse(false, "Vehicle with this registration number already exists", null);
        }

        Vehicle vehicle = Vehicle.builder()
                .vehicleNumber(request.getVehicleNumber())
                .registrationNumber(request.getRegistrationNumber())
                .vehiclePhoto(request.getVehiclePhoto())
                .vehicleType(request.getVehicleType())
                .capacity(request.getCapacity())
                .isActive(request.getIsActive())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        System.out.println("🔍 Vehicle entity created with createdBy: " + vehicle.getCreatedBy());
        System.out.println("🔍 Vehicle entity capacity: " + vehicle.getCapacity());
        Vehicle saved = vehicleRepository.save(vehicle);
        System.out.println("🔍 Vehicle saved with ID: " + saved.getVehicleId());
        System.out.println("🔍 Saved vehicle capacity: " + saved.getCapacity());

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
        vehicle.setCapacity(request.getCapacity());
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

        // Check if mapping already exists
        boolean mappingExists = schoolVehicleRepository
                .existsBySchoolAndVehicle(school, vehicle);
        
        if (mappingExists) {
            return new ApiResponse(false, "Vehicle already assigned to this school", null);
        }

        // SchoolVehicle mapping create
        SchoolVehicle schoolVehicle = SchoolVehicle.builder()
                .school(school)
                .vehicle(vehicle)
                .owner(owner)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .updatedBy(request.getUpdatedBy())
                .updatedDate(LocalDateTime.now())
                .build();

        SchoolVehicle savedMapping = schoolVehicleRepository.save(schoolVehicle);
        
        System.out.println("✅ SchoolVehicle mapping saved with ID: " + savedMapping.getSchoolVehicleId());

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
                .capacity(vehicle.getCapacity())
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
    
    @Override
    public ApiResponse getVehiclesByOwner(Integer ownerId) {
        // Validate vehicle owner exists
        VehicleOwner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));
        
        // Get vehicles by createdBy (username)
        String username = owner.getUser() != null ? owner.getUser().getUserName() : null;
        if (username == null) {
            return new ApiResponse(false, "Owner has no associated user", null);
        }
        
        List<Vehicle> vehicles = vehicleRepository.findByCreatedBy(username);
        List<VehicleResponseDto> response = vehicles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return new ApiResponse(true, "Vehicles fetched successfully", response);
    }
    
    @Override
    public ApiResponse getVehiclesByCreatedBy(String username) {
        List<Vehicle> vehicles = vehicleRepository.findByCreatedBy(username);
        List<VehicleResponseDto> response = vehicles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new ApiResponse(true, "Vehicles fetched successfully", response);
    }
    
    @Override
    public ApiResponse getUnassignedVehiclesByOwner(Integer ownerId) {
        System.out.println("🔍 Getting UNASSIGNED vehicles for ownerId: " + ownerId);
        
        // Validate owner exists
        VehicleOwner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with ID: " + ownerId));
        
        System.out.println("🔍 Owner found: " + owner.getName() + " (Username: " + owner.getUser().getUserName() + ")");
        
        // Get ALL vehicles created by this owner
        String ownerUsername = owner.getUser().getUserName();
        List<Vehicle> allVehicles = vehicleRepository.findByCreatedBy(ownerUsername);
        System.out.println("🔍 Total vehicles created by owner: " + allVehicles.size());
        
        // Get vehicles already assigned to schools
        List<SchoolVehicle> assignedMappings = schoolVehicleRepository.findByOwner_OwnerId(ownerId);
        System.out.println("🔍 Already assigned vehicles count: " + assignedMappings.size());
        
        // Extract assigned vehicle IDs
        List<Integer> assignedVehicleIds = assignedMappings.stream()
                .map(sv -> sv.getVehicle().getVehicleId())
                .collect(Collectors.toList());
        
        System.out.println("🔍 Assigned vehicle IDs: " + assignedVehicleIds);
        
        // Filter out assigned vehicles to get UNASSIGNED vehicles
        List<Vehicle> unassignedVehicles = allVehicles.stream()
                .filter(v -> !assignedVehicleIds.contains(v.getVehicleId()))
                .filter(Vehicle::getIsActive)  // Only active vehicles
                .collect(Collectors.toList());
        
        System.out.println("🔍 Unassigned vehicles count: " + unassignedVehicles.size());
        
        List<VehicleResponseDto> response = unassignedVehicles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        Map<String, Object> data = new HashMap<>();
        data.put("ownerId", ownerId);
        data.put("ownerName", owner.getName());
        data.put("totalVehicles", unassignedVehicles.size());
        data.put("vehicles", response);
        
        return new ApiResponse(true, "Unassigned vehicles retrieved successfully", data);
    }

}
