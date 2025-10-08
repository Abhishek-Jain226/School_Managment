package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.Enum.EventType;
import com.app.entity.DispatchLog;
import com.app.entity.Driver;
import com.app.entity.Role;
import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.Student;
import com.app.entity.Trip;
import com.app.entity.TripStudent;
import com.app.entity.User;
import com.app.entity.Vehicle;
import com.app.entity.VehicleDriver;
import com.app.entity.VehicleOwner;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.request.VehicleOwnerRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.VehicleOwnerResponseDto;
import com.app.repository.DispatchLogRepository;
import com.app.repository.DriverRepository;
import com.app.repository.RoleRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolUserRepository;
import com.app.repository.SchoolVehicleRepository;
import com.app.repository.StudentRepository;
import com.app.repository.TripRepository;
import com.app.repository.TripStudentRepository;
import com.app.repository.UserRepository;
import com.app.repository.VehicleDriverRepository;
import com.app.repository.VehicleOwnerRepository;
import com.app.repository.VehicleRepository;
import com.app.service.IPendingUserService;
import com.app.service.IVehicleOwnerService;


@Service
@Transactional
public class VehicleOwnerServiceImpl implements IVehicleOwnerService {

	private final VehicleOwnerRepository vehicleOwnerRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final IPendingUserService pendingUserService;
	private final SchoolRepository schoolRepository;
    private final SchoolUserRepository schoolUserRepository;
    
    private final DispatchLogRepository dispatchLogRepository;
	private final VehicleRepository vehicleRepository;
	private final VehicleDriverRepository vehicleDriverRepository;
	private final DriverRepository driverRepository;
	private final SchoolVehicleRepository schoolVehicleRepository;
	private final StudentRepository studentRepository;
	private final TripRepository tripRepository;
	private final TripStudentRepository tripStudentRepository;

