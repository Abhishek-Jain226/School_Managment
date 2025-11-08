package com.app.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.Driver;
import com.app.entity.DispatchLog;
import com.app.entity.Notification;
import com.app.entity.Role;
import com.app.entity.Trip;
import com.app.entity.TripStudent;
import com.app.entity.TripStatus;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.entity.Vehicle;
import com.app.entity.VehicleDriver;
import com.app.entity.VehicleLocation;
import com.app.Enum.EventType;
import com.app.Enum.NotificationType;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.DriverRequestDto;
import com.app.payload.request.NotificationRequestDto;
import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.request.StudentAttendanceRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.DriverDashboardResponseDto;
import com.app.payload.response.DriverProfileResponseDto;
import com.app.payload.response.DriverReportsResponseDto;
import com.app.payload.response.DriverResponseDto;
import com.app.payload.response.DispatchLogResponseDto;
import com.app.payload.response.TimeBasedTripsResponseDto;
import com.app.payload.response.TripResponseDto;
import com.app.payload.response.WebSocketNotificationDto;
import com.app.repository.DispatchLogRepository;
import com.app.repository.DriverRepository;
import com.app.repository.NotificationRepository;
import com.app.repository.RoleRepository;
import com.app.repository.StudentParentRepository;
import com.app.repository.TripRepository;
import com.app.repository.TripStatusRepository;
import com.app.repository.TripStudentRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import com.app.repository.VehicleDriverRepository;
import com.app.repository.VehicleLocationRepository;
import com.app.service.IDriverService;
import com.app.service.IPendingUserService;
import com.app.service.IWebSocketNotificationService;

@Service
public class DriverServiceImpl implements IDriverService {

    private static final Logger logger = LoggerFactory.getLogger(DriverServiceImpl.class);

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
    private IWebSocketNotificationService webSocketNotificationService;
    
    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private TripStatusRepository tripStatusRepository;
    
    @Autowired
    private TripStudentRepository tripStudentRepository;
    
    @Autowired
    private VehicleDriverRepository vehicleDriverRepository;
    
    @Autowired
    private DispatchLogRepository dispatchLogRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private StudentParentRepository studentParentRepository;
    
    @Autowired
    private com.app.repository.VehicleRepository vehicleRepository;

    @Autowired
    private VehicleLocationRepository vehicleLocationRepository;

    @Override
    @Transactional
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
        logger.debug("getDriverDashboard called with driverId: {}", driverId);
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            
            logger.debug("Driver found: {} (ID: {})", driver.getDriverName(), driver.getDriverId());

            // Validate that the driver has user credentials (is activated)
            if (driver.getUser() == null) {
                logger.warn("Driver has not completed user activation: {}", driver.getDriverName());
                return new ApiResponse(false, "Driver has not completed user activation. Please complete registration first.", null);
            }
            logger.debug("Driver is activated with user ID = {}", driver.getUser().getUId());

            // Get driver's assigned vehicle
            List<VehicleDriver> vehicleDrivers = vehicleDriverRepository.findByDriverAndIsActiveTrue(driver);
            logger.debug("Found {} active vehicle assignments for driver", vehicleDrivers.size());
            
            VehicleDriver vehicleDriver = vehicleDrivers.stream().findFirst().orElse(null);

