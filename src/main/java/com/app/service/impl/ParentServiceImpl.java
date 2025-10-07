package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.Student;
import com.app.entity.StudentParent;
import com.app.entity.User;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.StudentParentRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.StudentResponseDto;
import com.app.repository.StudentParentRepository;
import com.app.repository.StudentRepository;
import com.app.repository.UserRepository;
import com.app.service.IParentService;

@Service
@Transactional
public class ParentServiceImpl implements IParentService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
    private StudentRepository studentRepository;
	@Autowired
    private StudentParentRepository studentParentRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse createParent(UserRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already exists", null);
        }

        User parent = User.builder()
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword())) 
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        User savedParent = userRepository.save(parent);

        return new ApiResponse(true, "Parent created successfully", savedParent);
    }

    @Override
    public ApiResponse linkParentToStudent(StudentParentRequestDto request) {
        User parent = userRepository.findById(request.getParentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + request.getParentUserId()));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));

        StudentParent link = StudentParent.builder()
                .student(student)
                .parentUser(parent)
                .relation(request.getRelation())
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        studentParentRepository.save(link);

        return new ApiResponse(true, "Parent linked to student successfully", null);
    }

    @Override
    public ApiResponse updateParent(Integer parentId, UserRequestDto request) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + parentId));

        parent.setUserName(request.getUserName());
        parent.setPassword(request.getPassword()); // TODO: encode password
        parent.setContactNumber(request.getContactNumber());
        parent.setUpdatedBy(request.getUpdatedBy());
        parent.setUpdatedDate(LocalDateTime.now());

        User updatedParent = userRepository.save(parent);

        return new ApiResponse(true, "Parent updated successfully", updatedParent);
    }

    @Override
    public ApiResponse getParentById(Integer parentId) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found with ID: " + parentId));

        return new ApiResponse(true, "Parent fetched successfully", parent);
    }

    @Override
    public ApiResponse getAllParents(Integer schoolId) {
        List<User> parents = studentParentRepository.findByStudent_School_SchoolId(schoolId).stream()
                .map(StudentParent::getParentUser)
                .distinct()
                .collect(Collectors.toList());

        return new ApiResponse(true, "Parents fetched successfully", parents);
    }
    @Override
    public ApiResponse getParentByUserId(Integer userId) {
        StudentParent sp = studentParentRepository.findByParentUser_uId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Parent not found for userId: " + userId));

        Map<String, Object> resp = Map.of(
            "studentParentId", sp.getStudentParentId(),
            "relation", sp.getRelation(),
            "parentUserId", sp.getParentUser().getUId(),
            "parentName", sp.getParentUser().getUserName(),
            "email", sp.getParentUser().getEmail(),
            "contactNumber", sp.getParentUser().getContactNumber(),
            "studentName", sp.getStudent().getFirstName() + " " + sp.getStudent().getLastName(),
            "className", sp.getStudent().getClassMaster(),
            "section", sp.getStudent().getSectionMaster()
        );

        return new ApiResponse(true, "Parent fetched successfully", resp);
    }
    
    @Override
    public ApiResponse getStudentByParentUserId(Integer userId) {
        StudentParent sp = studentParentRepository.findByParentUser_uId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("No student linked with this parent"));

        // ✅ सिर्फ Student return करेंगे
        Student student = sp.getStudent();

        return new ApiResponse(true, "Student fetched successfully", mapToResponse(student));
    }

    // Mapper for StudentResponseDto
    private StudentResponseDto mapToResponse(Student student) {
        return StudentResponseDto.builder()
                .studentId(student.getStudentId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .classId(student.getClassMaster() != null ? student.getClassMaster().getClassId() : null)
                .className(student.getClassMaster() != null ? student.getClassMaster().getClassName() : null)
                .sectionId(student.getSectionMaster() != null ? student.getSectionMaster().getSectionId() : null)
                .sectionName(student.getSectionMaster() != null ? student.getSectionMaster().getSectionName() : null)
                .gender(student.getGender())
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


}