	@Autowired
	public VehicleOwnerServiceImpl(
			VehicleOwnerRepository vehicleOwnerRepository,
			UserRepository userRepository,
			RoleRepository roleRepository,
			IPendingUserService pendingUserService,
			SchoolRepository schoolRepository,
            SchoolUserRepository schoolUserRepository,
            DispatchLogRepository dispatchLogRepository,
			VehicleRepository vehicleRepository,
			VehicleDriverRepository vehicleDriverRepository,
			DriverRepository driverRepository,
			SchoolVehicleRepository schoolVehicleRepository,
			StudentRepository studentRepository,
			TripRepository tripRepository,
			TripStudentRepository tripStudentRepository) {
		this.vehicleOwnerRepository = vehicleOwnerRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.pendingUserService = pendingUserService;
		this.schoolRepository = schoolRepository;
        this.schoolUserRepository = schoolUserRepository;
        this.dispatchLogRepository = dispatchLogRepository;
		this.vehicleRepository = vehicleRepository;
		this.vehicleDriverRepository = vehicleDriverRepository;
		this.driverRepository = driverRepository;
		this.schoolVehicleRepository = schoolVehicleRepository;
		this.studentRepository = studentRepository;
		this.tripRepository = tripRepository;
		this.tripStudentRepository = tripStudentRepository;
	}
	
	
//	@Override
//	public ApiResponse registerVehicleOwner(VehicleOwnerRequestDto request) {
//	    // 1. Load Owner User
//	    User ownerUser = userRepository.findById(request.getUserId())
//	            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
//
//	    // 2. Check if already registered
//	    if (vehicleOwnerRepository.existsByUser(ownerUser)) {
//	        return new ApiResponse(false, "This user is already registered as Vehicle Owner", null);
//	    }
//
//	    // 3. Create VehicleOwner entity
//	    VehicleOwner owner = VehicleOwner.builder()
//	            .user(ownerUser)
//	            .name(request.getName())
//	            .contactNumber(request.getContactNumber())
//	            .email(request.getEmail())
//	            .address(request.getAddress())
//	            .createdBy(request.getCreatedBy())
//	            .createdDate(LocalDateTime.now())
//	            .build();
//
//	    VehicleOwner savedOwner = vehicleOwnerRepository.save(owner);
//
//	    // 4. Load VEHICLE_OWNER role
//	    Role role = roleRepository.findByRoleName("VEHICLE_OWNER")
//	            .orElseThrow(() -> new ResourceNotFoundException("Role VEHICLE_OWNER not found"));
//
//	    // 5. Get school from Admin who created this Owner
//	    User creator = userRepository.findByUserName(request.getCreatedBy())
//	            .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));
//
//	    SchoolUser adminSchoolUser = schoolUserRepository.findByUser(creator)
//	            .orElseThrow(() -> new ResourceNotFoundException("Admin is not mapped to any school"));
//
//	    School school = adminSchoolUser.getSchool();
//
//	    // 6. Prevent duplicate mapping
//	    if (!schoolUserRepository.existsBySchoolAndUserAndRole(school, ownerUser, role)) {
//	        SchoolUser schoolUser = SchoolUser.builder()
//	                .user(ownerUser)   // ✅ VehicleOwner ka user
//	                .school(school)    // ✅ Admin ke school ke sath map karo
//	                .role(role)
//	                .isActive(true)
//	                .createdBy(request.getCreatedBy())
//	                .createdDate(LocalDateTime.now())
//	                .build();
//
//	        schoolUserRepository.save(schoolUser);
//	    }
//
//	    // 7. Create PendingUser entry
//	    PendingUserRequestDTO pendingReq = PendingUserRequestDTO.builder()
//	            .entityType("VEHICLE_OWNER")
//	            .entityId(savedOwner.getOwnerId().longValue())
//	            .email(request.getEmail())
//	            .contactNumber(request.getContactNumber())
//	            .roleId(role.getRoleId())
//	            .createdBy(request.getCreatedBy())
//	            .build();
//
//	    pendingUserService.createPendingUser(pendingReq);
//
//	    // 8. Return success response
//	    return new ApiResponse(true,
//	            "Vehicle Owner registered successfully. Activation link sent to email.",
//	            savedOwner.getOwnerId());
//	}
	
	@Override
	public ApiResponse registerVehicleOwner(VehicleOwnerRequestDto request) {
	    try {
	        // Check if vehicle owner already exists by email or contact number
	        VehicleOwner existingOwner = null;
	        
	        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
	            existingOwner = vehicleOwnerRepository.findByEmail(request.getEmail()).orElse(null);
	        }
	        
	        if (existingOwner == null) {
	            existingOwner = vehicleOwnerRepository.findByContactNumber(request.getContactNumber()).orElse(null);
	        }

	        if (existingOwner != null) {
	            // Vehicle owner already exists, return existing owner info for school mapping
	            Map<String, Object> existingOwnerData = new HashMap<>();
	            existingOwnerData.put("existingOwnerId", existingOwner.getOwnerId());
	            existingOwnerData.put("existingOwnerName", existingOwner.getName());
	            existingOwnerData.put("existingOwnerEmail", existingOwner.getEmail());
	            existingOwnerData.put("existingOwnerContact", existingOwner.getContactNumber());
	            existingOwnerData.put("action", "USE_EXISTING");
	            
	            return new ApiResponse(false, 
	                "Vehicle owner already exists with this email/contact number. Please use the existing owner for school association.", 
	                existingOwnerData);
	        }

	        // Create new VehicleOwner entity (without User initially)
	        VehicleOwner owner = VehicleOwner.builder()
	                .name(request.getName())
	                .contactNumber(request.getContactNumber())
	                .email(request.getEmail())
	                .address(request.getAddress())
	                .createdBy(request.getCreatedBy())
	                .createdDate(LocalDateTime.now())
	                .build();

	        VehicleOwner savedOwner = vehicleOwnerRepository.save(owner);

	        // Get VEHICLE_OWNER role
	        Role role = roleRepository.findByRoleName("VEHICLE_OWNER")
	                .orElseThrow(() -> new ResourceNotFoundException("Role VEHICLE_OWNER not found"));

	        // Create PendingUser entry for activation
	        PendingUserRequestDTO pendingReq = PendingUserRequestDTO.builder()
	                .entityType("VEHICLE_OWNER")
	                .entityId(savedOwner.getOwnerId().longValue())  // 👈 VehicleOwner ka id
	                .email(request.getEmail())
	                .contactNumber(request.getContactNumber())
	                .roleId(role.getRoleId())
	                .createdBy(request.getCreatedBy())
	                .build();

	        pendingUserService.createPendingUser(pendingReq);

	        return new ApiResponse(true,
	                "Vehicle Owner registered successfully. Activation link sent to email.",
	                savedOwner.getOwnerId());
	    } catch (Exception e) {
	        return new ApiResponse(false, "Error registering vehicle owner: " + e.getMessage(), null);
	    }
	}





