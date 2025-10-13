package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.app.entity.SchoolVehicle;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.entity.Vehicle;
import com.app.entity.VehicleDriver;
import com.app.entity.VehicleOwner;
import com.app.entity.Trip;
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
import com.app.repository.UserRoleRepository;
import com.app.repository.VehicleDriverRepository;
import com.app.repository.VehicleOwnerRepository;
import com.app.repository.VehicleRepository;
import com.app.service.IPendingUserService;
import com.app.service.IVehicleOwnerService;
import com.app.service.IWebSocketNotificationService;

import lombok.extern.slf4j.Slf4j;


@Service
@Transactional
@Slf4j
public class VehicleOwnerServiceImpl implements IVehicleOwnerService {

	private final VehicleOwnerRepository vehicleOwnerRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final IPendingUserService pendingUserService;
	private final SchoolRepository schoolRepository;
    private final SchoolUserRepository schoolUserRepository;
    private final UserRoleRepository userRoleRepository;
    
    private final DispatchLogRepository dispatchLogRepository;
	private final VehicleRepository vehicleRepository;
	private final VehicleDriverRepository vehicleDriverRepository;
	private final DriverRepository driverRepository;
	private final SchoolVehicleRepository schoolVehicleRepository;
	private final StudentRepository studentRepository;
	private final TripRepository tripRepository;
	private final TripStudentRepository tripStudentRepository;
	private final IWebSocketNotificationService webSocketNotificationService;

