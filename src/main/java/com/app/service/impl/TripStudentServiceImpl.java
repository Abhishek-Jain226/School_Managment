package com.app.service.impl;

import com.app.entity.School;
import com.app.entity.Student;
import com.app.entity.Trip;
import com.app.entity.TripStudent;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.TripStudentRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.TripStudentResponseDto;
import com.app.repository.SchoolRepository;
import com.app.repository.StudentRepository;
import com.app.repository.TripRepository;
import com.app.repository.TripStudentRepository;
import com.app.service.ITripStudentService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TripStudentServiceImpl implements ITripStudentService {

	@Autowired
    private TripStudentRepository tripStudentRepository;
	@Autowired
    private TripRepository tripRepository;
	@Autowired
    private StudentRepository studentRepository;
	@Autowired
    private SchoolRepository schoolRepository;

    @Override
    public ApiResponse assignStudentToTrip(TripStudentRequestDto request) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));

        TripStudent tripStudent = TripStudent.builder()
                .trip(trip)
                .student(student)
                .pickupOrder(request.getPickupOrder())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        TripStudent saved = tripStudentRepository.save(tripStudent);

        return new ApiResponse(true, "Student assigned to trip successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse updateTripStudent(Integer tripStudentId, TripStudentRequestDto request) {
        TripStudent tripStudent = tripStudentRepository.findById(tripStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("TripStudent not found with ID: " + tripStudentId));

        if (request.getTripId() != null) {
            Trip trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));
            tripStudent.setTrip(trip);
        }

        if (request.getStudentId() != null) {
            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));
            tripStudent.setStudent(student);
        }

        tripStudent.setPickupOrder(request.getPickupOrder());
        tripStudent.setUpdatedBy(request.getUpdatedBy());
        tripStudent.setUpdatedDate(LocalDateTime.now());

        TripStudent updated = tripStudentRepository.save(tripStudent);

        return new ApiResponse(true, "TripStudent updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse removeStudentFromTrip(Integer tripStudentId) {
        TripStudent tripStudent = tripStudentRepository.findById(tripStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("TripStudent not found with ID: " + tripStudentId));

        tripStudentRepository.delete(tripStudent);

        return new ApiResponse(true, "Student removed from trip successfully", null);
    }

    @Override
    public ApiResponse getStudentsByTrip(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        // Directly fetch all TripStudent entities by trip
        List<TripStudent> students = tripStudentRepository.findByTrip(trip);

        return new ApiResponse(true, "Students fetched successfully", students);
    }

    @Override
    public ApiResponse getAllAssignmentsBySchool(Integer schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        // Get all trips for the school
        List<Trip> schoolTrips = tripRepository.findBySchool(school);
        
        // Get all trip-student assignments for these trips
        List<TripStudent> allAssignments = new ArrayList<>();
        for (Trip trip : schoolTrips) {
            List<TripStudent> tripAssignments = tripStudentRepository.findByTrip(trip);
            allAssignments.addAll(tripAssignments);
        }

        // Convert to response DTOs with proper names
        List<TripStudentResponseDto> responseList = allAssignments.stream()
                .map(this::mapToResponseWithNames)
                .collect(Collectors.toList());

        return new ApiResponse(true, "All trip-student assignments fetched successfully", responseList);
    }

    // ------------------ Private Mapper ------------------
    private TripStudentResponseDto mapToResponse(TripStudent tripStudent) {
        return TripStudentResponseDto.builder()
                .tripStudentId(tripStudent.getTripStudentId())
                .tripId(tripStudent.getTrip().getTripId())
                .studentId(tripStudent.getStudent().getStudentId())
                .pickupOrder(tripStudent.getPickupOrder())
                .createdBy(tripStudent.getCreatedBy())
                .createdDate(tripStudent.getCreatedDate())
                .updatedBy(tripStudent.getUpdatedBy())
                .updatedDate(tripStudent.getUpdatedDate())
                .build();
    }

    // ------------------ Private Mapper with Names ------------------
    private TripStudentResponseDto mapToResponseWithNames(TripStudent tripStudent) {
        return TripStudentResponseDto.builder()
                .tripStudentId(tripStudent.getTripStudentId())
                .tripId(tripStudent.getTrip().getTripId())
                .tripName(tripStudent.getTrip().getTripName())
                .studentId(tripStudent.getStudent().getStudentId())
                .studentName(tripStudent.getStudent().getFirstName() + " " + tripStudent.getStudent().getLastName())
                .pickupOrder(tripStudent.getPickupOrder())
                .createdBy(tripStudent.getCreatedBy())
                .createdDate(tripStudent.getCreatedDate())
                .updatedBy(tripStudent.getUpdatedBy())
                .updatedDate(tripStudent.getUpdatedDate())
                .build();
    }
}