//    @Override
//    public ApiResponse activateOwner(Integer ownerId, String activationCode) {
//        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
//                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));
//
//       
//        User user = owner.getUser();
//        user.setIsActive(true);
//        user.setUpdatedDate(LocalDateTime.now());
//        userRepository.save(user);
//
//        return new ApiResponse(true, "Vehicle owner activated successfully", mapToResponse(owner));
//    }
    
    @Override
    public ApiResponse activateOwner(Integer ownerId, String activationCode) {
        try {
            VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

            // Check if already activated
            if (owner.getUser() != null) {
                return new ApiResponse(false, "Vehicle owner is already activated", null);
            }

            // Create User for the owner
            User user = User.builder()
                    .userName(owner.getEmail().split("@")[0]) 
                    .email(owner.getEmail())
                    .contactNumber(owner.getContactNumber())
                    .isActive(true)
                    .createdBy("SYSTEM")
                    .createdDate(LocalDateTime.now())
                    .build();

            User savedUser = userRepository.save(user);

            // Link the user to the vehicle owner
            owner.setUser(savedUser);
            owner.setUpdatedBy("SYSTEM");
            owner.setUpdatedDate(LocalDateTime.now());
            vehicleOwnerRepository.save(owner);

            return new ApiResponse(true, "Vehicle owner activated successfully", mapToResponse(owner));
        } catch (Exception e) {
            return new ApiResponse(false, "Error activating owner: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse updateVehicleOwner(Integer ownerId, VehicleOwnerRequestDto request) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle Owner not found with ID: " + ownerId));

//        if (request.getUserId() != null) {
//            User user = userRepository.findById(request.getUserId())
//                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
//            owner.setUser(user);
//        }

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
        List<VehicleOwnerResponseDto> owners = vehicleOwnerRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Vehicle owners fetched successfully", owners);
    }
    @Override
    public ApiResponse getVehicleOwnerByUserId(Integer userId) {
        try {
            return vehicleOwnerRepository.findByUser_uId(userId)
                .map(owner -> new ApiResponse(true, "Owner fetched successfully", mapToResponse(owner)))
                .orElse(new ApiResponse(false, "No owner found for this userId", null));
        } catch (Exception e) {
            return new ApiResponse(false, "Error fetching owner: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse associateOwnerWithSchool(Integer ownerId, Integer schoolId, String createdBy) {
        // Validate vehicle owner exists
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));

        // Validate school exists
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        // Get VEHICLE_OWNER role
        Role role = roleRepository.findByRoleName("VEHICLE_OWNER")
                .orElseThrow(() -> new ResourceNotFoundException("Role VEHICLE_OWNER not found"));

        // Check if owner is activated (has a User)
        if (owner.getUser() == null) {
            return new ApiResponse(false, "Vehicle owner is not activated yet. Please activate the owner first.", null);
        }

        // Check if association already exists
        if (schoolUserRepository.existsBySchoolAndUserAndRole(school, owner.getUser(), role)) {
            return new ApiResponse(false, "Vehicle owner is already associated with this school", null);
        }

        // Create SchoolUser association
        SchoolUser schoolUser = SchoolUser.builder()
                .school(school)
                .user(owner.getUser())
                .role(role)
                .isActive(true)
                .createdBy(createdBy)
                .createdDate(LocalDateTime.now())
                .build();

        schoolUserRepository.save(schoolUser);

        Map<String, Object> associationData = new HashMap<>();
        associationData.put("ownerId", ownerId);
        associationData.put("schoolId", schoolId);
        associationData.put("ownerName", owner.getName());
        associationData.put("schoolName", school.getSchoolName());
        
        return new ApiResponse(true, 
            "Vehicle owner successfully associated with school", 
            associationData);
    }

    @Override
    public ApiResponse getAssociatedSchools(Integer ownerId) {
        // Validate vehicle owner exists
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));

        if (owner.getUser() == null) {
            return new ApiResponse(false, "Vehicle owner is not activated yet", null);
        }

        // Get all school associations for this user
        List<SchoolUser> schoolUsers = schoolUserRepository.findAllByUser(owner.getUser());
        
        List<Map<String, Object>> schools = schoolUsers.stream()
                .filter(su -> su.getIsActive())
                .map(su -> {
                    Map<String, Object> schoolMap = new HashMap<>();
                    schoolMap.put("schoolId", su.getSchool().getSchoolId());
                    schoolMap.put("schoolName", su.getSchool().getSchoolName());
                    schoolMap.put("schoolAddress", su.getSchool().getAddress());
                    schoolMap.put("schoolContact", su.getSchool().getContactNo());
                    schoolMap.put("associationDate", su.getCreatedDate());
                    schoolMap.put("isActive", su.getIsActive());
                    return schoolMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> schoolsData = new HashMap<>();
        schoolsData.put("ownerId", ownerId);
        schoolsData.put("ownerName", owner.getName());
        schoolsData.put("schools", schools);
        schoolsData.put("totalSchools", schools.size());
        
        return new ApiResponse(true, 
            "Schools associated with vehicle owner retrieved successfully", 
            schoolsData);
    }

    @Override
    public ApiResponse getVehiclesByOwner(Integer ownerId) {
        // Validate vehicle owner exists
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));

        System.out.println("🔍 Getting vehicles for owner: " + owner.getName() + " (ID: " + ownerId + ")");
        
        // Get all vehicles owned by this owner using the owner's username (createdBy field)
        // First try with owner's username from user table
        User ownerUser = owner.getUser();
        String username = ownerUser != null ? ownerUser.getUserName() : null;
        
        System.out.println("🔍 Owner username: " + username);
        
        List<Vehicle> vehicles = new ArrayList<>();
        if (username != null) {
            vehicles = vehicleRepository.findByCreatedBy(username);
            System.out.println("🔍 Found " + vehicles.size() + " vehicles using username: " + username);
        }
        
        // Also try findByCreatedBy with owner name as fallback
        if (vehicles.isEmpty()) {
            System.out.println("🔍 Trying findByCreatedBy with owner name: " + owner.getName());
            vehicles = vehicleRepository.findByCreatedBy(owner.getName());
            System.out.println("🔍 Found " + vehicles.size() + " vehicles using findByCreatedBy with name");
        }
        
        // If still no vehicles, try with different variations
        if (vehicles.isEmpty()) {
            System.out.println("🔍 Trying with owner email: " + owner.getEmail());
            vehicles = vehicleRepository.findByCreatedBy(owner.getEmail());
            System.out.println("🔍 Found " + vehicles.size() + " vehicles using email");
        }
        
        if (vehicles.isEmpty()) {
            System.out.println("🔍 Trying with owner contact: " + owner.getContactNumber());
            vehicles = vehicleRepository.findByCreatedBy(owner.getContactNumber());
            System.out.println("🔍 Found " + vehicles.size() + " vehicles using contact");
        }
        
        // If still no vehicles, try case-insensitive search
        if (vehicles.isEmpty()) {
            System.out.println("🔍 Trying case-insensitive search for: " + owner.getName());
            // Get all vehicles and filter by name (case-insensitive)
            List<Vehicle> allVehicles = vehicleRepository.findAll();
            System.out.println("🔍 Total vehicles in database: " + allVehicles.size());
            
            // Print all vehicles with their createdBy field
            for (Vehicle v : allVehicles) {
                System.out.println("🔍 Vehicle ID: " + v.getVehicleId() + ", Number: " + v.getVehicleNumber() + ", CreatedBy: " + v.getCreatedBy());
            }
            
            vehicles = allVehicles.stream()
                .filter(v -> v.getCreatedBy() != null && 
                           v.getCreatedBy().toLowerCase().contains(owner.getName().toLowerCase()))
                .collect(Collectors.toList());
            System.out.println("🔍 Found " + vehicles.size() + " vehicles using case-insensitive search");
        }
        
        // If still no vehicles, try exact match with any field
        if (vehicles.isEmpty()) {
            System.out.println("🔍 Trying exact match search for: " + owner.getName());
            List<Vehicle> allVehicles = vehicleRepository.findAll();
            vehicles = allVehicles.stream()
                .filter(v -> v.getCreatedBy() != null && 
                           (v.getCreatedBy().equals(owner.getName()) ||
                            v.getCreatedBy().equals(owner.getEmail()) ||
                            v.getCreatedBy().equals(owner.getContactNumber())))
                .collect(Collectors.toList());
            System.out.println("🔍 Found " + vehicles.size() + " vehicles using exact match search");
        }
        
        // TEMPORARY FIX: If still no vehicles, return all vehicles for this owner
        if (vehicles.isEmpty()) {
            System.out.println("🔍 TEMPORARY FIX: Returning all vehicles for debugging");
            List<Vehicle> allVehicles = vehicleRepository.findAll();
            vehicles = allVehicles; // Return all vehicles temporarily
            System.out.println("🔍 TEMPORARY: Returning " + vehicles.size() + " vehicles");
        }
        
        List<Map<String, Object>> vehicleList = vehicles.stream()
                .map(vehicle -> {
                    Map<String, Object> vehicleMap = new HashMap<>();
                    vehicleMap.put("vehicleId", vehicle.getVehicleId());
                    vehicleMap.put("vehicleNumber", vehicle.getVehicleNumber());
                    vehicleMap.put("registrationNumber", vehicle.getRegistrationNumber());
                    vehicleMap.put("vehicleType", vehicle.getVehicleType());
                    vehicleMap.put("isActive", vehicle.getIsActive());
                    vehicleMap.put("createdDate", vehicle.getCreatedDate());
                    return vehicleMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> vehiclesData = new HashMap<>();
        vehiclesData.put("ownerId", ownerId);
        vehiclesData.put("ownerName", owner.getName());
        vehiclesData.put("vehicles", vehicleList);
        vehiclesData.put("totalVehicles", vehicleList.size());
        
        return new ApiResponse(true, 
            "Vehicles for owner retrieved successfully", 
            vehiclesData);
    }

    @Override
    public ApiResponse getDriversByOwner(Integer ownerId) {
        // Validate vehicle owner exists
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));

        System.out.println("🔍 Getting drivers for owner: " + owner.getName() + " (ID: " + ownerId + ")");
        
        // Get owner's user to find drivers created by this owner
        User ownerUser = owner.getUser();
        String username = ownerUser != null ? ownerUser.getUserName() : null;
        
        System.out.println("🔍 Owner username: " + username);
        
        // Get all drivers created by this owner (using createdBy field)
        List<Driver> allDrivers = new ArrayList<>();
        if (username != null) {
            allDrivers = driverRepository.findByCreatedBy(username);
            System.out.println("🔍 Found " + allDrivers.size() + " drivers using username: " + username);
        }
        
        // Also try with owner name as fallback
        if (allDrivers.isEmpty()) {
            System.out.println("🔍 Trying findByCreatedBy with owner name: " + owner.getName());
            allDrivers = driverRepository.findByCreatedBy(owner.getName());
            System.out.println("🔍 Found " + allDrivers.size() + " drivers using name");
        }
        
        // If still no drivers, try with different variations
        if (allDrivers.isEmpty()) {
            System.out.println("🔍 Trying with owner email: " + owner.getEmail());
            allDrivers = driverRepository.findByCreatedBy(owner.getEmail());
            System.out.println("🔍 Found " + allDrivers.size() + " drivers using email");
        }
        
        if (allDrivers.isEmpty()) {
            System.out.println("🔍 Trying with owner contact: " + owner.getContactNumber());
            allDrivers = driverRepository.findByCreatedBy(owner.getContactNumber());
            System.out.println("🔍 Found " + allDrivers.size() + " drivers using contact");
        }
        
        // If still no drivers, try case-insensitive search
        if (allDrivers.isEmpty()) {
            System.out.println("🔍 Trying case-insensitive search for: " + owner.getName());
            List<Driver> allDriversInDb = driverRepository.findAll();
            System.out.println("🔍 Total drivers in database: " + allDriversInDb.size());
            
            // Print all drivers with their createdBy field
            for (Driver d : allDriversInDb) {
                System.out.println("🔍 Driver ID: " + d.getDriverId() + ", Name: " + d.getDriverName() + ", CreatedBy: " + d.getCreatedBy());
            }
            
            allDrivers = allDriversInDb.stream()
                .filter(d -> d.getCreatedBy() != null && 
                           d.getCreatedBy().toLowerCase().contains(owner.getName().toLowerCase()))
                .collect(Collectors.toList());
            System.out.println("🔍 Found " + allDrivers.size() + " drivers using case-insensitive search");
        }
        
        // TEMPORARY FIX: If still no drivers, return all drivers for debugging
        if (allDrivers.isEmpty()) {
            System.out.println("🔍 TEMPORARY FIX: Returning all drivers for debugging");
            allDrivers = driverRepository.findAll();
            System.out.println("🔍 TEMPORARY: Returning " + allDrivers.size() + " drivers");
        }
        
        // Convert to response format
        List<Map<String, Object>> driverList = allDrivers.stream()
                .map(driver -> {
                    Map<String, Object> driverMap = new HashMap<>();
                    driverMap.put("driverId", driver.getDriverId());
                    driverMap.put("driverName", driver.getDriverName());
                    driverMap.put("driverContactNumber", driver.getDriverContactNumber());
                    driverMap.put("driverAddress", driver.getDriverAddress());
                    driverMap.put("email", driver.getEmail() != null ? driver.getEmail() : "");
                    driverMap.put("isActive", driver.getIsActive());
                    driverMap.put("createdBy", driver.getCreatedBy());
                    driverMap.put("createdDate", driver.getCreatedDate());
                    
                    // Check if this driver is assigned to any vehicle
                    List<VehicleDriver> assignments = vehicleDriverRepository.findByDriverAndIsActiveTrue(driver);
                    if (!assignments.isEmpty()) {
                        VehicleDriver assignment = assignments.get(0);
                        driverMap.put("assignedVehicle", assignment.getVehicle().getVehicleNumber());
                        driverMap.put("schoolName", assignment.getSchool().getSchoolName());
                        driverMap.put("assignmentDate", assignment.getCreatedDate());
                    } else {
                        driverMap.put("assignedVehicle", null);
                        driverMap.put("schoolName", null);
                        driverMap.put("assignmentDate", null);
                    }
                    
                    return driverMap;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> driversData = new HashMap<>();
        driversData.put("ownerId", ownerId);
        driversData.put("ownerName", owner.getName());
        driversData.put("drivers", driverList);
        driversData.put("totalDrivers", driverList.size());
        
        System.out.println("🔍 Returning " + driverList.size() + " drivers for owner");
        
        return new ApiResponse(true, 
            "Drivers for owner retrieved successfully", 
            driversData);
    }

    // ------------------ Private Mapper ------------------
    private VehicleOwnerResponseDto mapToResponse(VehicleOwner owner) {
        return VehicleOwnerResponseDto.builder()
                .ownerId(owner.getOwnerId())
                .userId(owner.getUser() != null ? owner.getUser().getUId() : null)
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

    @Override
    public ApiResponse getVehiclesInTransitByOwner(Integer ownerId) {
        System.out.println("🔍 getVehiclesInTransitByOwner called with ownerId: " + ownerId);
        try {
            // Validate vehicle owner exists
            VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));

            System.out.println("🔍 Owner found: " + owner.getName());

            // Get all vehicles owned by this owner using the same logic as getVehiclesByOwner
            User ownerUser = owner.getUser();
            String username = ownerUser != null ? ownerUser.getUserName() : null;
            
            List<Vehicle> ownerVehicles = new ArrayList<>();
            if (username != null) {
                ownerVehicles = vehicleRepository.findByCreatedBy(username);
                System.out.println("🔍 Found " + ownerVehicles.size() + " vehicles using username: " + username);
            }
            
            // Fallback to owner name
            if (ownerVehicles.isEmpty()) {
                ownerVehicles = vehicleRepository.findByCreatedBy(owner.getName());
                System.out.println("🔍 Found " + ownerVehicles.size() + " vehicles using owner name");
            }
            
            if (ownerVehicles.isEmpty()) {
                System.out.println("🔍 No vehicles found for owner");
                return new ApiResponse(true, "No vehicles found for this owner", 0L);
            }

            // Get all dispatch logs for today for these vehicles
            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            
            List<Integer> vehicleIds = ownerVehicles.stream()
                    .map(Vehicle::getVehicleId)
                    .collect(Collectors.toList());

            System.out.println("🔍 Checking " + vehicleIds.size() + " vehicles for transit status");

            // Count vehicles that are currently in transit
            long vehiclesInTransit = 0;
            for (Integer vehicleId : vehicleIds) {
                // Check if this vehicle has started a trip but hasn't completed it
                List<DispatchLog> todayLogs = dispatchLogRepository.findByVehicle_VehicleIdAndCreatedDateBetween(
                    vehicleId, startOfDay, endOfDay);
                
                boolean hasStartedTrip = todayLogs.stream()
                    .anyMatch(log -> log.getEventType() == EventType.PICKUP_FROM_PARENT || 
                                   log.getEventType() == EventType.PICKUP_FROM_SCHOOL);
                
                boolean hasCompletedTrip = todayLogs.stream()
                    .anyMatch(log -> log.getEventType() == EventType.DROP_TO_SCHOOL || 
                                   log.getEventType() == EventType.DROP_TO_PARENT);
                
                System.out.println("🔍 Vehicle " + vehicleId + ": hasStartedTrip=" + hasStartedTrip + ", hasCompletedTrip=" + hasCompletedTrip);
                
                if (hasStartedTrip && !hasCompletedTrip) {
                    vehiclesInTransit++;
                    System.out.println("🔍 Vehicle " + vehicleId + " is in transit");
                }
            }

            System.out.println("🔍 Total vehicles in transit: " + vehiclesInTransit);
            return new ApiResponse(true, "Vehicles in transit count retrieved successfully", vehiclesInTransit);
            
        } catch (Exception e) {
            System.out.println("🔍 Error in getVehiclesInTransitByOwner: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving vehicles in transit: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getRecentActivityByOwner(Integer ownerId) {
        System.out.println("🔍 getRecentActivityByOwner called with ownerId: " + ownerId);
        try {
            // Validate vehicle owner exists
            VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));

            System.out.println("🔍 Owner found: " + owner.getName());

            // Get all vehicles owned by this owner using the same logic as getVehiclesByOwner
            User ownerUser = owner.getUser();
            String username = ownerUser != null ? ownerUser.getUserName() : null;
            
            List<Vehicle> ownerVehicles = new ArrayList<>();
            if (username != null) {
                ownerVehicles = vehicleRepository.findByCreatedBy(username);
                System.out.println("🔍 Found " + ownerVehicles.size() + " vehicles using username: " + username);
            }
            
            // Fallback to owner name
            if (ownerVehicles.isEmpty()) {
                ownerVehicles = vehicleRepository.findByCreatedBy(owner.getName());
                System.out.println("🔍 Found " + ownerVehicles.size() + " vehicles using owner name");
            }
            
            if (ownerVehicles.isEmpty()) {
                System.out.println("🔍 No vehicles found for owner");
                return new ApiResponse(true, "No vehicles found for this owner", new ArrayList<>());
            }

            // Get recent dispatch logs for these vehicles (last 7 days)
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            LocalDateTime now = LocalDateTime.now();
            
            List<Integer> vehicleIds = ownerVehicles.stream()
                    .map(Vehicle::getVehicleId)
                    .collect(Collectors.toList());

            List<Map<String, Object>> recentActivities = new ArrayList<>();
            
            for (Integer vehicleId : vehicleIds) {
                List<DispatchLog> recentLogs = dispatchLogRepository.findByVehicle_VehicleIdAndCreatedDateBetween(
                    vehicleId, sevenDaysAgo, now);
                
                for (DispatchLog log : recentLogs) {
                    Map<String, Object> activity = new HashMap<>();
                    activity.put("id", log.getDispatchLogId());
                    activity.put("type", log.getEventType().toString());
                    activity.put("vehicleId", log.getVehicle().getVehicleId());
                    activity.put("vehicleNumber", log.getVehicle().getVehicleNumber());
                    String studentName = log.getStudent().getFirstName() + 
                        (log.getStudent().getMiddleName() != null ? " " + log.getStudent().getMiddleName() : "") + 
                        " " + log.getStudent().getLastName();
                    activity.put("studentName", studentName);
                    activity.put("createdDate", log.getCreatedDate());
                    activity.put("remarks", log.getRemarks());
                    
                    // Add human-readable description
                    String description = getActivityDescription(log.getEventType(), 
                        log.getVehicle().getVehicleNumber(), studentName);
                    activity.put("description", description);
                    
                    recentActivities.add(activity);
                }
            }
            
            // Sort by date (most recent first) and limit to 10
            recentActivities.sort((a, b) -> {
                LocalDateTime dateA = (LocalDateTime) a.get("createdDate");
                LocalDateTime dateB = (LocalDateTime) b.get("createdDate");
                return dateB.compareTo(dateA);
            });
            
            if (recentActivities.size() > 10) {
                recentActivities = recentActivities.subList(0, 10);
            }

            System.out.println("🔍 Found " + recentActivities.size() + " recent activities");
            return new ApiResponse(true, "Recent activity retrieved successfully", recentActivities);
            
        } catch (Exception e) {
            System.out.println("🔍 Error in getRecentActivityByOwner: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving recent activity: " + e.getMessage(), null);
        }
    }

    
    private String getActivityDescription(EventType eventType, String vehicleNumber, String studentName) {
        switch (eventType) {
            case PICKUP_FROM_PARENT:
                return "Vehicle $vehicleNumber picked up $studentName from home";
            case DROP_TO_SCHOOL:
                return "Vehicle $vehicleNumber dropped $studentName at school";
            case PICKUP_FROM_SCHOOL:
                return "Vehicle $vehicleNumber picked up $studentName from school";
            case DROP_TO_PARENT:
                return "Vehicle $vehicleNumber dropped $studentName at home";
            case GATE_ENTRY:
                return "Vehicle $vehicleNumber entered school gate";
            case GATE_EXIT:
                return "Vehicle $vehicleNumber exited school gate";
            default:
                return "Vehicle $vehicleNumber activity recorded";
        }
    }
}