            if (vehicleDriver == null) {
                logger.warn("No active vehicle assignment found for driver: {}", driver.getDriverName());
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
            
            logger.debug("Vehicle assignment found: Vehicle {} assigned to School {}", 
                        vehicleDriver.getVehicle().getVehicleNumber(), vehicleDriver.getSchool().getSchoolName());

            Vehicle vehicle = vehicleDriver.getVehicle();
            
            // Log current vehicle capacity from database
            logger.debug("Vehicle capacity from database: {}", vehicle.getCapacity());
            
            // Get all trips assigned to this driver (don't filter by date or active status)
            // This ensures we show all trips that are assigned to the driver
            List<Trip> allDriverTrips = tripRepository.findByDriver(driver);
            logger.debug("Found {} total trips assigned to driver", allDriverTrips.size());
            
            // Filter for active trips only (for dashboard display)
            List<Trip> activeTrips = allDriverTrips.stream()
                    .filter(t -> Boolean.TRUE.equals(t.getIsActive()))
                    .collect(Collectors.toList());
            logger.debug("Found {} active trips assigned to driver", activeTrips.size());
            
            // If no active trips, use all trips (for statistics)
            List<Trip> tripsForStats = activeTrips.isEmpty() ? allDriverTrips : activeTrips;
            
            // Calculate statistics based on all assigned trips
            int totalTripsToday = tripsForStats.size();
            int completedTrips = (int) tripsForStats.stream()
                    .filter(t -> "COMPLETED".equals(t.getTripStatus()) || "ENDED".equals(t.getTripStatus()))
                    .count();
            int pendingTrips = (int) tripsForStats.stream()
                    .filter(t -> "NOT_STARTED".equals(t.getTripStatus()) || "SCHEDULED".equals(t.getTripStatus()))
                    .count();
            
            // Calculate student statistics across all assigned trips
            int totalStudentsToday = 0;
            int studentsPickedUp = 0;
            int studentsDropped = 0;
            
            logger.debug("Calculating student statistics for {} trips", tripsForStats.size());
            
            for (Trip trip : tripsForStats) {
                // Count total students in trip
                int tripStudents = tripStudentRepository.countByTrip(trip);
                totalStudentsToday += tripStudents;
                logger.debug("Trip {} has {} students", trip.getTripName(), tripStudents);
                
                // Count students picked up and dropped based on dispatch logs
                List<DispatchLog> tripLogs = dispatchLogRepository.findByTripAndVehicle(trip, vehicle);
                int pickupCount = (int) tripLogs.stream()
                    .filter(log -> log.getEventType() == EventType.PICKUP_FROM_PARENT || 
                                  log.getEventType() == EventType.PICKUP_FROM_SCHOOL)
                    .count();
                int dropCount = (int) tripLogs.stream()
                    .filter(log -> log.getEventType() == EventType.DROP_TO_SCHOOL || 
                                  log.getEventType() == EventType.DROP_TO_PARENT)
                    .count();
                
                studentsPickedUp += pickupCount;
                studentsDropped += dropCount;
                
                System.out.println("🔍 Trip " + trip.getTripName() + " - Pickups: " + pickupCount + ", Drops: " + dropCount);
            }
            
            System.out.println("🔍 Total trips: " + totalTripsToday + ", Total students: " + totalStudentsToday + 
                             ", Picked up: " + studentsPickedUp + ", Dropped: " + studentsDropped);
            
            // Get current trip (in progress)
            Trip currentTrip = tripsForStats.stream()
                    .filter(t -> "IN_PROGRESS".equals(t.getTripStatus()) || "STARTED".equals(t.getTripStatus()))
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
                    .totalStudentsToday(totalStudentsToday)
                    .studentsPickedUp(studentsPickedUp)
                    .studentsDropped(studentsDropped)
                    .currentTripId(currentTrip != null ? currentTrip.getTripId() : null)
                    .currentTripName(currentTrip != null ? currentTrip.getTripName() : null)
                    .currentTripStatus(currentTrip != null ? currentTrip.getTripStatus() : null)
                    .currentTripStartTime(currentTrip != null ? currentTrip.getTripStartTime() : null)
                    .currentTripStudentCount(currentTrip != null ? tripStudentRepository.countByTrip(currentTrip) : 0)
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
            System.out.println("🔍 getAssignedTrips called for driverId: " + driverId);
            
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            System.out.println("🔍 Driver found: " + driver.getDriverName() + " (ID: " + driver.getDriverId() + ")");

            // Validate that the driver has user credentials (is activated)
            if (driver.getUser() == null) {
                System.out.println("🔍 Driver has no user credentials - not activated");
                return new ApiResponse(false, "Driver has not completed user activation. Please complete registration first.", null);
            }

            System.out.println("🔍 Driver is activated, searching for trips...");

            // Get all trips assigned to this driver (regardless of active status or date)
            // This ensures we show all trips that are assigned to the driver
            List<Trip> allTrips = tripRepository.findByDriver(driver);
            System.out.println("🔍 Found " + allTrips.size() + " total trips assigned to driver");
            
            // Filter for active trips (preferred for display)
            List<Trip> activeTrips = allTrips.stream()
                    .filter(t -> Boolean.TRUE.equals(t.getIsActive()))
                    .collect(Collectors.toList());
            System.out.println("🔍 Found " + activeTrips.size() + " active trips");
            
            // Use active trips if available, otherwise use all trips
            List<Trip> trips = activeTrips.isEmpty() ? allTrips : activeTrips;
            System.out.println("🔍 Using " + trips.size() + " trips for display");

            System.out.println("🔍 Final trip count: " + trips.size());
            for (Trip trip : trips) {
                System.out.println("🔍 Trip: " + trip.getTripName() + " (Status: " + trip.getTripStatus() + ", Active: " + trip.getIsActive() + ")");
            }

            List<TripResponseDto> tripDtos = trips.stream()
                    .map(this::mapToTripResponseDto)
                    .collect(Collectors.toList());

            String message = trips.isEmpty() ? 
                "No trips assigned to this driver yet. Please contact school admin to assign trips." : 
                "Assigned trips retrieved successfully";

            System.out.println("🔍 Returning " + tripDtos.size() + " trips with message: " + message);
            return new ApiResponse(true, message, tripDtos);
        } catch (Exception e) {
            System.out.println("🔍 Error in getAssignedTrips: " + e.getMessage());
            e.printStackTrace();
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
    @Transactional
    public ApiResponse markStudentAttendance(Integer driverId, StudentAttendanceRequestDto request) {
        try {
            System.out.println("🔍 markStudentAttendance called - driverId: " + driverId + ", tripId: " + request.getTripId() + ", studentId: " + request.getStudentId() + ", eventType: " + request.getEventType());
            
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            System.out.println("🔍 Driver found: " + driver.getDriverName());

            Trip trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));
            System.out.println("🔍 Trip found: " + trip.getTripName());

            // Find the student in the trip
            List<TripStudent> tripStudents = tripStudentRepository.findByTrip(trip);
            System.out.println("🔍 Found " + tripStudents.size() + " students in trip");
            
            TripStudent tripStudent = tripStudents.stream()
                    .filter(ts -> ts.getStudent().getStudentId().equals(request.getStudentId()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found in trip"));
            System.out.println("🔍 Student found: " + tripStudent.getStudent().getFirstName() + " " + tripStudent.getStudent().getLastName());

            // Create dispatch log entry
            System.out.println("🔍 Creating DispatchLog entry...");
            DispatchLog dispatchLog = DispatchLog.builder()
                    .trip(trip)
                    .student(tripStudent.getStudent())
                    .school(trip.getSchool())
                    .vehicle(trip.getVehicle())
                    .driver(driver) // CRITICAL FIX: Set the driver field
                    .eventType(com.app.Enum.EventType.valueOf(request.getEventType()))
                    .remarks(request.getRemarks())
                    .createdBy(driver.getDriverName())
                    .createdDate(request.getEventTime() != null ? request.getEventTime() : LocalDateTime.now())
                    .build();

            System.out.println("🔍 Saving DispatchLog to database...");
            DispatchLog savedLog = dispatchLogRepository.save(dispatchLog);
            System.out.println("🔍 DispatchLog saved with ID: " + savedLog.getDispatchLogId());

            // Send real-time notifications based on event type
            _sendRealTimeNotification(trip, tripStudent.getStudent(), request.getEventType(), driver);

            System.out.println("🔍 Attendance marked successfully for student: " + tripStudent.getStudent().getFirstName());
            return new ApiResponse(true, "Student attendance marked successfully", null);
        } catch (Exception e) {
            System.out.println("❌ Error in markStudentAttendance: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error marking student attendance: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponse sendParentNotification(Integer driverId, NotificationRequestDto request) {
        try {
            System.out.println("🔔 sendParentNotification called for driverId: " + driverId + ", tripId: " + request.getTripId());
            
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            // Get the trip
            Trip trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));

            // Get all students in this trip
            List<TripStudent> tripStudents = tripStudentRepository.findByTrip(trip);
            System.out.println("🔔 Found " + tripStudents.size() + " students in trip: " + trip.getTripName());

            if (tripStudents.isEmpty()) {
                return new ApiResponse(false, "No students found in this trip", null);
            }

            // Send notifications to all students' parents
            int notificationCount = 0;
            for (TripStudent tripStudent : tripStudents) {
                try {
                    // Create dispatch log entry for the notification
                    System.out.println("🔔 Creating DispatchLog for student: " + tripStudent.getStudent().getStudentId());
                    System.out.println("🔔 EventType: " + request.getNotificationType());
                    
                    DispatchLog notificationLog = DispatchLog.builder()
                            .trip(trip)
                            .student(tripStudent.getStudent())
                            .school(trip.getSchool())
                            .vehicle(trip.getVehicle())
                            .driver(driver) // CRITICAL FIX: Set the driver field
                            .eventType(com.app.Enum.EventType.valueOf(request.getNotificationType()))
                            .remarks("Notification sent: " + request.getMessage())
                            .createdBy(driver.getDriverName())
                            .createdDate(LocalDateTime.now())
                            .build();

                    System.out.println("🔔 Saving DispatchLog to database...");
                    DispatchLog savedLog = dispatchLogRepository.save(notificationLog);
                    System.out.println("🔔 DispatchLog saved with ID: " + savedLog.getDispatchLogId());
                    notificationCount++;

                    // Send real-time notification
                    _sendRealTimeNotification(trip, tripStudent.getStudent(), request.getNotificationType(), driver);

                    System.out.println("🔔 Notification sent to parent of student: " + 
                        tripStudent.getStudent().getFirstName() + " " + tripStudent.getStudent().getLastName());

                } catch (Exception e) {
                    System.out.println("❌ Error sending notification to student " + 
                        tripStudent.getStudent().getStudentId() + ": " + e.getMessage());
                }
            }

            String message = String.format("Notification sent to %d parents successfully", notificationCount);
            System.out.println("🔔 " + message);
            
            return new ApiResponse(true, message, null);
        } catch (Exception e) {
            System.out.println("❌ Error in sendParentNotification: " + e.getMessage());
            e.printStackTrace();
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

            // GET ALL STUDENTS IN TRIP
            List<TripStudent> tripStudents = tripStudentRepository.findByTrip(trip);
            
            // CREATE DISPATCH LOG AND NOTIFICATION FOR EACH STUDENT
            for (TripStudent ts : tripStudents) {
                // Create DispatchLog entry
                DispatchLog dispatchLog = DispatchLog.builder()
                    .trip(trip)
                    .student(ts.getStudent())
                    .school(trip.getSchool())
                    .vehicle(trip.getVehicle())
                    .driver(driver)
                    .eventType(EventType.TRIP_STARTED)
                    .remarks("Trip started by driver: " + driver.getDriverName())
                    .createdDate(LocalDateTime.now())
                    .createdBy(driver.getDriverName())
                    .build();
                DispatchLog savedDispatchLog = dispatchLogRepository.save(dispatchLog);
                
                // Create Notification record
                Notification notification = Notification.builder()
                    .dispatchLog(savedDispatchLog)
                    .notificationType(NotificationType.TRIP_UPDATE)
                    .isSent(true)
                    .sentAt(LocalDateTime.now())
                    .createdBy(driver.getDriverName())
                    .createdDate(LocalDateTime.now())
                    .build();
                notificationRepository.save(notification);
                
                // SEND WEBSOCKET NOTIFICATION TO PARENTS
                WebSocketNotificationDto notificationDto = WebSocketNotificationDto.builder()
                    .type("TRIP_STARTED")
                    .title("Trip Started")
                    .message("Trip has started for " + trip.getTripName())
                    .priority("HIGH")
                    .tripId(trip.getTripId())
                    .schoolId(trip.getSchool().getSchoolId())
                    .studentId(ts.getStudent().getStudentId())
                    .build();
                _sendNotificationToStudentParents(ts.getStudent(), notificationDto);
            }
            
            // ALSO SEND TO SCHOOL ADMIN
            WebSocketNotificationDto schoolDto = WebSocketNotificationDto.builder()
                .type("TRIP_STARTED")
                .title("Trip Started")
                .message("Driver " + driver.getDriverName() + " started trip: " + trip.getTripName())
                .priority("MEDIUM")
                .tripId(trip.getTripId())
                .schoolId(trip.getSchool().getSchoolId())
                .build();
            webSocketNotificationService.sendNotificationToSchool(trip.getSchool().getSchoolId(), schoolDto);

            return new ApiResponse(true, "Trip started successfully", null);
        } catch (Exception e) {
            return new ApiResponse(false, "Error starting trip: " + e.getMessage(), null);
        }
    }

    // ---------------- Mapper ----------------
    private DispatchLogResponseDto mapToDispatchLogDto(DispatchLog log) {
        if (log == null) {
            return null;
        }
        
        // Extract simple values to avoid any Hibernate proxy issues
        Integer dispatchLogId = log.getDispatchLogId();
        String remarks = log.getRemarks();
        String createdBy = log.getCreatedBy();
        LocalDateTime createdDate = log.getCreatedDate();
        String updatedBy = log.getUpdatedBy();
        LocalDateTime updatedDate = log.getUpdatedDate();
        String eventTypeStr = log.getEventType() != null ? log.getEventType().name() : null;
        
        // Extract IDs and names with null checks to avoid lazy loading issues
        Integer tripId = null;
        String tripName = null;
        try {
            if (log.getTrip() != null) {
                tripId = log.getTrip().getTripId();
                tripName = log.getTrip().getTripName();
            }
        } catch (Exception e) {
            // Ignore lazy loading exceptions
            System.out.println("Warning: Could not access trip data: " + e.getMessage());
        }
        
        Integer studentId = null;
        String studentName = null;
        try {
            if (log.getStudent() != null) {
                studentId = log.getStudent().getStudentId();
                studentName = log.getStudent().getFirstName() + " " + log.getStudent().getLastName();
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not access student data: " + e.getMessage());
        }
        
        Integer schoolId = null;
        String schoolName = null;
        try {
            if (log.getSchool() != null) {
                schoolId = log.getSchool().getSchoolId();
                schoolName = log.getSchool().getSchoolName();
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not access school data: " + e.getMessage());
        }
        
        Integer vehicleId = null;
        String vehicleNumber = null;
        try {
            if (log.getVehicle() != null) {
                vehicleId = log.getVehicle().getVehicleId();
                vehicleNumber = log.getVehicle().getVehicleNumber();
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not access vehicle data: " + e.getMessage());
        }
        
        return DispatchLogResponseDto.builder()
                .dispatchLogId(dispatchLogId)
                .tripId(tripId)
                .tripName(tripName)
                .studentId(studentId)
                .studentName(studentName)
                .schoolId(schoolId)
                .schoolName(schoolName)
                .vehicleId(vehicleId)
                .vehicleNumber(vehicleNumber)
                .eventType(eventTypeStr)
                .remarks(remarks)
                .createdBy(createdBy)
                .createdDate(createdDate)
                .updatedBy(updatedBy)
                .updatedDate(updatedDate)
                .build();
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
        // Calculate student statistics
        int totalStudents = tripStudentRepository.countByTrip(trip);
        
        // Get dispatch logs for this trip to calculate pickups and drops
        List<DispatchLog> tripLogs = dispatchLogRepository.findByTripAndVehicle(trip, trip.getVehicle());
        int studentsPickedUp = (int) tripLogs.stream()
            .filter(log -> log.getEventType() == com.app.Enum.EventType.PICKUP_FROM_PARENT ||
                          log.getEventType() == com.app.Enum.EventType.PICKUP_FROM_SCHOOL)
            .count();
        int studentsDropped = (int) tripLogs.stream()
            .filter(log -> log.getEventType() == com.app.Enum.EventType.DROP_TO_SCHOOL ||
                          log.getEventType() == com.app.Enum.EventType.DROP_TO_PARENT)
            .count();
        
        // Get trip students for the students list
        List<TripStudent> tripStudents = tripStudentRepository.findByTrip(trip);
        List<TripResponseDto.TripStudentDto> studentDtos = tripStudents.stream()
            .map(this::mapToTripStudentDto)
            .collect(Collectors.toList());
        
        return TripResponseDto.builder()
                .tripId(trip.getTripId())
                .tripName(trip.getTripName())
                .tripNumber(trip.getTripNumber())
                .tripType(trip.getTripType())
                .tripTypeDisplay(trip.getTripType() != null ? trip.getTripType().toString() : "Unknown")
                .scheduledTime(trip.getScheduledTime())
                .estimatedDurationMinutes(trip.getEstimatedDurationMinutes())
                .routeName(trip.getRouteName())
                .routeDescription(trip.getRouteDescription())
                .isActive(trip.getIsActive())
                .vehicleId(trip.getVehicle().getVehicleId())
                .vehicleNumber(trip.getVehicle().getVehicleNumber())
                .vehicleType(trip.getVehicle().getVehicleType() != null ? trip.getVehicle().getVehicleType().toString() : "Unknown")
                .vehicleCapacity(trip.getVehicle().getCapacity())
                .schoolId(trip.getSchool().getSchoolId())
                .schoolName(trip.getSchool().getSchoolName())
                .driverId(trip.getDriver() != null ? trip.getDriver().getDriverId() : null)
                .driverName(trip.getDriver() != null ? trip.getDriver().getDriverName() : "Unknown")
                .driverContactNumber(trip.getDriver() != null ? trip.getDriver().getDriverContactNumber() : null)
                .tripStatus(trip.getTripStatus() != null ? trip.getTripStatus() : "NOT_STARTED")
                .tripStartTime(trip.getTripStartTime())
                .tripEndTime(trip.getTripEndTime())
                .totalStudents(totalStudents)
                .studentsPickedUp(studentsPickedUp)
                .studentsDropped(studentsDropped)
                .studentsAbsent(Math.max(0, totalStudents - studentsPickedUp - studentsDropped))
                .students(studentDtos)
                .createdBy(trip.getCreatedBy())
                .createdDate(trip.getCreatedDate())
                .updatedBy(trip.getUpdatedBy())
                .updatedDate(trip.getUpdatedDate())
                .build();
    }
    
    private TripResponseDto.TripStudentDto mapToTripStudentDto(TripStudent tripStudent) {
        // Get the trip to determine pickup and drop locations based on trip type
        Trip trip = tripStudent.getTrip();
        String pickupLocation;
        String dropLocation;
        
        if (trip.getTripType() != null) {
            switch (trip.getTripType()) {
                case MORNING_PICKUP:
                    // Morning: Pickup from Home, Drop to School
                    pickupLocation = "Home";
                    dropLocation = "School";
                    break;
                case AFTERNOON_DROP:
                    // Afternoon: Pickup from School, Drop to Home
                    pickupLocation = "School";
                    dropLocation = "Home";
                    break;
                case SPECIAL_TRIP:
                case FIELD_TRIP:
                    // Special trips: Both locations are School
                    pickupLocation = "School";
                    dropLocation = "School";
                    break;
                default:
                    pickupLocation = "Unknown";
                    dropLocation = "Unknown";
            }
        } else {
            // Fallback if trip type is null
            pickupLocation = "Unknown";
            dropLocation = "Unknown";
        }
        
        // Calculate attendance status from dispatch logs
        String attendanceStatus = calculateAttendanceStatus(trip, tripStudent.getStudent());
        
        return TripResponseDto.TripStudentDto.builder()
                .studentId(tripStudent.getStudent().getStudentId())
                .studentName(tripStudent.getStudent().getFirstName() + " " + tripStudent.getStudent().getLastName())
                .studentPhoto(tripStudent.getStudent().getStudentPhoto())
                .className(tripStudent.getStudent().getClassMaster() != null ? tripStudent.getStudent().getClassMaster().getClassName() : "Unknown")
                .sectionName(tripStudent.getStudent().getSectionMaster() != null ? tripStudent.getStudent().getSectionMaster().getSectionName() : "Unknown")
                .pickupLocation(pickupLocation)
                .dropLocation(dropLocation)
                .pickupOrder(tripStudent.getPickupOrder())
                .dropOrder(tripStudent.getPickupOrder()) // Use pickup order as drop order since dropOrder doesn't exist
                .attendanceStatus(attendanceStatus) // Calculate from dispatch logs
                .pickupTime(null)
                .dropTime(null)
                .remarks(null)
                .parentName(tripStudent.getStudent().getFatherName() != null ? tripStudent.getStudent().getFatherName() : "Unknown")
                .parentContactNumber(tripStudent.getStudent().getPrimaryContactNumber())
                .parentEmail(tripStudent.getStudent().getEmail())
                .build();
    }
    
    /**
     * Send real-time notifications based on event type
     */
    private void _sendRealTimeNotification(Trip trip, com.app.entity.Student student, String eventType, Driver driver) {
        try {
            String notificationMessage = "";
            String notificationTitle = "";
            
            switch (eventType) {
                case "PICKUP_FROM_PARENT":
                    notificationTitle = "Student Picked Up";
                    notificationMessage = String.format("%s has been picked up from home by %s (Vehicle: %s). Trip: %s", 
                        student.getFirstName() + " " + student.getLastName(),
                        driver.getDriverName(),
                        trip.getVehicle().getVehicleNumber(),
                        trip.getTripName());
                    break;
                    
                case "DROP_TO_SCHOOL":
                    notificationTitle = "Student Arrived at School";
                    notificationMessage = String.format("%s has arrived at school safely. Vehicle: %s", 
                        student.getFirstName() + " " + student.getLastName(),
                        trip.getVehicle().getVehicleNumber());
                    break;
                    
                case "PICKUP_FROM_SCHOOL":
                    notificationTitle = "Student Picked Up from School";
                    notificationMessage = String.format("%s has been picked up from school by %s (Vehicle: %s). Trip: %s", 
                        student.getFirstName() + " " + student.getLastName(),
                        driver.getDriverName(),
                        trip.getVehicle().getVehicleNumber(),
                        trip.getTripName());
                    break;
                    
                case "DROP_TO_PARENT":
                    notificationTitle = "Student Dropped Home";
                    notificationMessage = String.format("%s has been dropped home safely by %s (Vehicle: %s)", 
                        student.getFirstName() + " " + student.getLastName(),
                        driver.getDriverName(),
                        trip.getVehicle().getVehicleNumber());
                    break;
                    
                case "ARRIVAL_NOTIFICATION":
                    notificationTitle = "Bus Arrival Notification";
                    notificationMessage = String.format("🚌 Your child's school bus will arrive in approximately 5 minutes. Please be ready for pickup. Vehicle: %s", 
                        trip.getVehicle().getVehicleNumber());
                    break;
                    
                case "PICKUP_CONFIRMATION":
                    notificationTitle = "Pickup Confirmed";
                    notificationMessage = String.format("%s has been successfully picked up. Vehicle: %s", 
                        student.getFirstName() + " " + student.getLastName(),
                        trip.getVehicle().getVehicleNumber());
                    break;
                    
                case "DROP_CONFIRMATION":
                    notificationTitle = "Drop Confirmed";
                    notificationMessage = String.format("%s has been successfully dropped. Vehicle: %s", 
                        student.getFirstName() + " " + student.getLastName(),
                        trip.getVehicle().getVehicleNumber());
                    break;
                    
                case "DELAY_NOTIFICATION":
                    notificationTitle = "Trip Delay Notification";
                    notificationMessage = String.format("⚠️ Your child's school bus is running late. Expected delay: 10-15 minutes. Vehicle: %s", 
                        trip.getVehicle().getVehicleNumber());
                    break;
                    
                default:
                    notificationTitle = "Trip Update";
                    notificationMessage = String.format("Trip update for %s: %s", 
                        student.getFirstName() + " " + student.getLastName(),
                        eventType);
            }
            
            // Create the notification DTO
            com.app.payload.response.WebSocketNotificationDto notificationDto = 
                com.app.payload.response.WebSocketNotificationDto.builder()
                    .type(eventType)
                    .title(notificationTitle)
                    .message(notificationMessage)
                    .priority("HIGH")
                    .tripId(trip.getTripId())
                    .studentId(student.getStudentId())
                    .action("UPDATE")
                    .entityType("STUDENT")
                    .schoolId(trip.getSchool().getSchoolId())
                    .build();
            
            // Send WebSocket notification based on event type
            try {
                switch (eventType) {
                    case "PICKUP_FROM_PARENT":
                    case "DROP_TO_PARENT":
                    case "ARRIVAL_NOTIFICATION":
                    case "PICKUP_CONFIRMATION":
                    case "DROP_CONFIRMATION":
                    case "DELAY_NOTIFICATION":
                        // Send to specific parents of the student
                        _sendNotificationToStudentParents(student, notificationDto);
                        break;
                        
                    case "DROP_TO_SCHOOL":
                    case "PICKUP_FROM_SCHOOL":
                        // Send to school staff
                        webSocketNotificationService.sendNotificationToSchool(
                            trip.getSchool().getSchoolId(),
                            notificationDto
                        );
                        break;
                        
                    default:
                        // Send to school as fallback
                        webSocketNotificationService.sendNotificationToSchool(
                            trip.getSchool().getSchoolId(),
                            notificationDto
                        );
                }
                
                System.out.println("✅ WebSocket notification sent: " + notificationTitle + " - " + notificationMessage);
                
            } catch (Exception wsException) {
                System.out.println("❌ Error sending WebSocket notification: " + wsException.getMessage());
                // Fallback to console logging
                System.out.println("🔔 Real-time notification: " + notificationTitle + " - " + notificationMessage);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error sending real-time notification: " + e.getMessage());
        }
    }
    
    /**
     * Send notification to specific parents of a student
     */
    private void _sendNotificationToStudentParents(com.app.entity.Student student, com.app.payload.response.WebSocketNotificationDto notificationDto) {
        try {
            // Find all parent relationships for this student
            List<com.app.entity.StudentParent> studentParents = studentParentRepository.findByStudent(student);
            
            if (studentParents.isEmpty()) {
                System.out.println("⚠️ No parents found for student: " + student.getFirstName() + " " + student.getLastName());
                // Fallback to school-wide notification
                webSocketNotificationService.sendNotificationToSchool(
                    student.getSchool().getSchoolId(),
                    notificationDto
                );
                return;
            }
            
            // Send notification to each parent
            for (com.app.entity.StudentParent studentParent : studentParents) {
                if (studentParent.getParentUser() != null) {
                    // Send to specific parent user
                    webSocketNotificationService.sendNotificationToUser(
                        studentParent.getParentUser().getUId().toString(),
                        notificationDto
                    );
                    System.out.println("📱 Notification sent to parent: " + studentParent.getParentUser().getUserName());
                }
            }
            
            // Also send to school topic for school staff visibility
            webSocketNotificationService.sendNotificationToSchool(
                student.getSchool().getSchoolId(),
                notificationDto
            );
            
        } catch (Exception e) {
            System.out.println("❌ Error sending notification to student parents: " + e.getMessage());
            // Fallback to school-wide notification
            try {
                webSocketNotificationService.sendNotificationToSchool(
                    student.getSchool().getSchoolId(),
                    notificationDto
                );
            } catch (Exception fallbackException) {
                System.out.println("❌ Fallback notification also failed: " + fallbackException.getMessage());
            }
        }
    }
    
    /**
     * Calculate attendance status based on dispatch logs
     */
    private String calculateAttendanceStatus(Trip trip, com.app.entity.Student student) {
        try {
            // Get all dispatch logs for this student in this trip
            List<DispatchLog> dispatchLogs = dispatchLogRepository.findByTripAndStudent(trip, student);
            
            if (dispatchLogs.isEmpty()) {
                return "PENDING";
            }
            
            // Check for different event types to determine status
            boolean hasPickupFromParent = dispatchLogs.stream()
                .anyMatch(log -> log.getEventType() == com.app.Enum.EventType.PICKUP_FROM_PARENT);
            
            boolean hasDropToSchool = dispatchLogs.stream()
                .anyMatch(log -> log.getEventType() == com.app.Enum.EventType.DROP_TO_SCHOOL);
            
            boolean hasPickupFromSchool = dispatchLogs.stream()
                .anyMatch(log -> log.getEventType() == com.app.Enum.EventType.PICKUP_FROM_SCHOOL);
            
            boolean hasDropToParent = dispatchLogs.stream()
                .anyMatch(log -> log.getEventType() == com.app.Enum.EventType.DROP_TO_PARENT);
            
            // Determine status based on trip type and events
            if (trip.getTripType() != null) {
                switch (trip.getTripType()) {
                    case MORNING_PICKUP:
                        if (hasDropToSchool) {
                            return "DROPPED"; // Completed morning trip
                        } else if (hasPickupFromParent) {
                            return "PICKED_UP"; // Picked up but not dropped yet
                        } else {
                            return "PENDING";
                        }
                    case AFTERNOON_DROP:
                        if (hasDropToParent) {
                            return "DROPPED"; // Completed afternoon trip
                        } else if (hasPickupFromSchool) {
                            return "PICKED_UP"; // Picked up from school but not dropped yet
                        } else {
                            return "PENDING";
                        }
                    default:
                        // For other trip types, check if any drop event occurred
                        if (hasDropToSchool || hasDropToParent) {
                            return "DROPPED";
                        } else if (hasPickupFromParent || hasPickupFromSchool) {
                            return "PICKED_UP";
                        } else {
                            return "PENDING";
                        }
                }
            } else {
                // Fallback logic if trip type is null
                if (hasDropToSchool || hasDropToParent) {
                    return "DROPPED";
                } else if (hasPickupFromParent || hasPickupFromSchool) {
                    return "PICKED_UP";
                } else {
                    return "PENDING";
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error calculating attendance status: " + e.getMessage());
            return "PENDING"; // Default fallback
        }
    }

    // ================ ENHANCED DRIVER DASHBOARD METHODS ================

    @Override
    public ApiResponse getTimeBasedTrips(Integer driverId) {
        try {
            System.out.println("🔍 getTimeBasedTrips called for driverId: " + driverId);
            
            // Get driver
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            
            // Get all assigned trips
            List<Trip> allTrips = tripRepository.findByDriverAndIsActive(driver, true);
            System.out.println("🔍 Found " + allTrips.size() + " total trips for driver");
            
            // Get current time
            LocalDateTime now = LocalDateTime.now();
            int currentHour = now.getHour();
            String currentTime = now.toString();
            
            // Determine time slot and filter trips
            String timeSlot;
            List<Trip> filteredTrips = new ArrayList<>();
            boolean isWorkingHours = false;
            String workingHoursMessage = "";
            
            if (currentHour >= 6 && currentHour < 12) {
                // Morning: 6 AM - 12 PM
                timeSlot = "MORNING";
                filteredTrips = allTrips.stream()
                    .filter(trip -> trip.getTripType() != null && 
                            trip.getTripType().toString().equals("MORNING_PICKUP"))
                    .collect(Collectors.toList());
                isWorkingHours = true;
                workingHoursMessage = "Morning pickup time (6 AM - 12 PM)";
            } else if (currentHour >= 12 && currentHour < 18) {
                // Afternoon: 12 PM - 6 PM
                timeSlot = "AFTERNOON";
                filteredTrips = allTrips.stream()
                    .filter(trip -> trip.getTripType() != null && 
                            trip.getTripType().toString().equals("AFTERNOON_DROP"))
                    .collect(Collectors.toList());
                isWorkingHours = true;
                workingHoursMessage = "Afternoon drop time (12 PM - 6 PM)";
            } else if (currentHour >= 18 && currentHour < 22) {
                // Evening: 6 PM - 10 PM
                timeSlot = "EVENING";
                isWorkingHours = false;
                workingHoursMessage = "Evening time - No scheduled trips";
            } else {
                // Night: 10 PM - 6 AM
                timeSlot = "NIGHT";
                isWorkingHours = false;
                workingHoursMessage = "Night time - No scheduled trips";
            }
            
            // Convert to DTOs
            List<TripResponseDto> availableTripDtos = filteredTrips.stream()
                .map(this::mapToTripResponseDto)
                .collect(Collectors.toList());
                
            List<TripResponseDto> allTripDtos = allTrips.stream()
                .map(this::mapToTripResponseDto)
                .collect(Collectors.toList());
            
            // Find next trip time
            String nextTripTime = "No upcoming trips";
            if (!filteredTrips.isEmpty()) {
                Trip nextTrip = filteredTrips.stream()
                    .filter(trip -> trip.getScheduledTime() != null)
                    .min((t1, t2) -> t1.getScheduledTime().compareTo(t2.getScheduledTime()))
                    .orElse(null);
                if (nextTrip != null) {
                    nextTripTime = nextTrip.getScheduledTime().toString();
                }
            }
            
            TimeBasedTripsResponseDto response = TimeBasedTripsResponseDto.builder()
                .currentTime(currentTime)
                .timeSlot(timeSlot)
                .message("Found " + filteredTrips.size() + " trips for " + timeSlot.toLowerCase() + " time slot")
                .availableTrips(availableTripDtos)
                .allTrips(allTripDtos)
                .isWorkingHours(isWorkingHours)
                .nextTripTime(nextTripTime)
                .workingHoursMessage(workingHoursMessage)
                .build();
            
            System.out.println("🔍 Returning " + filteredTrips.size() + " filtered trips for " + timeSlot + " time slot");
            return ApiResponse.builder()
                .success(true)
                .message("Time-based trips retrieved successfully")
                .data(response)
                .build();
                
        } catch (Exception e) {
            System.out.println("❌ Error in getTimeBasedTrips: " + e.getMessage());
            return ApiResponse.builder()
                .success(false)
                .message("Failed to retrieve time-based trips: " + e.getMessage())
                .build();
        }
    }

    @Override
    public ApiResponse getDriverProfile(Integer driverId) {
        try {
            System.out.println("🔍 getDriverProfile called for driverId: " + driverId);
            
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            
            // ✅ CHECK 1: Driver must be activated (have a user account)
            if (driver.getUser() == null) {
                System.out.println("⚠️ Driver not activated - no user account found");
                return ApiResponse.builder()
                    .success(true)
                    .message("Driver account not activated yet")
                    .data(null)
                    .build();
            }
            
            // ✅ CHECK 2: Driver must have active vehicle assignment
            List<VehicleDriver> vehicleAssignments = vehicleDriverRepository.findByDriverAndIsActiveTrue(driver);
            if (vehicleAssignments.isEmpty()) {
                System.out.println("⚠️ No active vehicle assignment found for driver");
                return ApiResponse.builder()
                    .success(true)
                    .message("No vehicle assigned to driver")
                    .data(null)
                    .build();
            }
            
            // Get school and vehicle information
            String schoolName = "N/A";
            String vehicleNumber = "N/A";
            String vehicleType = "N/A";
            
            VehicleDriver assignment = vehicleAssignments.get(0);
            if (assignment.getSchool() != null) {
                schoolName = assignment.getSchool().getSchoolName();
            }
            if (assignment.getVehicle() != null) {
                vehicleNumber = assignment.getVehicle().getVehicleNumber();
                vehicleType = assignment.getVehicle().getVehicleType().toString();
            }
            
            DriverProfileResponseDto profile = DriverProfileResponseDto.builder()
                .driverId(driver.getDriverId())
                .driverName(driver.getDriverName())
                .email(driver.getEmail())
                .driverContactNumber(driver.getDriverContactNumber())
                .driverAddress(driver.getDriverAddress())
                .driverPhoto(driver.getDriverPhoto())
                .schoolName(schoolName)
                .vehicleNumber(vehicleNumber)
                .vehicleType(vehicleType)
                .isActive(driver.getIsActive())
                .createdDate(driver.getCreatedDate())
                .updatedDate(driver.getUpdatedDate())
                .build();
            
            System.out.println("✅ Driver profile retrieved successfully for: " + driver.getDriverName());
            return ApiResponse.builder()
                .success(true)
                .message("Driver profile retrieved successfully")
                .data(profile)
                .build();
                
        } catch (Exception e) {
            System.out.println("❌ Error in getDriverProfile: " + e.getMessage());
            return ApiResponse.builder()
                .success(false)
                .message("Failed to retrieve driver profile: " + e.getMessage())
                .build();
        }
    }

    @Override
    public ApiResponse updateDriverProfile(Integer driverId, DriverRequestDto requestDto) {
        try {
            System.out.println("🔍 updateDriverProfile called for driverId: " + driverId);
            
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            
            // Update driver information
            if (requestDto.getDriverName() != null) {
                driver.setDriverName(requestDto.getDriverName());
            }
            if (requestDto.getEmail() != null) {
                driver.setEmail(requestDto.getEmail());
            }
            if (requestDto.getDriverContactNumber() != null) {
                driver.setDriverContactNumber(requestDto.getDriverContactNumber());
            }
            if (requestDto.getDriverAddress() != null) {
                driver.setDriverAddress(requestDto.getDriverAddress());
            }
            if (requestDto.getDriverPhoto() != null) {
                driver.setDriverPhoto(requestDto.getDriverPhoto());
            }
            
            driver.setUpdatedDate(LocalDateTime.now());
            driverRepository.save(driver);
            
            System.out.println("🔍 Driver profile updated successfully for: " + driver.getDriverName());
            return ApiResponse.builder()
                .success(true)
                .message("Driver profile updated successfully")
                .data(driver)
                .build();
                
        } catch (Exception e) {
            System.out.println("❌ Error in updateDriverProfile: " + e.getMessage());
            return ApiResponse.builder()
                .success(false)
                .message("Failed to update driver profile: " + e.getMessage())
                .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse getDriverReports(Integer driverId) {
        try {
            System.out.println("🔍 getDriverReports called for driverId: " + driverId);
            
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            
            // ✅ CHECK 1: Driver must be activated (have a user account)
            if (driver.getUser() == null) {
                System.out.println("⚠️ Driver not activated - cannot generate reports");
                return ApiResponse.builder()
                    .success(true)
                    .message("Driver account not activated yet")
                    .data(null)
                    .build();
            }
            
            // ✅ CHECK 2: Driver must have active vehicle assignment
            List<VehicleDriver> vehicleAssignments = vehicleDriverRepository.findByDriverAndIsActiveTrue(driver);
            if (vehicleAssignments.isEmpty()) {
                System.out.println("⚠️ No active vehicle assignment - cannot generate reports");
                return ApiResponse.builder()
                    .success(true)
                    .message("No vehicle assigned to driver")
                    .data(null)
                    .build();
            }
            
            // Get all trips for this driver
            List<Trip> allTrips = tripRepository.findByDriverAndIsActive(driver, true);
            
            // Calculate statistics
            LocalDate today = LocalDate.now();
            LocalDate weekStart = today.minusDays(7);
            LocalDate monthStart = today.withDayOfMonth(1);
            
            // Today's statistics
            List<Trip> todayTrips = allTrips.stream()
                .filter(trip -> trip.getCreatedDate().toLocalDate().equals(today))
                .collect(Collectors.toList());
            
            // This week's statistics
            List<Trip> weekTrips = allTrips.stream()
                .filter(trip -> trip.getCreatedDate().toLocalDate().isAfter(weekStart.minusDays(1)))
                .collect(Collectors.toList());
            
            // This month's statistics
            List<Trip> monthTrips = allTrips.stream()
                .filter(trip -> trip.getCreatedDate().toLocalDate().isAfter(monthStart.minusDays(1)))
                .collect(Collectors.toList());
            
            // Calculate student counts (optimized - batch query)
            List<Integer> todayTripIds = todayTrips.stream()
                .map(Trip::getTripId)
                .collect(Collectors.toList());
            int todayStudents = todayTripIds.isEmpty() ? 0 : 
                tripStudentRepository.countByTripTripIdIn(todayTripIds);
                
            List<Integer> weekTripIds = weekTrips.stream()
                .map(Trip::getTripId)
                .collect(Collectors.toList());
            int weekStudents = weekTripIds.isEmpty() ? 0 : 
                tripStudentRepository.countByTripTripIdIn(weekTripIds);
                
            List<Integer> monthTripIds = monthTrips.stream()
                .map(Trip::getTripId)
                .collect(Collectors.toList());
            int monthStudents = monthTripIds.isEmpty() ? 0 : 
                tripStudentRepository.countByTripTripIdIn(monthTripIds);
            
            // Build response
            DriverReportsResponseDto reports = DriverReportsResponseDto.builder()
                .totalTripsCompleted(allTrips.size())
                .totalStudentsTransported(monthStudents) // Using month as total for now
                .totalDistanceCovered(0) // TODO: Calculate actual distance
                .averageRating(4.5) // TODO: Implement rating system
                .todayTrips(todayTrips.size())
                .todayStudents(todayStudents)
                .todayPickups(0) // TODO: Calculate from dispatch logs
                .todayDrops(0) // TODO: Calculate from dispatch logs
                .weekTrips(weekTrips.size())
                .weekStudents(weekStudents)
                .weekPickups(0) // TODO: Calculate from dispatch logs
                .weekDrops(0) // TODO: Calculate from dispatch logs
                .monthTrips(monthTrips.size())
                .monthStudents(monthStudents)
                .monthPickups(0) // TODO: Calculate from dispatch logs
                .monthDrops(0) // TODO: Calculate from dispatch logs
                .attendanceRecords(new ArrayList<>()) // TODO: Implement attendance records
                .recentTrips(new ArrayList<>()) // TODO: Implement recent trips
                .build();
            
            System.out.println("🔍 Driver reports generated successfully for: " + driver.getDriverName());
            return ApiResponse.builder()
                .success(true)
                .message("Driver reports retrieved successfully")
                .data(reports)
                .build();
                
        } catch (Exception e) {
            System.out.println("❌ Error in getDriverReports: " + e.getMessage());
            return ApiResponse.builder()
                .success(false)
                .message("Failed to retrieve driver reports: " + e.getMessage())
                .build();
        }
    }

    @Override
    public ApiResponse send5MinuteAlert(Integer driverId, Integer tripId, Integer studentId) {
        try {
            System.out.println("🔍 send5MinuteAlert called - driverId: " + driverId + ", tripId: " + tripId + ", studentId: " + studentId);
            
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            
            Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));
            
            // Get ALL students in trip and find SPECIFIC student
            List<TripStudent> tripStudents = tripStudentRepository.findByTrip(trip);
            TripStudent tripStudent = tripStudents.stream()
                .filter(ts -> ts.getStudent().getStudentId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Student not found in this trip"));
            
            // Create DispatchLog for SPECIFIC student
            DispatchLog dispatchLog = DispatchLog.builder()
                .trip(trip)
                .student(tripStudent.getStudent())  // ⭐ SPECIFIC student
                .school(trip.getSchool())
                .vehicle(trip.getVehicle())
                .driver(driver)
                .eventType(EventType.ARRIVAL_NOTIFICATION)
                .remarks("5-minute alert sent")
                .createdDate(LocalDateTime.now())
                .createdBy(driver.getDriverName())
                .build();
            DispatchLog savedDispatchLog = dispatchLogRepository.save(dispatchLog);
            
            // CREATE NOTIFICATION
            Notification notification = Notification.builder()
                .dispatchLog(savedDispatchLog)
                .notificationType(NotificationType.ARRIVAL_NOTIFICATION)
                .isSent(true)
                .sentAt(LocalDateTime.now())
                .createdBy(driver.getDriverName())
                .createdDate(LocalDateTime.now())
                .build();
            notificationRepository.save(notification);
            
            // Send WebSocket to SPECIFIC parents
            WebSocketNotificationDto notificationDto = WebSocketNotificationDto.builder()
                .type("ARRIVAL_NOTIFICATION")
                .title("Bus Arrival Notification")
                .message("🚌 Your child's school bus will arrive in approximately 5 minutes. Please be ready for pickup.")
                .priority("HIGH")
                .tripId(trip.getTripId())
                .studentId(tripStudent.getStudent().getStudentId())
                .schoolId(trip.getSchool().getSchoolId())
                .build();
            _sendNotificationToStudentParents(tripStudent.getStudent(), notificationDto);
            
            System.out.println("🔍 5-minute alert sent successfully for student: " + tripStudent.getStudent().getFirstName());
            return ApiResponse.builder()
                .success(true)
                .message("5-minute alert sent successfully")
                .build();
            
        } catch (Exception e) {
            System.out.println("❌ Error in send5MinuteAlert: " + e.getMessage());
            return ApiResponse.builder()
                .success(false)
                .message("Failed to send 5-minute alert: " + e.getMessage())
                .build();
        }
    }

    // ================ CONTEXT-SENSITIVE STUDENT ACTIONS ================

    @Override
    public ApiResponse markPickupFromHome(Integer driverId, Integer tripId, Integer studentId) {
        return markStudentAction(driverId, tripId, studentId, EventType.PICKUP_FROM_PARENT, 
            "Student picked up from home successfully");
    }

    @Override
    public ApiResponse markDropToSchool(Integer driverId, Integer tripId, Integer studentId) {
        return markStudentAction(driverId, tripId, studentId, EventType.DROP_TO_SCHOOL, 
            "Student dropped at school successfully");
    }

    @Override
    public ApiResponse markPickupFromSchool(Integer driverId, Integer tripId, Integer studentId) {
        return markStudentAction(driverId, tripId, studentId, EventType.PICKUP_FROM_SCHOOL, 
            "Student picked up from school successfully");
    }

    @Override
    public ApiResponse markDropToHome(Integer driverId, Integer tripId, Integer studentId) {
        return markStudentAction(driverId, tripId, studentId, EventType.DROP_TO_PARENT, 
            "Student dropped at home successfully");
    }

    // Helper method for context-sensitive student actions
    private ApiResponse markStudentAction(Integer driverId, Integer tripId, Integer studentId, 
                                        EventType eventType, String successMessage) {
        try {
            System.out.println("🔍 markStudentAction called - driverId: " + driverId + 
                ", tripId: " + tripId + ", studentId: " + studentId + ", eventType: " + eventType);
            
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            
            Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));
            
            // Find the student in the trip
            List<TripStudent> tripStudents = tripStudentRepository.findByTrip(trip);
            TripStudent tripStudent = tripStudents.stream()
                .filter(ts -> ts.getStudent().getStudentId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Student not found in this trip"));
            
            // Create dispatch log entry
            DispatchLog dispatchLog = DispatchLog.builder()
                .trip(trip)
                .student(tripStudent.getStudent())
                .vehicle(trip.getVehicle())
                .school(trip.getSchool())
                .driver(driver) // CRITICAL FIX: Set the driver field
                .eventType(eventType)
                .remarks("Driver action: " + eventType.toString())
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
            
            DispatchLog savedLog = dispatchLogRepository.save(dispatchLog);
            System.out.println("🔍 DispatchLog saved with ID: " + savedLog.getDispatchLogId());
            
            // Create Notification record
            Notification notification = Notification.builder()
                .dispatchLog(savedLog)
                .notificationType(mapEventTypeToNotificationType(eventType))
                .isSent(true)
                .sentAt(LocalDateTime.now())
                .createdBy(driver.getDriverName())
                .createdDate(LocalDateTime.now())
                .build();
            notificationRepository.save(notification);
            System.out.println("🔍 Notification saved for event: " + eventType);
            
            // Send real-time notification
            String notificationMessage = buildNotificationMessage(eventType, tripStudent.getStudent().getFirstName());
            _sendRealTimeNotification(trip, tripStudent.getStudent(), eventType.toString(), driver);
            
                          System.out.println("🔍 Student action completed successfully: " + successMessage);
              // Return null for data to avoid circular reference issues
              // The success message is sufficient for the frontend
              return ApiResponse.builder()
                  .success(true)
                  .message(successMessage)
                  .data(null)
                  .build();
                
        } catch (Exception e) {
            System.out.println("❌ Error in markStudentAction: " + e.getMessage());
            return ApiResponse.builder()
                .success(false)
                .message("Failed to mark student action: " + e.getMessage())
                .build();
        }
    }

    // Helper method to build notification messages
    private String buildNotificationMessage(EventType eventType, String studentName) {
        switch (eventType) {
            case PICKUP_FROM_PARENT:
                return "Student Picked Up - " + studentName + " has been picked up from home safely.";
            case DROP_TO_SCHOOL:
                return "Student Arrived at School - " + studentName + " has arrived at school safely.";
            case PICKUP_FROM_SCHOOL:
                return "Student Picked Up from School - " + studentName + " has been picked up from school.";
            case DROP_TO_PARENT:
                return "Student Dropped at Home - " + studentName + " has been dropped at home safely.";
            default:
                return "Student Update - " + studentName + " status has been updated.";
        }
    }

    /**
     * Map EventType to NotificationType
     */
    private NotificationType mapEventTypeToNotificationType(EventType eventType) {
        switch (eventType) {
            case TRIP_STARTED:
                return NotificationType.TRIP_UPDATE;
            case PICKUP_FROM_PARENT:
            case PICKUP_FROM_SCHOOL:
                return NotificationType.PICKUP_CONFIRMATION;
            case DROP_TO_SCHOOL:
            case DROP_TO_PARENT:
                return NotificationType.DROP_CONFIRMATION;
            case ARRIVAL_NOTIFICATION:
                return NotificationType.ARRIVAL_NOTIFICATION;
            case DELAY_NOTIFICATION:
                return NotificationType.DELAY_NOTIFICATION;
            case GATE_ENTRY:
            case GATE_EXIT:
                return NotificationType.SYSTEM_ALERT;
            default:
                return NotificationType.PUSH;
        }
    }

    // ================ LOCATION TRACKING ================

    @Override
    public ApiResponse updateDriverLocation(Integer driverId, Map<String, Object> locationData) {
        try {
            System.out.println("🔍 updateDriverLocation called for driverId: " + driverId);
            System.out.println("🔍 Location data: " + locationData);
            
            Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));
            
            // Extract location data
            Double latitude = (Double) locationData.get("latitude");
            Double longitude = (Double) locationData.get("longitude");
            String timestamp = (String) locationData.get("timestamp");
            
            if (latitude == null || longitude == null) {
                return ApiResponse.builder()
                    .success(false)
                    .message("Invalid location data: latitude and longitude are required")
                    .build();
            }
            
            // Update driver's current location (you might want to create a separate Location entity)
            // For now, we'll just log the location update
            System.out.println("📍 Driver " + driver.getDriverName() + " location updated: " + 
                latitude + ", " + longitude + " at " + timestamp);
            
            // TODO: Save location to database (create Location entity if needed)
            // Location location = Location.builder()
            //     .driver(driver)
            //     .latitude(latitude)
            //     .longitude(longitude)
            //     .timestamp(LocalDateTime.parse(timestamp))
            //     .build();
            // locationRepository.save(location);
            
            // Send real-time location update to parents of students in active trips
            _sendLocationUpdateToParents(driver, latitude, longitude);
            
            return ApiResponse.builder()
                .success(true)
                .message("Location updated successfully")
                .data(Map.of(
                    "driverId", driverId,
                    "latitude", latitude,
                    "longitude", longitude,
                    "timestamp", timestamp
                ))
                .build();
                
        } catch (Exception e) {
            System.out.println("❌ Error in updateDriverLocation: " + e.getMessage());
            return ApiResponse.builder()
                .success(false)
                .message("Failed to update location: " + e.getMessage())
                .build();
        }
    }

    private void _sendLocationUpdateToParents(Driver driver, Double latitude, Double longitude) {
        try {
            // Find active trips for this driver
            List<Trip> activeTrips = tripRepository.findByDriverAndIsActive(driver, true);
            
            for (Trip trip : activeTrips) {
                // Get all students in this trip
                List<TripStudent> tripStudents = tripStudentRepository.findByTrip(trip);
                
                for (TripStudent tripStudent : tripStudents) {
                    // Send location update to parent
                    String notificationMessage = String.format(
                        "🚌 Vehicle location update: %s is at %.6f, %.6f. Trip: %s",
                        driver.getDriverName(),
                        latitude,
                        longitude,
                        trip.getTripName()
                    );
                    
                    // Send WebSocket notification to parent
                    webSocketNotificationService.sendNotificationToSchool(
                        trip.getSchool().getSchoolId(),
                        com.app.payload.response.WebSocketNotificationDto.builder()
                            .type("LOCATION_UPDATE")
                            .title("Vehicle Location Update")
                            .message(notificationMessage)
                            .data(Map.of(
                                "driverId", driver.getDriverId(),
                                "driverName", driver.getDriverName(),
                                "latitude", latitude,
                                "longitude", longitude,
                                "tripId", trip.getTripId(),
                                "tripName", trip.getTripName(),
                                "vehicleNumber", trip.getVehicle().getVehicleNumber()
                            ))
                            .build()
                    );
                }
            }
            
            System.out.println("📍 Location updates sent to parents for driver: " + driver.getDriverName());
            
        } catch (Exception e) {
            System.out.println("❌ Error sending location updates to parents: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse getDriverLocation(Integer driverId) {
        try {
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            // Get the latest location from dispatch logs
            List<DispatchLog> locationLogs = dispatchLogRepository.findByDriverOrderByCreatedDateDesc(driver);
            
            if (locationLogs.isEmpty()) {
                return new ApiResponse(false, "No location data available for driver", null);
            }

            // Get the most recent location log
            DispatchLog latestLocation = locationLogs.get(0);
            
            Map<String, Object> locationData = new java.util.HashMap<>();
            locationData.put("driverId", driverId);
            locationData.put("driverName", driver.getDriverName());
            locationData.put("latitude", latestLocation.getLatitude());
            locationData.put("longitude", latestLocation.getLongitude());
            locationData.put("lastUpdated", latestLocation.getCreatedDate());
            locationData.put("address", latestLocation.getAddress());
            
            // Calculate if location is recent (within last 5 minutes)
            LocalDateTime now = LocalDateTime.now();
            long minutesSinceUpdate = java.time.Duration.between(latestLocation.getCreatedDate(), now).toMinutes();
            locationData.put("isRecent", minutesSinceUpdate <= 5);
            locationData.put("minutesAgo", minutesSinceUpdate);

            return new ApiResponse(true, "Driver location retrieved successfully", locationData);
            
        } catch (Exception e) {
            System.out.println("❌ Error getting driver location: " + e.getMessage());
            return new ApiResponse(false, "Failed to get driver location: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponse saveLocationUpdate(Integer driverId, Integer tripId, Double latitude, Double longitude, String address) {
        try {
            System.out.println("📍 saveLocationUpdate called - driverId: " + driverId + ", tripId: " + tripId);
            
            // Validate driver
            Driver driver = driverRepository.findById(driverId)
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + driverId));

            // Validate trip and check if it's assigned to driver
            Trip trip = tripRepository.findByDriverAndTripId(driver, tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found or not assigned to driver"));

            // Verify trip is active (trip_status = "IN_PROGRESS")
            if (!"IN_PROGRESS".equals(trip.getTripStatus())) {
                return new ApiResponse(false, "Cannot update location: Trip is not in progress. Current status: " + trip.getTripStatus(), null);
            }

            // Create and save VehicleLocation record
            VehicleLocation vehicleLocation = VehicleLocation.builder()
                    .trip(trip)
                    .driver(driver)
                    .vehicle(trip.getVehicle())
                    .school(trip.getSchool())
                    .latitude(latitude)
                    .longitude(longitude)
                    .address(address != null ? address : "")
                    .speed(null) // Can be calculated or provided if available
                    .bearing(null) // Can be calculated or provided if available
                    .build();

            VehicleLocation savedLocation = vehicleLocationRepository.save(vehicleLocation);
            System.out.println("📍 VehicleLocation saved with ID: " + savedLocation.getLocationId());

            // Send WebSocket notification with location data to all parents, school admin, and vehicle owner
            WebSocketNotificationDto locationNotification = WebSocketNotificationDto.builder()
                    .type("LOCATION_UPDATE")
                    .title("Vehicle Location Update")
                    .message(String.format("📍 Vehicle %s is currently at %.6f, %.6f. Trip: %s", 
                            trip.getVehicle().getVehicleNumber(), latitude, longitude, trip.getTripName()))
                    .priority("MEDIUM")
                    .tripId(trip.getTripId())
                    .schoolId(trip.getSchool().getSchoolId())
                    .vehicleId(trip.getVehicle().getVehicleId())
                    .timestamp(LocalDateTime.now())
                    .data(Map.of(
                            "locationId", savedLocation.getLocationId(),
                            "driverId", driver.getDriverId(),
                            "driverName", driver.getDriverName(),
                            "latitude", latitude,
                            "longitude", longitude,
                            "address", address != null ? address : "",
                            "tripId", trip.getTripId(),
                            "tripName", trip.getTripName(),
                            "vehicleId", trip.getVehicle().getVehicleId(),
                            "vehicleNumber", trip.getVehicle().getVehicleNumber()
                    ))
                    .build();

            // Send notification to school (which includes school admin, parents, and vehicle owner)
            webSocketNotificationService.sendNotificationToSchool(trip.getSchool().getSchoolId(), locationNotification);
            System.out.println("📱 Location update notification sent to school ID: " + trip.getSchool().getSchoolId());

            return new ApiResponse(true, "Location update saved successfully", Map.of(
                    "locationId", savedLocation.getLocationId(),
                    "latitude", latitude,
                    "longitude", longitude,
                    "address", address != null ? address : "",
                    "timestamp", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            System.out.println("❌ Error saving location update: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Failed to save location update: " + e.getMessage(), null);
        }
    }
}

