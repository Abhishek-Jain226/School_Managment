//package com.app.service.impl;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.app.entity.DispatchLog;
//import com.app.entity.School;
//import com.app.entity.Student;
//import com.app.entity.Trip;
//import com.app.entity.User;
//import com.app.exception.ResourceNotFoundException;
//import com.app.payload.request.GateEventRequestDto;
//import com.app.payload.response.ApiResponse;
//import com.app.payload.response.TripStudentResponseDto;
//import com.app.repository.DispatchLogRepository;
//import com.app.repository.SchoolRepository;
//import com.app.repository.StudentRepository;
//import com.app.repository.TripRepository;
//import com.app.repository.UserRepository;
//import com.app.service.IGateStaffService;
//import com.app.service.INotificationService;
//
//import ch.qos.logback.core.spi.ConfigurationEvent.EventType;
//import jakarta.transaction.Transactional;
//
//@Service
//@Transactional
//public class GateStaffServiceImpl implements IGateStaffService{
//	
//	 @Autowired
//	    private TripRepository tripRepository;
//	    @Autowired
//	    private StudentRepository studentRepository;
//	    @Autowired
//	    private DispatchLogRepository dispatchLogRepository;
//	    @Autowired
//	    private SchoolRepository schoolRepository;
//	    @Autowired
//	    private UserRepository userRepository;
//	    @Autowired
//	    private INotificationService notificationService; // assume already exists
//	    
//	    
//	    
//	    @Override
//	    public ApiResponse getAssignedStudents(Integer schoolId) {
//	        School school = schoolRepository.findById(schoolId)
//	                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));
//
//	        List<TripStudentResponseDto> students = school.getStudents().stream()
//	                .flatMap(student -> student.getTrips().stream().map(trip -> 
//	                        TripStudentResponseDto.builder()
//	                                .studentId(student.getStudentId())
//	                                .studentName(student.getStudentName())
//	                                .tripId(trip.getTripId())
//	                                .tripName(trip.getTripName())
//	                                .build()))
//	                .collect(Collectors.toList());
//
//	        return new ApiResponse(true, "Assigned students fetched successfully", students);
//	    }
//	    
//	    @Override
//	    public ApiResponse markGateEntry(GateEventRequestDto request) {
//	        Student student = studentRepository.findById(request.getStudentId())
//	                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
//
//	        Trip trip = tripRepository.findById(request.getTripId())
//	                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
//
//	        User staff = userRepository.findById(request.getStaffUserId())
//	                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));
//
//	        DispatchLog log = DispatchLog.builder()
//	                .student(student)
//	                .trip(trip)
//	                .eventType(EventType.GATE_ENTRY.name())
//	                .eventTime(LocalDateTime.now())
//	                .createdBy(staff.getUserName())
//	                .build();
//
//	        dispatchLogRepository.save(log);
//
//	        // Notify Parent + School
//	        notificationService.sendNotification(student, "Student " + student.getFirstName() + " has ENTERED the school gate.");
//
//	        return new ApiResponse(true, "Gate Entry marked successfully", null);
//	    }
//	    
//	    
//	    @Override
//	    public ApiResponse markGateExit(GateEventRequestDto request) {
//	        Student student = studentRepository.findById(request.getStudentId())
//	                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
//
//	        Trip trip = tripRepository.findById(request.getTripId())
//	                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
//
//	        User staff = userRepository.findById(request.getStaffUserId())
//	                .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));
//
//	        DispatchLog log = DispatchLog.builder()
//	                .student(student)
//	                .trip(trip)
//	                .eventType("GATE_EXIT")
//	                .eventTime(LocalDateTime.now())
//	                .createdBy(staff.getUserName())
//	                .build();
//
//	        dispatchLogRepository.save(log);
//
//	        // Notify Parent + School
//	        notificationService.sendNotification(student, "Student " + student.getFirstName() + " has EXITED the school gate.");
//
//	        return new ApiResponse(true, "Gate Exit marked successfully", null);
//	    }
//
//	
//
//}
