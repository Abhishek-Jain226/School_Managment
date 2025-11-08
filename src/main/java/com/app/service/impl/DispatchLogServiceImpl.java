package com.app.service.impl;

import com.app.entity.*;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.DispatchLogRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.DispatchLogResponseDto;
import com.app.repository.*;
import com.app.service.IDispatchLogService;
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
public class DispatchLogServiceImpl implements IDispatchLogService {

	@Autowired
    private DispatchLogRepository dispatchLogRepository;
	@Autowired
    private TripRepository tripRepository;
	@Autowired
    private StudentRepository studentRepository;
	@Autowired
    private SchoolRepository schoolRepository;
	@Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public ApiResponse createDispatchLog(DispatchLogRequestDto request) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));

        DispatchLog log = DispatchLog.builder()
                .trip(trip)
                .student(student)
                .school(school)
                .vehicle(vehicle)
                .eventType(request.getEventType())
                .remarks(request.getRemarks())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        DispatchLog saved = dispatchLogRepository.save(log);

        return new ApiResponse(true, "Dispatch log created successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse updateDispatchLog(Integer dispatchLogId, DispatchLogRequestDto request) {
        DispatchLog log = dispatchLogRepository.findById(dispatchLogId)
                .orElseThrow(() -> new ResourceNotFoundException("DispatchLog not found with ID: " + dispatchLogId));

        if (request.getTripId() != null) {
            Trip trip = tripRepository.findById(request.getTripId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + request.getTripId()));
            log.setTrip(trip);
        }
        if (request.getStudentId() != null) {
            Student student = studentRepository.findById(request.getStudentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));
            log.setStudent(student);
        }
        if (request.getSchoolId() != null) {
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));
            log.setSchool(school);
        }
        if (request.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + request.getVehicleId()));
            log.setVehicle(vehicle);
        }

        log.setEventType(request.getEventType());
        log.setRemarks(request.getRemarks());
        log.setUpdatedBy(request.getUpdatedBy());
        log.setUpdatedDate(LocalDateTime.now());

        DispatchLog updated = dispatchLogRepository.save(log);

        return new ApiResponse(true, "Dispatch log updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse getDispatchLogById(Integer dispatchLogId) {
        DispatchLog log = dispatchLogRepository.findById(dispatchLogId)
                .orElseThrow(() -> new ResourceNotFoundException("DispatchLog not found with ID: " + dispatchLogId));

        return new ApiResponse(true, "Dispatch log fetched successfully", mapToResponse(log));
    }

    @Override
    public ApiResponse getDispatchLogsByTrip(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        List<DispatchLogResponseDto> logs = dispatchLogRepository.findByTrip(trip).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Dispatch logs for trip fetched successfully", logs);
    }

    @Override
    public ApiResponse getDispatchLogsByVehicle(Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + vehicleId));

        List<DispatchLogResponseDto> logs = dispatchLogRepository.findByVehicle(vehicle).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Dispatch logs for vehicle fetched successfully", logs);
    }

    // ---------------- Mapper ----------------
    private DispatchLogResponseDto mapToResponse(DispatchLog log) {
        return DispatchLogResponseDto.builder()
                .dispatchLogId(log.getDispatchLogId())
                .tripId(log.getTrip() != null ? log.getTrip().getTripId() : null)
                .tripName(log.getTrip() != null ? log.getTrip().getTripName() : null)
                .studentId(log.getStudent() != null ? log.getStudent().getStudentId() : null)
                .studentName(log.getStudent() != null ? 
                        log.getStudent().getFirstName() + " " + log.getStudent().getLastName() : null)
                .schoolId(log.getSchool() != null ? log.getSchool().getSchoolId() : null)
                .schoolName(log.getSchool() != null ? log.getSchool().getSchoolName() : null)
                .vehicleId(log.getVehicle() != null ? log.getVehicle().getVehicleId() : null)
                .vehicleNumber(log.getVehicle() != null ? log.getVehicle().getVehicleNumber() : null)
                .eventType(log.getEventType() != null ? log.getEventType().name() : null)
                .remarks(log.getRemarks())
                .createdBy(log.getCreatedBy())
                .createdDate(log.getCreatedDate())
                .updatedBy(log.getUpdatedBy())
                .updatedDate(log.getUpdatedDate())
                .build();
    }
}
