package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.Role;
import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.User;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.LoginRequestDTO;
import com.app.payload.request.SchoolUserRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.UserResponseDto;
import com.app.repository.RoleRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolUserRepository;
import com.app.repository.UserRepository;
import com.app.service.ISchoolAdminService;



@Service
@Transactional
public class SchoolAdminServiceImpl implements ISchoolAdminService {
 
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private SchoolRepository schoolRepository;
	@Autowired
    private RoleRepository roleRepository;
	@Autowired
    private SchoolUserRepository schoolUserRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse createSuperAdmin(UserRequestDto request) {
        // Check duplicate username
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            return new ApiResponse(false, "Username already exists", null);
        }

        // Create User
        User user = User.builder()
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))  // plain password save (as per your requirement)
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);

        // Assign SUPER_ADMIN role
        Role role = roleRepository.findByRoleName("SUPER_ADMIN")
                .orElseThrow(() -> new ResourceNotFoundException("Role SUPER_ADMIN not found"));

        SchoolUser schoolUser = SchoolUser.builder()
                .user(saved)
                .role(role)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        schoolUserRepository.save(schoolUser);

        return new ApiResponse(true, "Super Admin created successfully", mapToResponse(saved));
    }

//    @Override
//    public ApiResponse login(LoginRequestDTO request) {
//        // Find user by userName
//        User user = userRepository.findByUserName(request.getUserName())
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + request.getUserName()));
//
//        // Compare plain passwords
//        if (!user.getPassword().equals(request.getPassword())) {
//            return new ApiResponse(false, "Invalid credentials", null);
//        }
//
//        // Check if user is active
//        if (!user.getIsActive()) {
//            return new ApiResponse(false, "User account is inactive", null);
//        }
//
//        return new ApiResponse(true, "Login successful", mapToResponse(user));
//    }


    @Override
    public ApiResponse updateProfile(Integer userId, UserRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setUserName(request.getUserName());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword()); // plain password update
        }
        user.setContactNumber(request.getContactNumber());
        user.setUpdatedBy(request.getUpdatedBy());
        user.setUpdatedDate(LocalDateTime.now());

        User updated = userRepository.save(user);

        return new ApiResponse(true, "Profile updated successfully", mapToResponse(updated));
    }

    @Override
    public ApiResponse assignStaffToSchool(SchoolUserRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));

        SchoolUser schoolUser = SchoolUser.builder()
                .user(user)
                .school(school)
                .role(role)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        schoolUserRepository.save(schoolUser);

        return new ApiResponse(true, "Staff assigned to school successfully", null);
    }

    @Override
    public ApiResponse getDashboardStats(Integer schoolId) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

        Map<String, Object> stats = new HashMap<>();
        stats.put("schoolName", school.getSchoolName());
        stats.put("totalUsers", schoolUserRepository.countBySchool(school));
        stats.put("activeUsers", schoolUserRepository.countBySchoolAndIsActive(school, true));
        stats.put("inactiveUsers", schoolUserRepository.countBySchoolAndIsActive(school, false));

        return new ApiResponse(true, "Dashboard stats fetched successfully", stats);
    }

    // ------------------ Private Mapper ------------------
    private UserResponseDto mapToResponse(User user) {
        return UserResponseDto.builder()
                .uId(user.getUId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .isActive(user.getIsActive())
                .createdBy(user.getCreatedBy())
                .createdDate(user.getCreatedDate())
                .updatedBy(user.getUpdatedBy())
                .updatedDate(user.getUpdatedDate())
                .build();
    }
}
