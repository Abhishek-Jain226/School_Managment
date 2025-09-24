package com.app.service.impl;

import com.app.entity.School;
import com.app.entity.Trip;
import com.app.entity.Vehicle;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.TripRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.TripResponseDto;
import com.app.repository.SchoolRepository;
import com.app.repository.TripRepository;
import com.app.repository.VehicleRepository;
import com.app.service.ITripService;
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
public class TripServiceImpl implements ITripService {

	@Autowired
    private TripRepository tripRepository;
	@Autowired
    private SchoolRepository schoolRepository;
	@Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public ApiResponse createTrip(TripRequestDto request) {
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        Trip trip = new Trip();
        trip.setSchool(school);
        trip.setVehicle(vehicle);
        trip.setTripName(request.getTripName());
        trip.setTripNumber(request.getTripNumber());
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
        return TripResponseDto.builder()
                .tripId(trip.getTripId())
                .schoolId(trip.getSchool().getSchoolId())
                .schoolName(trip.getSchool().getSchoolName())
                .vehicleId(trip.getVehicle().getVehicleId())
                .vehicleNumber(trip.getVehicle().getVehicleNumber())
                .tripName(trip.getTripName())
                .tripNumber(trip.getTripNumber())
                .isActive(trip.getIsActive())
                .createdBy(trip.getCreatedBy())
                .createdDate(trip.getCreatedDate())
                .updatedBy(trip.getUpdatedBy())
                .updatedDate(trip.getUpdatedDate())
                .build();
    }
}
