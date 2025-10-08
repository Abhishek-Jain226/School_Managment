package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.DispatchLog;
import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.Student;
import com.app.entity.Trip;
import com.app.entity.TripStudent;
import com.app.entity.User;
import com.app.Enum.EventType;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.response.ApiResponse;
import com.app.repository.DispatchLogRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolUserRepository;
import com.app.repository.StudentRepository;
import com.app.repository.TripRepository;
import com.app.repository.TripStudentRepository;
import com.app.repository.UserRepository;
import com.app.service.IGateStaffService;

@Service
@Transactional
public class GateStaffServiceImpl implements IGateStaffService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SchoolUserRepository schoolUserRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private TripStudentRepository tripStudentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DispatchLogRepository dispatchLogRepository;

    @Override
    public ApiResponse getGateStaffDashboard(Integer userId) {
        System.out.println("🔍 getGateStaffDashboard called with userId: " + userId);
        try {
            // Validate user exists (gate staff is a user with GATE_STAFF role)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            System.out.println("🔍 User found: " + user.getUserName());

            // Get school from SchoolUser relationship
            SchoolUser schoolUser = schoolUserRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("User is not associated with any school"));

            School school = schoolUser.getSchool();
            System.out.println("🔍 School: " + school.getSchoolName());

            // Get ALL students from this school first
            List<Student> allStudents = studentRepository.findAll();
            List<Student> schoolStudents = new ArrayList<>();
            
            // Filter students by school
            for (Student student : allStudents) {
                if (student.getSchool() != null && student.getSchool().getSchoolId().equals(school.getSchoolId())) {
                    schoolStudents.add(student);
                }
            }
            
            System.out.println("🔍 Found " + schoolStudents.size() + " students in school: " + school.getSchoolName());

            // Get trips for this school
            List<Trip> trips = tripRepository.findBySchool(school);
            System.out.println("🔍 Found " + trips.size() + " trips for school");

            int totalStudents = schoolStudents.size(); // Use all students count
            int studentsWithGateEntry = 0;
            int studentsWithGateExit = 0;
            List<Map<String, Object>> studentsByTrip = new ArrayList<>();

            // Create a "All Students" section first
            Map<String, Object> allStudentsTrip = new HashMap<>();
            allStudentsTrip.put("tripId", 0);
            allStudentsTrip.put("tripName", "All Students");
            allStudentsTrip.put("tripNumber", "ALL-001");
            allStudentsTrip.put("vehicleNumber", "N/A");
            allStudentsTrip.put("driverName", "N/A");
            allStudentsTrip.put("studentCount", schoolStudents.size());
            
            List<Map<String, Object>> allStudentsList = new ArrayList<>();
            
            for (Student student : schoolStudents) {
                Map<String, Object> studentMap = new HashMap<>();
                studentMap.put("studentId", student.getStudentId());
                studentMap.put("firstName", student.getFirstName());
                studentMap.put("middleName", student.getMiddleName());
                studentMap.put("lastName", student.getLastName());
                studentMap.put("grade", student.getClassMaster() != null ? student.getClassMaster().getClassName() : "N/A");
                studentMap.put("section", student.getSectionMaster() != null ? student.getSectionMaster().getSectionName() : "N/A");
                studentMap.put("tripId", 0);

                // Check gate entry/exit status (simplified)
                boolean hasEntry = false;
                boolean hasExit = false;
                
                // Get all dispatch logs for this student
                List<DispatchLog> studentLogs = dispatchLogRepository.findAll();
                for (DispatchLog log : studentLogs) {
                    if (log.getStudent() != null && log.getStudent().getStudentId().equals(student.getStudentId())) {
                        if (log.getEventType() == EventType.GATE_ENTRY) {
                            hasEntry = true;
                        }
                        if (log.getEventType() == EventType.GATE_EXIT) {
                            hasExit = true;
                        }
                    }
                }

                studentMap.put("hasGateEntry", hasEntry);
                studentMap.put("hasGateExit", hasExit);

                if (hasEntry) {
                    studentsWithGateEntry++;
                }
                if (hasExit) {
                    studentsWithGateExit++;
                }
                allStudentsList.add(studentMap);
            }
            
            allStudentsTrip.put("students", allStudentsList);
            studentsByTrip.add(allStudentsTrip);

            // Now add trip-specific students
            for (Trip trip : trips) {
                List<TripStudent> tripStudents = tripStudentRepository.findByTrip(trip);

                Map<String, Object> tripMap = new HashMap<>();
                tripMap.put("tripId", trip.getTripId());
                tripMap.put("tripName", trip.getTripName());
                tripMap.put("tripNumber", trip.getTripNumber());
                tripMap.put("vehicleNumber", trip.getVehicle() != null ? trip.getVehicle().getVehicleNumber() : "N/A");
                tripMap.put("driverName", trip.getDriver() != null ? trip.getDriver().getDriverName() : "N/A");
                tripMap.put("studentCount", tripStudents.size());

                List<Map<String, Object>> studentsInTrip = new ArrayList<>();
                for (TripStudent ts : tripStudents) {
                    Student student = ts.getStudent();
                    Map<String, Object> studentMap = new HashMap<>();
                    studentMap.put("studentId", student.getStudentId());
                    studentMap.put("firstName", student.getFirstName());
                    studentMap.put("middleName", student.getMiddleName());
                    studentMap.put("lastName", student.getLastName());
                    studentMap.put("grade", student.getClassMaster() != null ? student.getClassMaster().getClassName() : "N/A");
                    studentMap.put("section", student.getSectionMaster() != null ? student.getSectionMaster().getSectionName() : "N/A");
                    studentMap.put("tripId", trip.getTripId());

                    // Check gate entry/exit status (simplified)
                    boolean hasEntry = false;
                    boolean hasExit = false;
                    
                    // Get all dispatch logs for this student
                    List<DispatchLog> studentLogs = dispatchLogRepository.findAll();
                    for (DispatchLog log : studentLogs) {
                        if (log.getStudent() != null && log.getStudent().getStudentId().equals(student.getStudentId())) {
                            if (log.getEventType() == EventType.GATE_ENTRY) {
                                hasEntry = true;
                            }
                            if (log.getEventType() == EventType.GATE_EXIT) {
                                hasExit = true;
                            }
                        }
                    }

                    studentMap.put("hasGateEntry", hasEntry);
                    studentMap.put("hasGateExit", hasExit);
                    studentsInTrip.add(studentMap);
                }
                // Don't double-count students - they're already counted in all students
                tripMap.put("students", studentsInTrip);

                studentsByTrip.add(tripMap);
            }

            // Create dashboard data
            Map<String, Object> dashboard = new HashMap<>();
            dashboard.put("gateStaffId", userId);
            dashboard.put("gateStaffName", user.getUserName());
            dashboard.put("schoolId", school.getSchoolId());
            dashboard.put("schoolName", school.getSchoolName());
            dashboard.put("totalStudents", totalStudents);
            dashboard.put("studentsWithGateEntry", studentsWithGateEntry);
            dashboard.put("studentsWithGateExit", studentsWithGateExit);
            dashboard.put("studentsByTrip", studentsByTrip);
            dashboard.put("date", LocalDateTime.now().toLocalDate());

            System.out.println("🔍 Gate staff dashboard data created successfully for user: " + user.getUserName());
            return new ApiResponse(true, "Gate staff dashboard data retrieved successfully", dashboard);

        } catch (Exception e) {
            System.out.println("🔍 Error in getGateStaffDashboard: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving gate staff dashboard: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getStudentsByTrip(Integer userId, Integer tripId) {
        System.out.println("🔍 getStudentsByTrip called with userId: " + userId + ", tripId: " + tripId);
        try {
            // Validate user exists (gate staff is a user with GATE_STAFF role)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Validate trip exists
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

            // Get students assigned to this trip
            List<TripStudent> tripStudents = tripStudentRepository.findByTrip(trip);

            List<Map<String, Object>> students = new ArrayList<>();
            for (TripStudent ts : tripStudents) {
                Student student = ts.getStudent();
                Map<String, Object> studentMap = new HashMap<>();
                studentMap.put("studentId", student.getStudentId());
                studentMap.put("firstName", student.getFirstName());
                studentMap.put("middleName", student.getMiddleName());
                studentMap.put("lastName", student.getLastName());
                studentMap.put("grade", student.getClassMaster() != null ? student.getClassMaster().getClassName() : "N/A");
                studentMap.put("section", student.getSectionMaster() != null ? student.getSectionMaster().getSectionName() : "N/A");

                // Check gate entry/exit status (simplified)
                boolean hasEntry = false;
                boolean hasExit = false;
                
                List<DispatchLog> studentLogs = dispatchLogRepository.findAll();
                for (DispatchLog log : studentLogs) {
                    if (log.getStudent() != null && log.getStudent().getStudentId().equals(student.getStudentId())) {
                        if (log.getEventType() == EventType.GATE_ENTRY) {
                            hasEntry = true;
                        }
                        if (log.getEventType() == EventType.GATE_EXIT) {
                            hasExit = true;
                        }
                    }
                }

                studentMap.put("hasGateEntry", hasEntry);
                studentMap.put("hasGateExit", hasExit);
                students.add(studentMap);
            }

            System.out.println("🔍 Found " + students.size() + " students for trip " + tripId);
            return new ApiResponse(true, "Students for trip retrieved successfully", students);

        } catch (Exception e) {
            System.out.println("🔍 Error in getStudentsByTrip: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving students for trip: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse markGateEntry(Integer userId, Integer studentId, Integer tripId, String remarks) {
        System.out.println("🔍 markGateEntry called with userId: " + userId + ", studentId: " + studentId + ", tripId: " + tripId);
        try {
            // Validate user exists (gate staff is a user with GATE_STAFF role)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Get school from SchoolUser relationship
            SchoolUser schoolUser = schoolUserRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("User is not associated with any school"));

            School school = schoolUser.getSchool();

            // Validate student exists
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

            // Validate trip exists
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

            // Create dispatch log for gate entry
            DispatchLog dispatchLog = DispatchLog.builder()
                    .trip(trip)
                    .student(student)
                    .school(school)
                    .vehicle(trip.getVehicle())
                    .eventType(EventType.GATE_ENTRY)
                    .remarks(remarks)
                    .createdBy(user.getUserName())
                    .createdDate(LocalDateTime.now())
                    .build();

            DispatchLog saved = dispatchLogRepository.save(dispatchLog);
            System.out.println("🔍 Gate entry marked successfully with ID: " + saved.getDispatchLogId());

            return new ApiResponse(true, "Gate entry marked successfully", null);

        } catch (Exception e) {
            System.out.println("🔍 Error in markGateEntry: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error marking gate entry: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse markGateExit(Integer userId, Integer studentId, Integer tripId, String remarks) {
        System.out.println("🔍 markGateExit called with userId: " + userId + ", studentId: " + studentId + ", tripId: " + tripId);
        try {
            // Validate user exists (gate staff is a user with GATE_STAFF role)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Get school from SchoolUser relationship
            SchoolUser schoolUser = schoolUserRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("User is not associated with any school"));

            School school = schoolUser.getSchool();

            // Validate student exists
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

            // Validate trip exists
            Trip trip = tripRepository.findById(tripId)
                    .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

            // Create dispatch log for gate exit
            DispatchLog dispatchLog = DispatchLog.builder()
                    .trip(trip)
                    .student(student)
                    .school(school)
                    .vehicle(trip.getVehicle())
                    .eventType(EventType.GATE_EXIT)
                    .remarks(remarks)
                    .createdBy(user.getUserName())
                    .createdDate(LocalDateTime.now())
                    .build();

            DispatchLog saved = dispatchLogRepository.save(dispatchLog);
            System.out.println("🔍 Gate exit marked successfully with ID: " + saved.getDispatchLogId());

            return new ApiResponse(true, "Gate exit marked successfully", null);

        } catch (Exception e) {
            System.out.println("🔍 Error in markGateExit: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error marking gate exit: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getRecentDispatchLogs(Integer userId) {
        System.out.println("🔍 getRecentDispatchLogs called with userId: " + userId);
        try {
            // Validate user exists (gate staff is a user with GATE_STAFF role)
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Get school from SchoolUser relationship
            SchoolUser schoolUser = schoolUserRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("User is not associated with any school"));

            School school = schoolUser.getSchool();

            // Get recent dispatch logs for this school (simplified)
            List<DispatchLog> recentLogs = dispatchLogRepository.findAll();

            List<Map<String, Object>> logs = new ArrayList<>();
            for (DispatchLog log : recentLogs) {
                if (log.getSchool() != null && log.getSchool().getSchoolId().equals(school.getSchoolId())) {
                    Map<String, Object> logMap = new HashMap<>();
                    logMap.put("dispatchLogId", log.getDispatchLogId());
                    logMap.put("eventType", log.getEventType().toString());
                    logMap.put("studentName", log.getStudent() != null ? 
                        log.getStudent().getFirstName() + " " + log.getStudent().getLastName() : "N/A");
                    logMap.put("vehicleNumber", log.getVehicle() != null ? log.getVehicle().getVehicleNumber() : "N/A");
                    logMap.put("tripName", log.getTrip() != null ? log.getTrip().getTripName() : "N/A");
                    logMap.put("remarks", log.getRemarks());
                    logMap.put("createdDate", log.getCreatedDate());
                    logs.add(logMap);
                }
            }

            System.out.println("🔍 Found " + logs.size() + " recent dispatch logs for school: " + school.getSchoolName());
            return new ApiResponse(true, "Recent dispatch logs retrieved successfully", logs);

        } catch (Exception e) {
            System.out.println("🔍 Error in getRecentDispatchLogs: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving recent dispatch logs: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getGateStaffByUserId(Integer userId) {
        System.out.println("🔍 getGateStaffByUserId called with userId: " + userId);
        try {
            // Validate user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

            // Get school from SchoolUser relationship
            SchoolUser schoolUser = schoolUserRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("User is not associated with any school"));

            School school = schoolUser.getSchool();

            // Create gate staff response
            Map<String, Object> gateStaffData = new HashMap<>();
            gateStaffData.put("gateStaffId", userId);
            gateStaffData.put("gateStaffName", user.getUserName());
            gateStaffData.put("schoolId", school.getSchoolId());
            gateStaffData.put("schoolName", school.getSchoolName());
            gateStaffData.put("email", user.getEmail());
            gateStaffData.put("contactNumber", user.getContactNumber());

            System.out.println("🔍 Found gate staff: " + user.getUserName() + " (ID: " + userId + ")");

            return new ApiResponse(true, "Gate staff retrieved successfully", gateStaffData);

        } catch (Exception e) {
            System.out.println("🔍 Error in getGateStaffByUserId: " + e.getMessage());
            e.printStackTrace();
            return new ApiResponse(false, "Error retrieving gate staff: " + e.getMessage(), null);
        }
    }
}