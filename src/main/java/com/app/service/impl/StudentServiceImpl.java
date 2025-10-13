package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.app.entity.ClassMaster;
import com.app.entity.Role;
import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.SectionMaster;
import com.app.entity.Student;
import com.app.entity.StudentParent;
import com.app.entity.Trip;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.request.StudentRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.StudentResponseDto;
import com.app.repository.ClassMasterRepository;
import com.app.repository.RoleRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolUserRepository;
import com.app.repository.SectionMasterRepository;
import com.app.repository.StudentParentRepository;
import com.app.repository.StudentRepository;
import com.app.repository.TripRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import com.app.service.IPendingUserService;
import com.app.service.IStudentService;

@Service
public class StudentServiceImpl implements IStudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private IPendingUserService pendingUserService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentParentRepository studentParentRepository;
    
    @Autowired
    private ClassMasterRepository classMasterRepository;
    
    @Autowired
    private SectionMasterRepository sectionMasterRepository;
    
    @Autowired
    private SchoolUserRepository schoolUserRepository;
    
    @Autowired
    private UserRoleRepository userRoleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse createStudent(StudentRequestDto request) {
        // 1. Validate school
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

        // 2. Validate class master
        ClassMaster classMaster = classMasterRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + request.getClassId()));

        // 3. Validate section master
        SectionMaster sectionMaster = sectionMasterRepository.findById(request.getSectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found with ID: " + request.getSectionId()));

        // 4. Check for duplicate email
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (studentRepository.existsByEmail(request.getEmail())) {
                return new ApiResponse(false, "Student with this email already exists", null);
            }
        }

        // 3. Check for duplicate primary contact
        if (studentRepository.existsByPrimaryContactNumber(request.getPrimaryContactNumber())) {
            return new ApiResponse(false, "Student with this primary contact number already exists", null);
        }

        // 4. Check for duplicate alternate contact (if provided)
        if (request.getAlternateContactNumber() != null && !request.getAlternateContactNumber().trim().isEmpty()) {
            if (studentRepository.existsByAlternateContactNumber(request.getAlternateContactNumber())) {
                return new ApiResponse(false, "Student with this alternate contact number already exists", null);
            }
        }

        // 5. Save student
        Student student = Student.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .classMaster(classMaster)
                .sectionMaster(sectionMaster)
                .studentPhoto(request.getStudentPhoto())
                .school(school)
                .motherName(request.getMotherName())
                .fatherName(request.getFatherName())
                .primaryContactNumber(request.getPrimaryContactNumber())
                .alternateContactNumber(request.getAlternateContactNumber())
                .email(request.getEmail())
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        Student savedStudent = studentRepository.save(student);

        // 3. Save StudentParent (without user initially)
        StudentParent sp = StudentParent.builder()
                .student(savedStudent)
                .relation(request.getParentRelation())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        studentParentRepository.save(sp);

        // 4. Assign Parent Role
        Role parentRole = roleRepository.findByRoleName("PARENT")
                .orElseThrow(() -> new ResourceNotFoundException("Role PARENT not found"));

        // 5. Create PendingUser for parent
        PendingUserRequestDTO pendingReq = PendingUserRequestDTO.builder()
                .entityType("PARENT")
                .entityId(sp.getStudentParentId().longValue())
                .email(request.getEmail())
                .contactNumber(request.getPrimaryContactNumber())
                .roleId(parentRole.getRoleId())
                .createdBy(request.getCreatedBy())
                .build();

        pendingUserService.createPendingUser(pendingReq);

        // 6. Note: SchoolUser entry will be created when parent activates account
        // This ensures proper User account exists before creating SchoolUser relationship

        return new ApiResponse(true,
                "Student registered successfully. Parent activation link sent to email.",
                Map.of("studentId", savedStudent.getStudentId(), "studentParentId", sp.getStudentParentId()));
    }

    
    
    @Override
    public ApiResponse updateStudent(Integer studentId, StudentRequestDto request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        // Validate class master if provided
        ClassMaster classMaster = null;
        if (request.getClassId() != null) {
            classMaster = classMasterRepository.findById(request.getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found with ID: " + request.getClassId()));
        }

        // Validate section master if provided
        SectionMaster sectionMaster = null;
        if (request.getSectionId() != null) {
            sectionMaster = sectionMasterRepository.findById(request.getSectionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Section not found with ID: " + request.getSectionId()));
        }

        // duplicate checks (email, phone)
        if (request.getEmail() != null && !student.getEmail().equals(request.getEmail()) &&
                studentRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Student with this email already exists", null);
        }

        if (request.getPrimaryContactNumber() != null &&
                !student.getPrimaryContactNumber().equals(request.getPrimaryContactNumber()) &&
                studentRepository.existsByPrimaryContactNumber(request.getPrimaryContactNumber())) {
            return new ApiResponse(false, "Student with this contact number already exists", null);
        }

        // ✅ update school only if provided
        if (request.getSchoolId() != null) {
            School school = schoolRepository.findById(request.getSchoolId())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));
            student.setSchool(school);
        }

        // update fields (null checks optional)
        student.setFirstName(request.getFirstName());
        student.setMiddleName(request.getMiddleName());
        student.setLastName(request.getLastName());
        student.setGender(request.getGender());
        if (classMaster != null) {
            student.setClassMaster(classMaster);
        }
        if (sectionMaster != null) {
            student.setSectionMaster(sectionMaster);
        }
        student.setStudentPhoto(request.getStudentPhoto());
        student.setMotherName(request.getMotherName());
        student.setFatherName(request.getFatherName());
        student.setPrimaryContactNumber(request.getPrimaryContactNumber());
        student.setAlternateContactNumber(request.getAlternateContactNumber());
        student.setEmail(request.getEmail());
        student.setIsActive(request.getIsActive());
        student.setUpdatedBy(request.getUpdatedBy());
        student.setUpdatedDate(LocalDateTime.now());

        Student updated = studentRepository.save(student);

        return new ApiResponse(true, "Student updated successfully", mapToResponse(updated));
    }


    @Override
    public ApiResponse deleteStudent(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        studentRepository.delete(student);

        return new ApiResponse(true, "Student deleted successfully", null);
    }

    @Override
    public ApiResponse getStudentById(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        return new ApiResponse(true, "Student fetched successfully", mapToResponse(student));
    }

    @Override
    public ApiResponse getAllStudents(Integer schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        List<StudentResponseDto> students = studentRepository.findBySchoolSchoolId(schoolId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new ApiResponse(true, "Students fetched successfully", students);
    }

    @Override
    public ApiResponse getStudentsByTrip(Integer tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with ID: " + tripId));

        List<StudentResponseDto> students = studentRepository.findStudentsByTripId(tripId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "Students for trip fetched successfully", students);
    }

    // Helper mapper
    private StudentResponseDto mapToResponse(Student student) {
        return StudentResponseDto.builder()
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .middleName(student.getMiddleName())
                .lastName(student.getLastName())
                .gender(student.getGender())
                .classId(student.getClassMaster() != null ? student.getClassMaster().getClassId() : null)
                .className(student.getClassMaster() != null ? student.getClassMaster().getClassName() : null)
                .sectionId(student.getSectionMaster() != null ? student.getSectionMaster().getSectionId() : null)
                .sectionName(student.getSectionMaster() != null ? student.getSectionMaster().getSectionName() : null)
                .studentPhoto(student.getStudentPhoto())
                .schoolId(student.getSchool() != null ? student.getSchool().getSchoolId() : null)
                .schoolName(student.getSchool() != null ? student.getSchool().getSchoolName() : null)
                .motherName(student.getMotherName())
                .fatherName(student.getFatherName())
                .primaryContactNumber(student.getPrimaryContactNumber())
                .alternateContactNumber(student.getAlternateContactNumber())
                .email(student.getEmail())
                .isActive(student.getIsActive())
                .createdBy(student.getCreatedBy())
                .createdDate(student.getCreatedDate())
                .updatedBy(student.getUpdatedBy())
                .updatedDate(student.getUpdatedDate())
                .build();
    }

    @Override
    public long getStudentCountBySchool(Integer schoolId) {
        return studentRepository.countBySchool_SchoolId(schoolId);
    }


	
}
