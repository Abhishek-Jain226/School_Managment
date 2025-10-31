package com.app.service.impl;

import com.app.entity.School;
import com.app.entity.Trip;
import com.app.entity.Vehicle;
import com.app.entity.Driver;
import com.app.entity.VehicleDriver;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.TripRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.TripResponseDto;
import com.app.payload.response.TripStatusResponseDto;
import com.app.repository.SchoolRepository;
import com.app.repository.TripRepository;
import com.app.repository.VehicleRepository;
import com.app.repository.DriverRepository;
import com.app.repository.VehicleDriverRepository;
import com.app.repository.TripStatusRepository;
import com.app.entity.TripStatus;
import com.app.service.ITripService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TripServiceImpl implements ITripService {

	@Autowired
    private TripRepository tripRepository;
	@Autowired
    private SchoolRepository schoolRepository;
	@Autowired
    private VehicleRepository vehicleRepository;
	@Autowired
    private DriverRepository driverRepository;
	@Autowired
    private VehicleDriverRepository vehicleDriverRepository;
	@Autowired
    private TripStatusRepository tripStatusRepository;

    @Override
    public ApiResponse createTrip(TripRequestDto request) {
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        // Get driver assigned to this vehicle through VehicleDriver relationship
        Driver driver = null;
        VehicleDriver vehicleDriver = vehicleDriverRepository.findByVehicleAndIsActiveTrue(vehicle)
                .stream()
                .findFirst()
                .orElse(null);
        
        if (vehicleDriver != null) {
            driver = vehicleDriver.getDriver();
        }

        Trip trip = new Trip();
        trip.setSchool(school);
        trip.setVehicle(vehicle);
        trip.setDriver(driver); // Assign driver if available
        trip.setTripName(request.getTripName());
        trip.setTripNumber(request.getTripNumber());
        trip.setTripType(request.getTripType());
        trip.setRouteName(request.getRouteName());
        trip.setRouteDescription(request.getRouteDescription());
        // scheduledTime, tripStatus are now nullable as requested
        // tripStartTime and tripEndTime will be set when trip actually starts/ends
        
        trip.setIsActive(true);
        trip.setCreatedBy(request.getCreatedBy());
        trip.setCreatedDate(LocalDateTime.now());

        Trip savedTrip = tripRepository.save(trip);

        return new ApiResponse(true, "Trip created successfully", mapToResponse(savedTrip));
    }

    @Override
    public ApiResponse updateTrip(Integer tripId, TripRequestDto request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        if (request.getSchoolId() != null) {
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));
            trip.setSchool(school);
        }

        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));
            trip.setVehicle(vehicle);
        }

        trip.setTripName(request.getTripName());
        trip.setTripNumber(request.getTripNumber());
        trip.setTripType(request.getTripType());
        trip.setRouteName(request.getRouteName());
        trip.setRouteDescription(request.getRouteDescription());
        trip.setUpdatedBy(request.getUpdatedBy());
        trip.setUpdatedDate(LocalDateTime.now());

        Trip updatedTrip = tripRepository.save(trip);

        return new ApiResponse(true, "Trip updated successfully", mapToResponse(updatedTrip));
    }

    @Override
    public ApiResponse deleteTrip(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        tripRepository.delete(trip);

        return new ApiResponse(true, "Trip deleted successfully", null);
    }

    @Override
    public ApiResponse getTripById(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        return new ApiResponse(true, "Trip fetched successfully", mapToResponse(trip));
    }

    @Override
    public ApiResponse getAllTrips(Integer schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        List<TripResponseDto> trips = tripRepository.findBySchool(school).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Trips fetched successfully", trips);
    }

    private TripResponseDto mapToResponse(Trip trip) {
        TripResponseDto.TripResponseDtoBuilder builder = TripResponseDto.builder()
                .tripId(trip.getTripId())
                .schoolId(trip.getSchool().getSchoolId())
                .schoolName(trip.getSchool().getSchoolName())
                .vehicleId(trip.getVehicle().getVehicleId())
                .vehicleNumber(trip.getVehicle().getVehicleNumber())
                .vehicleType(trip.getVehicle().getVehicleType() != null ? trip.getVehicle().getVehicleType().toString() : null)
                .vehicleCapacity(trip.getVehicle().getCapacity())
                .tripName(trip.getTripName())
                .tripNumber(trip.getTripNumber())
                .tripType(trip.getTripType())
                .tripTypeDisplay(trip.getTripType() != null ? trip.getTripType().getDisplayName() : null)
                .scheduledTime(trip.getScheduledTime())
                .estimatedDurationMinutes(trip.getEstimatedDurationMinutes())
                .routeName(trip.getRouteName())
                .routeDescription(trip.getRouteDescription())
                .tripStatus(trip.getTripStatus())
                .tripStartTime(trip.getTripStartTime())
                .tripEndTime(trip.getTripEndTime())
                .isActive(trip.getIsActive())
                .createdBy(trip.getCreatedBy())
                .createdDate(trip.getCreatedDate())
                .updatedBy(trip.getUpdatedBy())
                .updatedDate(trip.getUpdatedDate());
        
        // Add driver information if driver is assigned
        if (trip.getDriver() != null) {
            builder.driverId(trip.getDriver().getDriverId())
                   .driverName(trip.getDriver().getDriverName())
                   .driverContactNumber(trip.getDriver().getDriverContactNumber());
        }
        
        return builder.build();
    }

    @Override
    public ApiResponse getTripsByDriver(Integer driverId) {
        try {
            List<Trip> trips = tripRepository.findByDriver_DriverId(driverId);
            List<TripResponseDto> tripDtos = trips.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return new ApiResponse(true, "Trips retrieved successfully", tripDtos);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving trips: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getTodayTripsByDriver(Integer driverId) {
        try {
            List<Trip> trips = tripRepository.findByDriver_DriverIdAndDate(driverId, java.time.LocalDate.now());
            List<TripResponseDto> tripDtos = trips.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());

            return new ApiResponse(true, "Today's trips retrieved successfully", tripDtos);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving today's trips: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getTripStatusHistory(Integer tripId) {
        try {
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

            List<TripStatus> statusHistory = tripStatusRepository.findByTripOrderByStatusTimeDesc(trip);
            List<TripStatusResponseDto> statusDtos = statusHistory.stream()
                    .map(this::mapToStatusResponse)
                    .collect(Collectors.toList());

            return new ApiResponse(true, "Trip status history retrieved successfully", statusDtos);
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving trip status history: " + e.getMessage(), null);
        }
    }

    private TripStatusResponseDto mapToStatusResponse(TripStatus tripStatus) {
        return TripStatusResponseDto.builder()
                .tripStatusId(tripStatus.getTripStatusId())
                .tripId(tripStatus.getTrip().getTripId())
                .tripName(tripStatus.getTrip().getTripName())
                .status(tripStatus.getStatus().toString())
                .statusTime(tripStatus.getStatusTime())
                .startTime(tripStatus.getStartTime())
                .endTime(tripStatus.getEndTime())
                .totalTimeMinutes(tripStatus.getTotalTimeMinutes())
                .remarks(tripStatus.getRemarks())
                .createdBy(tripStatus.getCreatedBy())
                .createdDate(tripStatus.getCreatedDate())
                .build();
    }
}
