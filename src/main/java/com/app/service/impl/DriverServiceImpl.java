package com.app.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.entity.Driver;
import com.app.entity.DispatchLog;
import com.app.entity.Role;
import com.app.entity.Trip;
import com.app.entity.TripStatus;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.entity.Vehicle;
import com.app.entity.VehicleDriver;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.DriverRequestDto;
import com.app.payload.request.NotificationRequestDto;
import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.request.StudentAttendanceRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.DriverDashboardResponseDto;
import com.app.payload.response.DriverResponseDto;
import com.app.payload.response.TripResponseDto;
import com.app.repository.DispatchLogRepository;
import com.app.repository.DriverRepository;
import com.app.repository.RoleRepository;
import com.app.repository.TripRepository;
import com.app.repository.TripStatusRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import com.app.repository.VehicleDriverRepository;
import com.app.service.IDriverService;
import com.app.service.IPendingUserService;

@Service
public class DriverServiceImpl implements IDriverService {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private IPendingUserService pendingUserService;
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private TripStatusRepository tripStatusRepository;
    
    @Autowired
    private VehicleDriverRepository vehicleDriverRepository;
    
    @Autowired
    private DispatchLogRepository dispatchLogRepository;
    
    @Autowired
    private com.app.repository.VehicleRepository vehicleRepository;

