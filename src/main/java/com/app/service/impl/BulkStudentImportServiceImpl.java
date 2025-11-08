package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.ClassMaster;
import com.app.entity.Role;
import com.app.entity.School;
import com.app.entity.SectionMaster;
import com.app.entity.Student;
import com.app.entity.StudentParent;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.BulkStudentImportRequestDto;
import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.request.StudentRequestDto;
import com.app.payload.response.BulkImportResultDto;
import com.app.payload.response.BulkImportResultDto.StudentImportResultDto;
import com.app.repository.ClassMasterRepository;
import com.app.repository.RoleRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.SectionMasterRepository;
import com.app.repository.StudentParentRepository;
import com.app.repository.StudentRepository;
import com.app.service.IBulkStudentImportService;
import com.app.service.IPendingUserService;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class BulkStudentImportServiceImpl implements IBulkStudentImportService {

    private static final Logger log = LoggerFactory.getLogger(BulkStudentImportServiceImpl.class);

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;
    
    @Autowired
    private ClassMasterRepository classMasterRepository;
    
    @Autowired
    private SectionMasterRepository sectionMasterRepository;
    
    @Autowired
    private StudentParentRepository studentParentRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private IPendingUserService pendingUserService;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.frontend.activation-url}")
    private String activationBaseUrl;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    @Override
    public BulkImportResultDto importStudents(BulkStudentImportRequestDto request) {
        log.info("Starting bulk import for {} students", request.getStudents().size());
        
        // ✅ Pre-request validation
        if (request.getStudents() == null || request.getStudents().isEmpty()) {
            return BulkImportResultDto.builder()
                .totalRows(0)
                .successfulImports(0)
                .failedImports(0)
                .results(new ArrayList<>())
                .errors(List.of("No students provided for import"))
                .success(false)
                .message("Import failed: No students provided")
                .build();
        }
        
        if (request.getSchoolId() == null) {
            return BulkImportResultDto.builder()
                .totalRows(request.getStudents().size())
                .successfulImports(0)
                .failedImports(request.getStudents().size())
                .results(new ArrayList<>())
                .errors(List.of("School ID is required"))
                .success(false)
                .message("Import failed: School ID is required")
                .build();
        }
        
        List<StudentImportResultDto> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int successfulImports = 0;
        int failedImports = 0;
        
        // Validate school exists
        School school;
        try {
            school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));
        } catch (ResourceNotFoundException e) {
            return BulkImportResultDto.builder()
                .totalRows(request.getStudents().size())
                .successfulImports(0)
                .failedImports(request.getStudents().size())
                .results(new ArrayList<>())
                .errors(List.of(e.getMessage()))
                .success(false)
                .message("Import failed: " + e.getMessage())
                .build();
        }
        
        // Get parent role
        Role parentRole = roleRepository.findByRoleName("PARENT")
            .orElseThrow(() -> new ResourceNotFoundException("Parent role not found"));
        
        // Process each student
        for (int i = 0; i < request.getStudents().size(); i++) {
            StudentRequestDto studentDto = request.getStudents().get(i);
            int rowNumber = i + 1;
            
            try {
                // Validate student data
                List<String> validationErrors = validateStudentData(studentDto, school.getSchoolId());
                if (!validationErrors.isEmpty()) {
                    results.add(StudentImportResultDto.builder()
                        .studentId(null)
                        .studentName(studentDto.getFirstName() + " " + studentDto.getLastName())
                        .parentEmail(null)
                        .status("ERROR")
                        .errorMessage(String.join(", ", validationErrors))
                        .rowNumber(rowNumber)
                        .build());
                    failedImports++;
                    continue;
                }
                
                // Generate parent email
                String parentEmail = generateParentEmail(studentDto, request.getSchoolDomain());
                
                // Create student
                Student student = createStudent(studentDto, school, request.getCreatedBy());
                Student savedStudent = studentRepository.save(student);
                
                // Create student-parent relationship
                StudentParent studentParent = createStudentParent(savedStudent, studentDto, request.getCreatedBy());
                StudentParent savedStudentParent = studentParentRepository.save(studentParent);
                
                // ✅ Create pending user for parent activation
                // User, UserRole, and SchoolUser will be created when parent activates account (clicks activation link)
                if (request.getSendActivationEmails()) {
                    createPendingUserForParent(savedStudent, savedStudentParent, parentEmail, parentRole, request.getCreatedBy());
                }
                
                results.add(StudentImportResultDto.builder()
                    .studentId(savedStudent.getStudentId())
                    .studentName(savedStudent.getFirstName() + " " + savedStudent.getLastName())
                    .parentEmail(parentEmail)
                    .status("SUCCESS")
                    .errorMessage(null)
                    .rowNumber(rowNumber)
                    .build());
                
                successfulImports++;
                log.info("Successfully imported student: {} {}", savedStudent.getFirstName(), savedStudent.getLastName());
                
            } catch (Exception e) {
                log.error("Failed to import student at row {}: {}", rowNumber, e.getMessage());
                results.add(StudentImportResultDto.builder()
                    .studentId(null)
                    .studentName(studentDto.getFirstName() + " " + studentDto.getLastName())
                    .parentEmail(null)
                    .status("ERROR")
                    .errorMessage(e.getMessage())
                    .rowNumber(rowNumber)
                    .build());
                failedImports++;
                errors.add("Row " + rowNumber + ": " + e.getMessage());
            }
        }
        
        log.info("Bulk import completed. Success: {}, Failed: {}", successfulImports, failedImports);
        
        return BulkImportResultDto.builder()
            .totalRows(request.getStudents().size())
            .successfulImports(successfulImports)
            .failedImports(failedImports)
            .results(results)
            .errors(errors)
            .success(failedImports == 0)
            .message(String.format("Import completed. %d successful, %d failed", successfulImports, failedImports))
            .build();
    }

    @Override
    public BulkImportResultDto validateStudents(BulkStudentImportRequestDto request) {
        log.info("Validating {} students", request.getStudents().size());
        
        List<StudentImportResultDto> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int validRows = 0;
        int invalidRows = 0;
        
        // Validate school exists
        if (!schoolRepository.existsById(request.getSchoolId())) {
            errors.add("School not found with ID: " + request.getSchoolId());
            return BulkImportResultDto.builder()
                .totalRows(request.getStudents().size())
                .successfulImports(0)
                .failedImports(request.getStudents().size())
                .results(results)
                .errors(errors)
                .success(false)
                .message("Validation failed: School not found")
                .build();
        }
        
        School school = schoolRepository.findById(request.getSchoolId()).orElse(null);
        
        // Validate each student
        for (int i = 0; i < request.getStudents().size(); i++) {
            StudentRequestDto studentDto = request.getStudents().get(i);
            int rowNumber = i + 1;
            
            List<String> validationErrors = validateStudentData(studentDto, school.getSchoolId());
            
            if (validationErrors.isEmpty()) {
                results.add(StudentImportResultDto.builder()
                    .studentId(null)
                    .studentName(studentDto.getFirstName() + " " + studentDto.getLastName())
                    .parentEmail(generateParentEmail(studentDto, request.getSchoolDomain()))
                    .status("VALID")
                    .errorMessage(null)
                    .rowNumber(rowNumber)
                    .build());
                validRows++;
            } else {
                results.add(StudentImportResultDto.builder()
                    .studentId(null)
                    .studentName(studentDto.getFirstName() + " " + studentDto.getLastName())
                    .parentEmail(null)
                    .status("INVALID")
                    .errorMessage(String.join(", ", validationErrors))
                    .rowNumber(rowNumber)
                    .build());
                invalidRows++;
                errors.add("Row " + rowNumber + ": " + String.join(", ", validationErrors));
            }
        }
        
        return BulkImportResultDto.builder()
            .totalRows(request.getStudents().size())
            .successfulImports(validRows)
            .failedImports(invalidRows)
            .results(results)
            .errors(errors)
            .success(invalidRows == 0)
            .message(String.format("Validation completed. %d valid, %d invalid", validRows, invalidRows))
            .build();
    }
    
    private List<String> validateStudentData(StudentRequestDto student, Integer schoolId) {
        List<String> errors = new ArrayList<>();
        
        // Required field validation
        if (student.getFirstName() == null || student.getFirstName().trim().isEmpty()) {
            errors.add("First name is required");
        }
        
        if (student.getLastName() == null || student.getLastName().trim().isEmpty()) {
            errors.add("Last name is required");
        }
        
        if (student.getFatherName() == null || student.getFatherName().trim().isEmpty()) {
            errors.add("Father name is required");
        }
        
        if (student.getPrimaryContactNumber() == null || student.getPrimaryContactNumber().trim().isEmpty()) {
            errors.add("Primary contact number is required");
        }
        
        // ✅ MANDATORY EMAIL VALIDATION
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            errors.add("Parent email is required for account activation");
        } else if (!EMAIL_PATTERN.matcher(student.getEmail()).matches()) {
            errors.add("Invalid email format");
        }
        
        // Class validation
        if (student.getClassId() != null) {
            ClassMaster classMaster = classMasterRepository.findByClassIdAndSchoolSchoolId(student.getClassId(), schoolId);
            if (classMaster == null) {
                errors.add("Invalid class ID: " + student.getClassId());
            }
        }
        
        // Section validation
        if (student.getSectionId() != null) {
            SectionMaster sectionMaster = sectionMasterRepository.findBySectionIdAndSchoolSchoolId(student.getSectionId(), schoolId);
            if (sectionMaster == null) {
                errors.add("Invalid section ID: " + student.getSectionId());
            }
        }
        
        // Contact number validation
        if (student.getPrimaryContactNumber() != null && !student.getPrimaryContactNumber().trim().isEmpty()) {
            if (!student.getPrimaryContactNumber().matches("\\d{10}")) {
                errors.add("Primary contact number must be 10 digits");
            }
        }
        
        if (student.getAlternateContactNumber() != null && !student.getAlternateContactNumber().trim().isEmpty()) {
            if (!student.getAlternateContactNumber().matches("\\d{10}")) {
                errors.add("Alternate contact number must be 10 digits");
            }
        }
        
        return errors;
    }
    
    private String generateParentEmail(StudentRequestDto student, String schoolDomain) {
        // ✅ Since email is now mandatory, we always use the provided email
        // This method is kept for backward compatibility but should not be used
        // in the new mandatory email approach
        
        if (student.getEmail() != null && !student.getEmail().trim().isEmpty() && 
            EMAIL_PATTERN.matcher(student.getEmail()).matches()) {
            return student.getEmail();
        }
        
        // This should never be reached with mandatory email validation
        throw new IllegalArgumentException("Email is required but not provided for student: " + 
            student.getFirstName() + " " + student.getLastName());
    }
    
    private Student createStudent(StudentRequestDto request, School school, String createdBy) {
        ClassMaster classMaster = null;
        SectionMaster sectionMaster = null;
        
        if (request.getClassId() != null) {
            classMaster = classMasterRepository.findByClassIdAndSchoolSchoolId(request.getClassId(), school.getSchoolId());
        }
        
        if (request.getSectionId() != null) {
            sectionMaster = sectionMasterRepository.findBySectionIdAndSchoolSchoolId(request.getSectionId(), school.getSchoolId());
        }
        
        // ✅ Use the provided createdBy (not "Bulk import")
        return Student.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .fatherName(request.getFatherName())
            .motherName(request.getMotherName())
            .primaryContactNumber(request.getPrimaryContactNumber())
            .alternateContactNumber(request.getAlternateContactNumber())
            .email(request.getEmail())
            .gender(request.getGender())
            .studentPhoto(request.getStudentPhoto())
            .school(school)
            .classMaster(classMaster)
            .sectionMaster(sectionMaster)
            .isActive(true)
            .createdDate(LocalDateTime.now())
            .createdBy(createdBy)  // ✅ Use actual creator name
            .build();
    }
    
    private StudentParent createStudentParent(Student student, StudentRequestDto request, String createdBy) {
        // ✅ Use parentRelation from request if provided, otherwise default to "GUARDIAN" (matches normal registration)
        String relation = (request.getParentRelation() != null && !request.getParentRelation().trim().isEmpty()) 
            ? request.getParentRelation() 
            : "GUARDIAN";  // Default to GUARDIAN instead of "Father"
        
        return StudentParent.builder()
            .student(student)
            .relation(relation)
            .createdDate(LocalDateTime.now())
            .createdBy(createdBy)  // ✅ Use actual creator name
            .build();
    }
    
    private void createPendingUserForParent(Student student, StudentParent studentParent, 
                                          String parentEmail, Role parentRole, String createdBy) {
        try {
            // ✅ FIX: Use "PARENT" entity type (same as normal registration) and StudentParent ID
            // This ensures activation flow can properly create SchoolUser and UserRole entries
            PendingUserRequestDTO pendingReq = PendingUserRequestDTO.builder()
                .entityType("PARENT")  // ✅ Changed from "STUDENT_PARENT" to "PARENT" to match activation flow
                .entityId(studentParent.getStudentParentId().longValue())  // ✅ Changed from student.getStudentId() to studentParent.getStudentParentId()
                .email(parentEmail)
                .contactNumber(student.getPrimaryContactNumber())
                .roleId(parentRole.getRoleId())
                .createdBy(createdBy)
                .build();
            
            pendingUserService.createPendingUser(pendingReq);
            log.info("Created pending user for parent: {} with entityType=PARENT, entityId={}", 
                    parentEmail, studentParent.getStudentParentId());
            
        } catch (Exception e) {
            log.error("Failed to create pending user for parent {}: {}", parentEmail, e.getMessage());
            // Don't fail the entire import for email issues
        }
    }
}