	@Autowired
	public VehicleOwnerServiceImpl(
			VehicleOwnerRepository vehicleOwnerRepository,
			UserRepository userRepository,
			RoleRepository roleRepository,
			IPendingUserService pendingUserService,
			SchoolRepository schoolRepository,
            SchoolUserRepository schoolUserRepository,
            UserRoleRepository userRoleRepository,
            DispatchLogRepository dispatchLogRepository,
			VehicleRepository vehicleRepository,
			VehicleDriverRepository vehicleDriverRepository,
			DriverRepository driverRepository,
			SchoolVehicleRepository schoolVehicleRepository,
			StudentRepository studentRepository,
			TripRepository tripRepository,
			TripStudentRepository tripStudentRepository,
			IWebSocketNotificationService webSocketNotificationService) {
		this.vehicleOwnerRepository = vehicleOwnerRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.pendingUserService = pendingUserService;
		this.schoolRepository = schoolRepository;
        this.schoolUserRepository = schoolUserRepository;
        this.userRoleRepository = userRoleRepository;
        this.dispatchLogRepository = dispatchLogRepository;
		this.vehicleRepository = vehicleRepository;
		this.vehicleDriverRepository = vehicleDriverRepository;
		this.driverRepository = driverRepository;
		this.schoolVehicleRepository = schoolVehicleRepository;
		this.studentRepository = studentRepository;
		this.tripRepository = tripRepository;
		this.tripStudentRepository = tripStudentRepository;
		this.webSocketNotificationService = webSocketNotificationService;
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

            // Note: UserRole will be created via PendingUser activation flow
            // This ensures proper role assignment and avoids duplicates

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
        
        // Get vehicles through SchoolVehicle relationship (proper way)
        List<SchoolVehicle> schoolVehicles = schoolVehicleRepository.findByOwner_OwnerId(ownerId);
        List<Vehicle> vehicles = schoolVehicles.stream()
                .map(SchoolVehicle::getVehicle)
                .filter(vehicle -> vehicle.getIsActive()) // Only active vehicles
                .collect(Collectors.toList());
        
        System.out.println("🔍 Found " + vehicles.size() + " vehicles through SchoolVehicle relationship");
        
        // Fallback: If no vehicles through relationship, try createdBy field
        if (vehicles.isEmpty()) {
            User ownerUser = owner.getUser();
            String username = ownerUser != null ? ownerUser.getUserName() : null;
            
            if (username != null) {
                vehicles = vehicleRepository.findByCreatedBy(username);
                System.out.println("🔍 Fallback: Found " + vehicles.size() + " vehicles using username: " + username);
            }
            
            // Try with owner name
            if (vehicles.isEmpty()) {
                vehicles = vehicleRepository.findByCreatedBy(owner.getName());
                System.out.println("🔍 Fallback: Found " + vehicles.size() + " vehicles using owner name");
            }
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
        
        // Get ALL activated drivers created by this owner (not just assigned ones)
        List<Driver> allDrivers = new ArrayList<>();
        
        // First try with username
        if (username != null) {
            allDrivers = driverRepository.findActivatedDriversByCreatedBy(username);
            System.out.println("🔍 Found " + allDrivers.size() + " activated drivers using username: " + username);
            for (Driver driver : allDrivers) {
                System.out.println("🔍 Driver: " + driver.getDriverName() + " (ID: " + driver.getDriverId() + ")");
            }
        }
        
        // If no drivers found with username, try with owner name
        if (allDrivers.isEmpty()) {
            allDrivers = driverRepository.findActivatedDriversByCreatedBy(owner.getName());
            System.out.println("🔍 Found " + allDrivers.size() + " activated drivers using owner name: " + owner.getName());
            for (Driver driver : allDrivers) {
                System.out.println("🔍 Driver: " + driver.getDriverName() + " (ID: " + driver.getDriverId() + ")");
            }
        }
        
        // Also get drivers through VehicleDriver relationship to ensure we don't miss any
        List<VehicleDriver> vehicleDrivers = vehicleDriverRepository.findByOwnerId(ownerId);
        List<Driver> assignedDrivers = vehicleDrivers.stream()
                .map(VehicleDriver::getDriver)
                .filter(driver -> driver.getIsActive() && driver.getUser() != null)
                .collect(Collectors.toList());
        
        System.out.println("🔍 Found " + assignedDrivers.size() + " drivers through VehicleDriver relationship");
        
        // Merge both lists and remove duplicates
        Set<Driver> uniqueDrivers = new HashSet<>(allDrivers);
        uniqueDrivers.addAll(assignedDrivers);
        allDrivers = new ArrayList<>(uniqueDrivers);
        
        System.out.println("🔍 Total unique activated drivers: " + allDrivers.size());
        
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

    @Override
    public ApiResponse getDriverAssignments(Integer ownerId) {
        System.out.println("🔍 getDriverAssignments called with ownerId: " + ownerId);
        try {
            // Validate vehicle owner exists
            VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));

            System.out.println("🔍 Owner found: " + owner.getName());

            // Get all driver assignments for this owner through VehicleDriver relationship
            List<VehicleDriver> vehicleDrivers = vehicleDriverRepository.findByOwnerId(ownerId);
            System.out.println("🔍 Found " + vehicleDrivers.size() + " vehicle-driver assignments");

            // Convert to response format
            List<Map<String, Object>> assignmentList = vehicleDrivers.stream()
                    .filter(vd -> vd.getIsActive()) // Only active assignments
                    .map(vd -> {
                        Map<String, Object> assignmentMap = new HashMap<>();
                        assignmentMap.put("assignmentId", vd.getVehicleDriverId());
                        assignmentMap.put("vehicleId", vd.getVehicle().getVehicleId());
                        assignmentMap.put("vehicleNumber", vd.getVehicle().getVehicleNumber());
                        assignmentMap.put("vehicleType", vd.getVehicle().getVehicleType());
                        assignmentMap.put("driverId", vd.getDriver().getDriverId());
                        assignmentMap.put("driverName", vd.getDriver().getDriverName());
                        assignmentMap.put("driverContactNumber", vd.getDriver().getDriverContactNumber());
                        assignmentMap.put("schoolId", vd.getSchool().getSchoolId());
                        assignmentMap.put("schoolName", vd.getSchool().getSchoolName());
                        assignmentMap.put("isPrimary", vd.getIsPrimary());
                        assignmentMap.put("isActive", vd.getIsActive());
                        assignmentMap.put("startDate", vd.getStartDate());
                        assignmentMap.put("endDate", vd.getEndDate());
                        assignmentMap.put("createdDate", vd.getCreatedDate());
                        assignmentMap.put("createdBy", vd.getCreatedBy());
                        return assignmentMap;
                    })
                    .collect(Collectors.toList());

            System.out.println("🔍 Returning " + assignmentList.size() + " active assignments");
            return new ApiResponse(true, "Driver assignments retrieved successfully", assignmentList);
            
        } catch (Exception e) {
            System.out.println("🔍 Error in getDriverAssignments: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving driver assignments: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getTotalAssignmentsByOwner(Integer ownerId) {
        System.out.println("🔍 getTotalAssignmentsByOwner called with ownerId: " + ownerId);
        try {
            // Validate vehicle owner exists
            VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));

            System.out.println("🔍 Owner found: " + owner.getName());

            // Get all driver assignments for this owner through VehicleDriver relationship
            List<VehicleDriver> vehicleDrivers = vehicleDriverRepository.findByOwnerId(ownerId);
            
            // Count active assignments
            long activeAssignments = vehicleDrivers.stream()
                    .filter(vd -> vd.getIsActive())
                    .count();

            System.out.println("🔍 Total active assignments: " + activeAssignments);
            return new ApiResponse(true, "Total assignments retrieved successfully", activeAssignments);
            
        } catch (Exception e) {
            System.out.println("🔍 Error in getTotalAssignmentsByOwner: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving total assignments: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getPendingDriverRegistrations(Integer ownerId) {
        System.out.println("🔍 getPendingDriverRegistrations called with ownerId: " + ownerId);
        try {
            // Validate vehicle owner exists
            VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle owner not found with ID: " + ownerId));

            System.out.println("🔍 Owner found: " + owner.getName());

            // Get owner's user to find drivers created by this owner
            User ownerUser = owner.getUser();
            String username = ownerUser != null ? ownerUser.getUserName() : null;
            
            // Get all drivers created by this owner (including non-activated ones)
            List<Driver> allDrivers = new ArrayList<>();
            if (username != null) {
                allDrivers = driverRepository.findByCreatedBy(username);
            }
            
            // Try with owner name if no drivers found with username
            if (allDrivers.isEmpty()) {
                allDrivers = driverRepository.findByCreatedBy(owner.getName());
            }
            
            // Filter to get only non-activated drivers (drivers without user credentials)
            List<Driver> pendingDrivers = allDrivers.stream()
                    .filter(driver -> driver.getUser() == null && driver.getIsActive())
                    .collect(Collectors.toList());

            System.out.println("🔍 Found " + pendingDrivers.size() + " pending driver registrations");
            return new ApiResponse(true, "Pending driver registrations retrieved successfully", pendingDrivers.size());
            
        } catch (Exception e) {
            System.out.println("🔍 Error in getPendingDriverRegistrations: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving pending driver registrations: " + e.getMessage(), null);
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

    // ========== TRIP ASSIGNMENT METHODS ==========

    @Override
    public ApiResponse getTripsByOwner(Integer ownerId) {
        try {
            log.info("Getting trips for owner: {}", ownerId);
            
            // Get all vehicles owned by this owner
            List<SchoolVehicle> schoolVehicles = schoolVehicleRepository.findByOwner_OwnerId(ownerId);
            if (schoolVehicles.isEmpty()) {
                return new ApiResponse(true, "No vehicles found for this owner", new ArrayList<>());
            }
            
            // Get vehicle IDs
            List<Integer> vehicleIds = schoolVehicles.stream()
                    .map(sv -> sv.getVehicle().getVehicleId())
                    .collect(Collectors.toList());
            
            // Get all trips for these vehicles
            List<Trip> trips = tripRepository.findByVehicleVehicleIdIn(vehicleIds);
            
            // Map to response DTOs
            List<Map<String, Object>> tripData = trips.stream()
                    .map(this::mapTripToResponse)
                    .collect(Collectors.toList());
            
            return new ApiResponse(true, "Trips retrieved successfully", tripData);
            
        } catch (Exception e) {
            log.error("Error getting trips for owner {}: {}", ownerId, e.getMessage(), e);
            return new ApiResponse(false, "Error retrieving trips: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getAvailableVehiclesForTrip(Integer ownerId, Integer schoolId) {
        try {
            log.info("Getting available vehicles for owner: {} and school: {}", ownerId, schoolId);
            
            // Get vehicles owned by this owner and associated with this school
            List<SchoolVehicle> schoolVehicles = schoolVehicleRepository
                    .findByOwner_OwnerIdAndSchool_SchoolId(ownerId, schoolId);
            
            if (schoolVehicles.isEmpty()) {
                return new ApiResponse(true, "No vehicles available for this school", new ArrayList<>());
            }
            
            // Map to response format
            List<Map<String, Object>> vehicleData = schoolVehicles.stream()
                    .map(sv -> {
                        Vehicle vehicle = sv.getVehicle();
                        Map<String, Object> vehicleMap = new HashMap<>();
                        vehicleMap.put("vehicleId", vehicle.getVehicleId());
                        vehicleMap.put("vehicleNumber", vehicle.getVehicleNumber());
                        vehicleMap.put("registrationNumber", vehicle.getRegistrationNumber());
                        vehicleMap.put("vehicleType", vehicle.getVehicleType().toString());
                        vehicleMap.put("capacity", vehicle.getCapacity());
                        vehicleMap.put("isActive", vehicle.getIsActive());
                        
                        // Get assigned driver info
                        List<VehicleDriver> vehicleDrivers = vehicleDriverRepository
                                .findByVehicleAndIsActiveTrue(vehicle);
                        if (!vehicleDrivers.isEmpty()) {
                            VehicleDriver primaryDriver = vehicleDrivers.stream()
                                    .filter(VehicleDriver::getIsPrimary)
                                    .findFirst()
                                    .orElse(vehicleDrivers.get(0));
                            
                            Driver driver = primaryDriver.getDriver();
                            vehicleMap.put("assignedDriverId", driver.getDriverId());
                            vehicleMap.put("assignedDriverName", driver.getDriverName());
                            vehicleMap.put("assignedDriverContact", driver.getDriverContactNumber());
                            vehicleMap.put("hasAssignedDriver", driver.getUser() != null);
                        } else {
                            vehicleMap.put("assignedDriverId", null);
                            vehicleMap.put("assignedDriverName", "No Driver Assigned");
                            vehicleMap.put("assignedDriverContact", null);
                            vehicleMap.put("hasAssignedDriver", false);
                        }
                        
                        return vehicleMap;
                    })
                    .collect(Collectors.toList());
            
            return new ApiResponse(true, "Available vehicles retrieved successfully", vehicleData);
            
        } catch (Exception e) {
            log.error("Error getting available vehicles for owner {} and school {}: {}", 
                    ownerId, schoolId, e.getMessage(), e);
            return new ApiResponse(false, "Error retrieving vehicles: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse assignTripToVehicle(Integer tripId, Integer vehicleId, String updatedBy) {
        try {
            log.info("Assigning trip {} to vehicle {} by {}", tripId, vehicleId, updatedBy);
            
            // Get trip
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));
            
            // Get vehicle
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));
            
            // Verify vehicle belongs to the owner (through school association)
            SchoolVehicle schoolVehicle = schoolVehicleRepository
                    .findByVehicle_VehicleIdAndSchool_SchoolId(vehicleId, trip.getSchool().getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Vehicle is not associated with the trip's school"));
            
            // Update trip with new vehicle
            trip.setVehicle(vehicle);
            trip.setUpdatedBy(updatedBy);
            trip.setUpdatedDate(LocalDateTime.now());
            
            // Auto-assign driver if vehicle has an assigned driver
            List<VehicleDriver> vehicleDrivers = vehicleDriverRepository
                    .findByVehicleAndIsActiveTrue(vehicle);
            if (!vehicleDrivers.isEmpty()) {
                VehicleDriver primaryDriver = vehicleDrivers.stream()
                        .filter(VehicleDriver::getIsPrimary)
                        .findFirst()
                        .orElse(vehicleDrivers.get(0));
                
                Driver driver = primaryDriver.getDriver();
                if (driver.getUser() != null) { // Only assign if driver is activated
                    trip.setDriver(driver);
                    log.info("Auto-assigned driver {} to trip {}", driver.getDriverName(), tripId);
                }
            }
            
            tripRepository.save(trip);
            
            // Map to response
            Map<String, Object> responseData = mapTripToResponse(trip);
            
            return new ApiResponse(true, "Trip assigned to vehicle successfully", responseData);
            
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            return new ApiResponse(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error assigning trip {} to vehicle {}: {}", tripId, vehicleId, e.getMessage(), e);
            return new ApiResponse(false, "Error assigning trip: " + e.getMessage(), null);
        }
    }

    /**
     * Maps Trip entity to response format
     */
    private Map<String, Object> mapTripToResponse(Trip trip) {
        Map<String, Object> tripMap = new HashMap<>();
        tripMap.put("tripId", trip.getTripId());
        tripMap.put("tripName", trip.getTripName());
        tripMap.put("tripNumber", trip.getTripNumber());
        tripMap.put("tripType", trip.getTripType() != null ? trip.getTripType().name() : null);
        tripMap.put("tripTypeDisplay", trip.getTripType() != null ? trip.getTripType().getDisplayName() : null);
        tripMap.put("routeName", trip.getRouteName());
        tripMap.put("routeDescription", trip.getRouteDescription());
        tripMap.put("scheduledTime", trip.getScheduledTime());
        tripMap.put("estimatedDurationMinutes", trip.getEstimatedDurationMinutes());
        tripMap.put("tripStatus", trip.getTripStatus());
        tripMap.put("tripStartTime", trip.getTripStartTime());
        tripMap.put("tripEndTime", trip.getTripEndTime());
        tripMap.put("isActive", trip.getIsActive());
        tripMap.put("createdBy", trip.getCreatedBy());
        tripMap.put("createdDate", trip.getCreatedDate());
        tripMap.put("updatedBy", trip.getUpdatedBy());
        tripMap.put("updatedDate", trip.getUpdatedDate());
        
        // School info
        if (trip.getSchool() != null) {
            Map<String, Object> schoolMap = new HashMap<>();
            schoolMap.put("schoolId", trip.getSchool().getSchoolId());
            schoolMap.put("schoolName", trip.getSchool().getSchoolName());
            tripMap.put("school", schoolMap);
        }
        
        // Vehicle info
        if (trip.getVehicle() != null) {
            Map<String, Object> vehicleMap = new HashMap<>();
            vehicleMap.put("vehicleId", trip.getVehicle().getVehicleId());
            vehicleMap.put("vehicleNumber", trip.getVehicle().getVehicleNumber());
            vehicleMap.put("registrationNumber", trip.getVehicle().getRegistrationNumber());
            vehicleMap.put("vehicleType", trip.getVehicle().getVehicleType() != null ? 
                    trip.getVehicle().getVehicleType().toString() : null);
            vehicleMap.put("capacity", trip.getVehicle().getCapacity());
            tripMap.put("vehicle", vehicleMap);
        }
        
        // Driver info
        if (trip.getDriver() != null) {
            Map<String, Object> driverMap = new HashMap<>();
            driverMap.put("driverId", trip.getDriver().getDriverId());
            driverMap.put("driverName", trip.getDriver().getDriverName());
            driverMap.put("driverContactNumber", trip.getDriver().getDriverContactNumber());
            driverMap.put("isActivated", trip.getDriver().getUser() != null);
            tripMap.put("driver", driverMap);
        }
        
        return tripMap;
    }

    // ========== ENHANCED DRIVER MANAGEMENT METHODS ==========

    @Override
    public ApiResponse setDriverAvailability(Integer vehicleDriverId, Boolean isAvailable, String reason, String updatedBy) {
        try {
            log.info("Setting driver availability for vehicleDriverId: {}, isAvailable: {}, reason: {}", 
                    vehicleDriverId, isAvailable, reason);
            
            VehicleDriver vehicleDriver = vehicleDriverRepository.findById(vehicleDriverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle-Driver assignment not found with ID: " + vehicleDriverId));
            
            vehicleDriver.setIsAvailable(isAvailable);
            vehicleDriver.setUnavailabilityReason(isAvailable ? null : reason);
            vehicleDriver.setUpdatedBy(updatedBy);
            vehicleDriver.setUpdatedDate(LocalDateTime.now());
            
            vehicleDriverRepository.save(vehicleDriver);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("vehicleDriverId", vehicleDriverId);
            responseData.put("isAvailable", isAvailable);
            responseData.put("reason", reason);
            responseData.put("driverName", vehicleDriver.getDriver().getDriverName());
            responseData.put("vehicleNumber", vehicleDriver.getVehicle().getVehicleNumber());
            
            return new ApiResponse(true, "Driver availability updated successfully", responseData);
            
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            return new ApiResponse(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error setting driver availability: {}", e.getMessage(), e);
            return new ApiResponse(false, "Error updating driver availability: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse setBackupDriver(Integer vehicleDriverId, Boolean isBackup, String updatedBy) {
        try {
            log.info("Setting backup driver for vehicleDriverId: {}, isBackup: {}", vehicleDriverId, isBackup);
            
            VehicleDriver vehicleDriver = vehicleDriverRepository.findById(vehicleDriverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle-Driver assignment not found with ID: " + vehicleDriverId));
            
            vehicleDriver.setIsBackupDriver(isBackup);
            vehicleDriver.setUpdatedBy(updatedBy);
            vehicleDriver.setUpdatedDate(LocalDateTime.now());
            
            vehicleDriverRepository.save(vehicleDriver);
            
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("vehicleDriverId", vehicleDriverId);
            responseData.put("isBackupDriver", isBackup);
            responseData.put("driverName", vehicleDriver.getDriver().getDriverName());
            responseData.put("vehicleNumber", vehicleDriver.getVehicle().getVehicleNumber());
            
            return new ApiResponse(true, "Backup driver status updated successfully", responseData);
            
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            return new ApiResponse(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error setting backup driver: {}", e.getMessage(), e);
            return new ApiResponse(false, "Error updating backup driver status: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getDriverRotationSchedule(Integer ownerId) {
        try {
            log.info("Getting driver rotation schedule for owner: {}", ownerId);
            
            // Get all vehicle-driver assignments for this owner
            List<VehicleDriver> vehicleDrivers = vehicleDriverRepository.findByOwnerId(ownerId);
            
            // Group by vehicle
            Map<Integer, List<VehicleDriver>> vehicleDriverMap = vehicleDrivers.stream()
                    .collect(Collectors.groupingBy(vd -> vd.getVehicle().getVehicleId()));
            
            List<Map<String, Object>> rotationSchedule = new ArrayList<>();
            
            for (Map.Entry<Integer, List<VehicleDriver>> entry : vehicleDriverMap.entrySet()) {
                Vehicle vehicle = entry.getValue().get(0).getVehicle();
                List<VehicleDriver> drivers = entry.getValue();
                
                Map<String, Object> vehicleSchedule = new HashMap<>();
                vehicleSchedule.put("vehicleId", vehicle.getVehicleId());
                vehicleSchedule.put("vehicleNumber", vehicle.getVehicleNumber());
                vehicleSchedule.put("vehicleType", vehicle.getVehicleType().toString());
                
                // Separate primary and backup drivers
                List<Map<String, Object>> primaryDrivers = new ArrayList<>();
                List<Map<String, Object>> backupDrivers = new ArrayList<>();
                
                for (VehicleDriver vd : drivers) {
                    Map<String, Object> driverInfo = new HashMap<>();
                    driverInfo.put("vehicleDriverId", vd.getVehicleDriverId());
                    driverInfo.put("driverId", vd.getDriver().getDriverId());
                    driverInfo.put("driverName", vd.getDriver().getDriverName());
                    driverInfo.put("driverContact", vd.getDriver().getDriverContactNumber());
                    driverInfo.put("isAvailable", vd.getIsAvailable());
                    driverInfo.put("unavailabilityReason", vd.getUnavailabilityReason());
                    driverInfo.put("isActivated", vd.getDriver().getUser() != null);
                    driverInfo.put("startDate", vd.getStartDate());
                    driverInfo.put("endDate", vd.getEndDate());
                    
                    if (Boolean.TRUE.equals(vd.getIsBackupDriver())) {
                        backupDrivers.add(driverInfo);
                    } else {
                        primaryDrivers.add(driverInfo);
                    }
                }
                
                vehicleSchedule.put("primaryDrivers", primaryDrivers);
                vehicleSchedule.put("backupDrivers", backupDrivers);
                vehicleSchedule.put("totalDrivers", drivers.size());
                vehicleSchedule.put("availableDrivers", drivers.stream()
                        .mapToInt(vd -> Boolean.TRUE.equals(vd.getIsAvailable()) ? 1 : 0)
                        .sum());
                
                rotationSchedule.add(vehicleSchedule);
            }
            
            return new ApiResponse(true, "Driver rotation schedule retrieved successfully", rotationSchedule);
            
        } catch (Exception e) {
            log.error("Error getting driver rotation schedule for owner {}: {}", ownerId, e.getMessage(), e);
            return new ApiResponse(false, "Error retrieving driver rotation schedule: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse reassignTripDriver(Integer tripId, Integer newDriverId, String updatedBy) {
        try {
            log.info("Reassigning trip {} to driver {} by {}", tripId, newDriverId, updatedBy);
            
            // Get trip
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));
            
            // Get new driver
            Driver newDriver = driverRepository.findById(newDriverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + newDriverId));
            
            // Verify driver is activated
            if (newDriver.getUser() == null) {
                return new ApiResponse(false, "Cannot assign trip to driver. Driver must complete user activation first.", null);
            }
            
            // Verify driver is assigned to the same vehicle as the trip
            List<VehicleDriver> vehicleDrivers = vehicleDriverRepository
                    .findByVehicleAndIsActiveTrue(trip.getVehicle());
            
            boolean driverAssignedToVehicle = vehicleDrivers.stream()
                    .anyMatch(vd -> vd.getDriver().getDriverId().equals(newDriverId));
            
            if (!driverAssignedToVehicle) {
                return new ApiResponse(false, "Driver is not assigned to the trip's vehicle", null);
            }
            
            // Update trip with new driver
            trip.setDriver(newDriver);
            trip.setUpdatedBy(updatedBy);
            trip.setUpdatedDate(LocalDateTime.now());
            
            tripRepository.save(trip);
            
            // Map to response
            Map<String, Object> responseData = mapTripToResponse(trip);
            
            return new ApiResponse(true, "Trip driver reassigned successfully", responseData);
            
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found: {}", e.getMessage());
            return new ApiResponse(false, e.getMessage(), null);
        } catch (Exception e) {
            log.error("Error reassigning trip driver: {}", e.getMessage(), e);
            return new ApiResponse(false, "Error reassigning trip driver: " + e.getMessage(), null);
        }
    }
}