    @Override
    public ApiResponse createDriver(DriverRequestDto request) {
        // Note: User will be created and linked during activation, no need to check existing user

        // Check duplicate driver contact
        if (driverRepository.existsByDriverContactNumber(request.getDriverContactNumber())) {
            return new ApiResponse(false, "Driver with this contact number already exists", null);
        }

        Driver driver = Driver.builder()
                .driverName(request.getDriverName())
                .driverPhoto(request.getDriverPhoto())
                .driverContactNumber(request.getDriverContactNumber())
                .driverAddress(request.getDriverAddress())
                .email(request.getEmail())
                .isActive(request.getIsActive())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .user(null) // User will be linked during activation
                .build();

        Driver savedDriver  = driverRepository.save(driver);
        
        // Find DRIVER role
        Role role = roleRepository.findByRoleName("DRIVER")
                .orElseThrow(() -> new ResourceNotFoundException("Role DRIVER not found"));

        // Note: UserRole will be created when driver activates account via email link
        // This ensures driver sets their own username/password

        // Create PendingUser entry for activation
        PendingUserRequestDTO pendingReq = PendingUserRequestDTO.builder()
                .entityType("DRIVER")
                .entityId(savedDriver.getDriverId().longValue())
                .email(request.getEmail())
                .contactNumber(request.getDriverContactNumber())
                .roleId(role.getRoleId()) // DRIVER role (ID = 4)
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
        driver.setEmail(request.getEmail());
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
    public ApiResponse getDriverByUserId(Integer userId) {
        System.out.println("🔍 Getting driver by userId: " + userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Driver driver = driverRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found for user ID: " + userId));
        
        System.out.println("🔍 Found driver: " + driver.getDriverName() + " (ID: " + driver.getDriverId() + ")");
        
        return new ApiResponse(true, "Driver retrieved successfully", mapToResponse(driver));
    }

    @Override
    public ApiResponse getAllDrivers(Integer ownerId) {
        // Only return drivers with user credentials (activated drivers)
        List<DriverResponseDto> drivers = driverRepository.findActivatedDrivers().stream()
                .filter(d -> d.getUser() != null && d.getUser().getUId().equals(ownerId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Activated drivers fetched successfully", drivers);
    }

    @Override
    public ApiResponse getAllDriversForAdmin() {
        // Get all drivers including non-activated ones (for admin purposes)
        List<DriverResponseDto> drivers = driverRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "All drivers fetched successfully", drivers);
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

    // ================ Driver Dashboard Methods ================

    @Override
    public ApiResponse getDriverDashboard(Integer driverId) {
        System.out.println("🔍 getDriverDashboard called with driverId: " + driverId);
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            
            System.out.println("🔍 Driver found: " + driver.getDriverName() + " (ID: " + driver.getDriverId() + ")");

            // Validate that the driver has user credentials (is activated)
            if (driver.getUser() == null) {
                System.out.println("🔍 Driver has not completed user activation");
                return new ApiResponse(false, "Driver has not completed user activation. Please complete registration first.", null);
            }
            System.out.println("🔍 Driver is activated with user ID = " + driver.getUser().getUId());

            // Get driver's assigned vehicle
            List<VehicleDriver> vehicleDrivers = vehicleDriverRepository.findByDriverAndIsActiveTrue(driver);
            System.out.println("🔍 Found " + vehicleDrivers.size() + " active vehicle assignments for driver");
            
            VehicleDriver vehicleDriver = vehicleDrivers.stream().findFirst().orElse(null);

            if (vehicleDriver == null) {
                System.out.println("🔍 No active vehicle assignment found for driver: " + driver.getDriverName());
                // Return a basic dashboard without vehicle info
                DriverDashboardResponseDto dashboard = DriverDashboardResponseDto.builder()
                        .driverId(driver.getDriverId())
                        .driverName(driver.getDriverName())
                        .driverContactNumber(driver.getDriverContactNumber())
                        .driverPhoto(driver.getDriverPhoto())
                        .vehicleId(null)
                        .vehicleNumber("Not Assigned")
                        .vehicleType(null)
                        .vehicleCapacity(0)
                        .schoolId(null)
                        .schoolName("Not Assigned")
                        .totalTripsToday(0)
                        .completedTrips(0)
                        .pendingTrips(0)
                        .totalStudentsToday(0)
                        .studentsPickedUp(0)
                        .studentsDropped(0)
                        .currentTripId(null)
                        .currentTripName(null)
                        .currentTripStatus(null)
                        .currentTripStartTime(null)
                        .currentTripStudentCount(0)
                        .recentActivities(new ArrayList<>())
                        .build();
                
                return new ApiResponse(true, "Driver dashboard data retrieved successfully (No vehicle assigned)", dashboard);
            }
            
            System.out.println("🔍 Vehicle assignment found: Vehicle " + vehicleDriver.getVehicle().getVehicleNumber() + 
                             " assigned to School " + vehicleDriver.getSchool().getSchoolName());

            Vehicle vehicle = vehicleDriver.getVehicle();
            
            // Log current vehicle capacity from database
            System.out.println("🔍 Vehicle capacity from database: " + vehicle.getCapacity());
            
            // Get today's trips
            List<Trip> todayTrips = tripRepository.findByDriverAndDate(driver, LocalDate.now());
            
            // Calculate statistics
            int totalTripsToday = todayTrips.size();
            int completedTrips = (int) todayTrips.stream().filter(t -> "COMPLETED".equals(t.getTripStatus())).count();
            int pendingTrips = (int) todayTrips.stream().filter(t -> "NOT_STARTED".equals(t.getTripStatus())).count();
            
            // Get current trip
            Trip currentTrip = todayTrips.stream()
                    .filter(t -> "IN_PROGRESS".equals(t.getTripStatus()))
                    .findFirst().orElse(null);

            // Get recent activities
            List<DispatchLog> recentLogs = dispatchLogRepository.findByVehicle_VehicleIdOrderByCreatedDateDesc(vehicle.getVehicleId())
                    .stream().limit(5).collect(Collectors.toList());

            List<DriverDashboardResponseDto.RecentActivityDto> recentActivities = recentLogs.stream()
                    .map(this::mapToActivityDto)
                    .collect(Collectors.toList());

            DriverDashboardResponseDto dashboard = DriverDashboardResponseDto.builder()
                    .driverId(driver.getDriverId())
                    .driverName(driver.getDriverName())
                    .driverContactNumber(driver.getDriverContactNumber())
                    .driverPhoto(driver.getDriverPhoto())
                    .vehicleId(vehicle.getVehicleId())
                    .vehicleNumber(vehicle.getVehicleNumber())
                    .vehicleType(vehicle.getVehicleType() != null ? vehicle.getVehicleType().toString() : null)
                    .vehicleCapacity(vehicle.getCapacity())
                    .schoolId(vehicleDriver.getSchool().getSchoolId())
                    .schoolName(vehicleDriver.getSchool().getSchoolName())
                    .totalTripsToday(totalTripsToday)
                    .completedTrips(completedTrips)
                    .pendingTrips(pendingTrips)
                    .totalStudentsToday(0) // Will be calculated based on trip students
                    .studentsPickedUp(0) // Will be calculated from dispatch logs
                    .studentsDropped(0) // Will be calculated from dispatch logs
                    .currentTripId(currentTrip != null ? currentTrip.getTripId() : null)
                    .currentTripName(currentTrip != null ? currentTrip.getTripName() : null)
                    .currentTripStatus(currentTrip != null ? currentTrip.getTripStatus() : null)
                    .currentTripStartTime(currentTrip != null ? currentTrip.getTripStartTime() : null)
                    .currentTripStudentCount(0) // Will be calculated
                    .recentActivities(recentActivities)
                    .build();

            System.out.println("🔍 Dashboard data created successfully for driver: " + driver.getDriverName());
            return new ApiResponse(true, "Driver dashboard data retrieved successfully", dashboard);
        } catch (Exception e) {
            System.out.println("🔍 Error in getDriverDashboard: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving driver dashboard: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getAssignedTrips(Integer driverId) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            // Validate that the driver has user credentials (is activated)
            if (driver.getUser() == null) {
                return new ApiResponse(false, "Driver has not completed user activation. Please complete registration first.", null);
            }

            // Try multiple methods to find trips
            List<Trip> trips = tripRepository.findByDriverAndIsActiveTrue(driver);
            
            // If no trips found, try to find trips by driver ID directly
            if (trips.isEmpty()) {
                trips = tripRepository.findByDriver_DriverId(driverId);
            }
            
            // If still no trips, try to find today's trips
            if (trips.isEmpty()) {
                trips = tripRepository.findByDriver_DriverIdAndDate(driverId, LocalDate.now());
            }

            List<TripResponseDto> tripDtos = trips.stream()
                    .map(this::mapToTripResponseDto)
                    .collect(Collectors.toList());

            String message = trips.isEmpty() ? 
                "No trips assigned to this driver yet. Please contact school admin to assign trips." : 
                "Assigned trips retrieved successfully";

            return new ApiResponse(true, message, tripDtos);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving assigned trips: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getTripStudents(Integer driverId, Integer tripId) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            Trip trip = tripRepository.findByDriverAndTripId(driver, tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found or not assigned to driver"));

            TripResponseDto tripDto = mapToTripResponseDto(trip);
            return new ApiResponse(true, "Trip students retrieved successfully", tripDto);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving trip students: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse markStudentAttendance(Integer driverId, StudentAttendanceRequestDto request) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            Trip trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));

            // Create dispatch log entry
            DispatchLog dispatchLog = DispatchLog.builder()
                    .trip(trip)
                    .student(trip.getStudents() != null ? trip.getStudents().stream()
                            .filter(ts -> ts.getStudent().getStudentId().equals(request.getStudentId()))
                            .findFirst().orElseThrow(() -> new ResourceNotFoundException("Student not found in trip"))
                            .getStudent() : null)
                    .school(trip.getSchool())
                    .vehicle(trip.getVehicle())
                    .eventType(com.app.Enum.EventType.valueOf(request.getEventType()))
                    .remarks(request.getRemarks())
                    .createdBy(driver.getDriverName())
                    .createdDate(request.getEventTime() != null ? request.getEventTime() : LocalDateTime.now())
                    .build();

            dispatchLogRepository.save(dispatchLog);

            return new ApiResponse(true, "Student attendance marked successfully", null);
        } catch (Exception e) {
            return new ApiResponse(false, "Error marking student attendance: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse sendParentNotification(Integer driverId, NotificationRequestDto request) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            // TODO: Implement notification service
            // This would integrate with SMS/Email/Push notification services
            
            return new ApiResponse(true, "Parent notification sent successfully", null);
        } catch (Exception e) {
            return new ApiResponse(false, "Error sending parent notification: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse startTrip(Integer driverId, Integer tripId) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            // Validate that the driver has user credentials (is activated)
            if (driver.getUser() == null) {
                return new ApiResponse(false, "Driver has not completed user activation. Please complete registration first.", null);
            }

            Trip trip = tripRepository.findByDriverAndTripId(driver, tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found or not assigned to driver"));

            trip.setTripStatus("IN_PROGRESS");
            trip.setTripStartTime(LocalDateTime.now());
            tripRepository.save(trip);

            // Create trip status entry
            LocalDateTime startTime = LocalDateTime.now();
            TripStatus tripStatus = TripStatus.builder()
                    .trip(trip)
                    .status(TripStatus.TripStatusType.IN_PROGRESS)
                    .statusTime(startTime)
                    .startTime(startTime)
                    .remarks("Trip started by driver")
                    .createdBy(driver.getDriverName())
                    .build();
            tripStatusRepository.save(tripStatus);

            return new ApiResponse(true, "Trip started successfully", null);
        } catch (Exception e) {
            return new ApiResponse(false, "Error starting trip: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse endTrip(Integer driverId, Integer tripId) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            // Validate that the driver has user credentials (is activated)
            if (driver.getUser() == null) {
                return new ApiResponse(false, "Driver has not completed user activation. Please complete registration first.", null);
            }

            Trip trip = tripRepository.findByDriverAndTripId(driver, tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found or not assigned to driver"));

            trip.setTripStatus("COMPLETED");
            trip.setTripEndTime(LocalDateTime.now());
            tripRepository.save(trip);

            // Get the previous IN_PROGRESS status to get start time
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = null;
            
            // Find the most recent IN_PROGRESS status for this trip
            List<TripStatus> inProgressStatuses = tripStatusRepository.findByTripAndStatusOrderByStatusTimeDesc(
                trip, TripStatus.TripStatusType.IN_PROGRESS);
            
            if (!inProgressStatuses.isEmpty()) {
                startTime = inProgressStatuses.get(0).getStartTime();
            }
            
            // Create trip status entry
            TripStatus tripStatus = TripStatus.builder()
                    .trip(trip)
                    .status(TripStatus.TripStatusType.COMPLETED)
                    .statusTime(endTime)
                    .startTime(startTime)
                    .endTime(endTime)
                    .remarks("Trip completed by driver")
                    .createdBy(driver.getDriverName())
                    .build();
            tripStatusRepository.save(tripStatus);

            return new ApiResponse(true, "Trip ended successfully", null);
        } catch (Exception e) {
            return new ApiResponse(false, "Error ending trip: " + e.getMessage(), null);
        }
    }
    

    // Helper methods
    private DriverDashboardResponseDto.RecentActivityDto mapToActivityDto(DispatchLog log) {
        return DriverDashboardResponseDto.RecentActivityDto.builder()
                .activityId(log.getDispatchLogId())
                .activityType(log.getEventType().toString())
                .description(getActivityDescription(log.getEventType(), log.getVehicle().getVehicleNumber(), 
                        log.getStudent().getFirstName() + " " + log.getStudent().getLastName()))
                .activityTime(log.getCreatedDate())
                .studentName(log.getStudent().getFirstName() + " " + log.getStudent().getLastName())
                .location(log.getRemarks())
                .build();
    }

    private String getActivityDescription(com.app.Enum.EventType eventType, String vehicleNumber, String studentName) {
        switch (eventType) {
            case PICKUP_FROM_PARENT:
                return "Picked up " + studentName + " from home";
            case DROP_TO_SCHOOL:
                return "Dropped " + studentName + " at school";
            case PICKUP_FROM_SCHOOL:
                return "Picked up " + studentName + " from school";
            case DROP_TO_PARENT:
                return "Dropped " + studentName + " at home";
            case GATE_ENTRY:
                return "Vehicle " + vehicleNumber + " entered school gate";
            case GATE_EXIT:
                return "Vehicle " + vehicleNumber + " exited school gate";
            default:
                return "Activity recorded for " + studentName;
        }
    }

    private TripResponseDto mapToTripResponseDto(Trip trip) {
        return TripResponseDto.builder()
                .tripId(trip.getTripId())
                .tripName(trip.getTripName())
                .tripNumber(trip.getTripNumber())
                .tripType(trip.getTripType())
                .scheduledTime(trip.getScheduledTime())
                .estimatedDurationMinutes(trip.getEstimatedDurationMinutes())
                .isActive(trip.getIsActive())
                .vehicleId(trip.getVehicle().getVehicleId())
                .vehicleNumber(trip.getVehicle().getVehicleNumber())
                .vehicleType(trip.getVehicle().getVehicleType() != null ? trip.getVehicle().getVehicleType().toString() : null)
                .vehicleCapacity(trip.getVehicle().getCapacity())
                .schoolId(trip.getSchool().getSchoolId())
                .schoolName(trip.getSchool().getSchoolName())
                .driverId(trip.getDriver() != null ? trip.getDriver().getDriverId() : null)
                .driverName(trip.getDriver() != null ? trip.getDriver().getDriverName() : null)
                .driverContactNumber(trip.getDriver() != null ? trip.getDriver().getDriverContactNumber() : null)
                .tripStatus(trip.getTripStatus())
                .tripStartTime(trip.getTripStartTime())
                .tripEndTime(trip.getTripEndTime())
                .totalStudents(0) // Will be calculated from trip students
                .studentsPickedUp(0) // Will be calculated from dispatch logs
                .studentsDropped(0) // Will be calculated from dispatch logs
                .studentsAbsent(0) // Will be calculated
                .students(new ArrayList<>()) // Will be populated with actual student data
                .createdBy(trip.getCreatedBy())
                .createdDate(trip.getCreatedDate())
                .updatedBy(trip.getUpdatedBy())
                .updatedDate(trip.getUpdatedDate())
                .build();
    }
}

