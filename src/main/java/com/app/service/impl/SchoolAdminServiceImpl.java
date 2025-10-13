package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.Role;
import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.LoginRequestDTO;
import com.app.payload.request.SchoolUserRequestDto;
import com.app.payload.request.StaffCreateRequestDto;
import com.app.payload.request.UserRequestDto;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.UserResponseDto;
import com.app.repository.RoleRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolUserRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
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
    private UserRoleRepository userRoleRepository;
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

        // Create UserRole mapping (user -> role)
        UserRole userRole = UserRole.builder()
                .user(saved)
                .role(role)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();
        
        userRoleRepository.save(userRole);

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

        // Create UserRole mapping (user -> role)
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();
        
        userRoleRepository.save(userRole);

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

    public ApiResponse createStaffAndAssign(StaffCreateRequestDto request) {
        // 1) username uniqueness check
        if (userRepository.findByUserName(request.getUserName()).isPresent()) {
            return new ApiResponse(false, "Username already exists", null);
        }

        // 2) load School
        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + request.getSchoolId()));

        // 3) load Role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));

//        Role role = roleRepository.findByRoleName("GATE_STAFF")
//                .orElseThrow(() -> new ResourceNotFoundException("Role OWNER not found"));
        // 4) create User (encode password)
        User user = User.builder()
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);

        // 5) create UserRole mapping (user -> role)
        UserRole userRole = UserRole.builder()
                .user(saved)
                .role(role)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();
        
        userRoleRepository.save(userRole);

        // 6) create SchoolUser mapping (user -> school -> role)
        SchoolUser schoolUser = SchoolUser.builder()
                .user(saved)
                .school(school)
                .role(role)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        schoolUserRepository.save(schoolUser);

        // 7) return created user summary
        return new ApiResponse(true, "Staff created and assigned successfully", mapToResponse(saved));
    }

    @Override
    public ApiResponse getAllStaffBySchool(Integer schoolId) {
        try {
            // Validate school exists
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            // Get all staff for this school (only TEACHER and GATE_STAFF, exclude PARENT)
            List<Map<String, Object>> staffList = schoolUserRepository.findBySchool_SchoolId(schoolId)
                    .stream()
                    .filter(schoolUser -> {
                        String roleName = schoolUser.getRole().getRoleName();
                        return "TEACHER".equals(roleName) || "GATE_STAFF".equals(roleName);
                    })
                    .map(this::mapToStaffResponse)
                    .collect(Collectors.toList());
            
            // Debug: Print all staff data (only TEACHER and GATE_STAFF)
            System.out.println("=== STAFF DATA DEBUG (TEACHER & GATE_STAFF ONLY) ===");
            for (Map<String, Object> staff : staffList) {
                System.out.println("Name: " + staff.get("name") + ", Role: " + staff.get("role") + ", Email: " + staff.get("email"));
            }
            System.out.println("=== END DEBUG ===");

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("staffList", staffList);
            responseData.put("totalCount", staffList.size());
            responseData.put("activeCount", staffList.stream().mapToInt(s -> (Boolean) s.get("isActive") ? 1 : 0).sum());

            return new ApiResponse(true, "Staff list retrieved successfully", responseData);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error retrieving staff list: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse updateStaffStatus(Integer staffId, Boolean isActive, String updatedBy) {
        try {
            SchoolUser schoolUser = schoolUserRepository.findById(staffId)
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with ID: " + staffId));

            // Update UserRole table
            List<UserRole> userRoles = userRoleRepository.findByUser(schoolUser.getUser());
            for (UserRole userRole : userRoles) {
                if (userRole.getRole().getRoleId().equals(schoolUser.getRole().getRoleId())) {
                    userRole.setIsActive(isActive);
                    userRole.setUpdatedBy(updatedBy);
                    userRole.setUpdatedDate(LocalDateTime.now());
                    userRoleRepository.save(userRole);
                    break;
                }
            }

            schoolUser.setIsActive(isActive);
            schoolUser.setUpdatedBy(updatedBy);
            schoolUser.setUpdatedDate(LocalDateTime.now());
            
            schoolUserRepository.save(schoolUser);

            return new ApiResponse(true, 
                "Staff " + (isActive ? "activated" : "deactivated") + " successfully", 
                mapToStaffResponse(schoolUser));
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error updating staff status: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse deleteStaff(Integer staffId, String updatedBy) {
        try {
            SchoolUser schoolUser = schoolUserRepository.findById(staffId)
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with ID: " + staffId));

            // Update UserRole table
            List<UserRole> userRoles = userRoleRepository.findByUser(schoolUser.getUser());
            for (UserRole userRole : userRoles) {
                if (userRole.getRole().getRoleId().equals(schoolUser.getRole().getRoleId())) {
                    userRole.setIsActive(false);
                    userRole.setUpdatedBy(updatedBy);
                    userRole.setUpdatedDate(LocalDateTime.now());
                    userRoleRepository.save(userRole);
                    break;
                }
            }

            // Soft delete - mark as inactive instead of hard delete
            schoolUser.setIsActive(false);
            schoolUser.setUpdatedBy(updatedBy);
            schoolUser.setUpdatedDate(LocalDateTime.now());
            
            schoolUserRepository.save(schoolUser);

            return new ApiResponse(true, "Staff deleted successfully", null);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error deleting staff: " + e.getMessage(), null);
        }
    }

    private Map<String, Object> mapToStaffResponse(SchoolUser schoolUser) {
        Map<String, Object> response = new HashMap<>();
        response.put("staffId", schoolUser.getSchoolUserId());
        response.put("userId", schoolUser.getUser().getUId());
        response.put("name", schoolUser.getUser().getUserName()); // Using userName as name since fullName doesn't exist
        response.put("userName", schoolUser.getUser().getUserName());
        response.put("email", schoolUser.getUser().getEmail());
        response.put("contactNo", schoolUser.getUser().getContactNumber());
        response.put("role", schoolUser.getRole().getRoleName());
        response.put("isActive", schoolUser.getIsActive());
        response.put("joinDate", schoolUser.getCreatedDate());
        response.put("schoolId", schoolUser.getSchool().getSchoolId());
        response.put("schoolName", schoolUser.getSchool().getSchoolName());
        return response;
    }

    @Override
    public ApiResponse getStaffByName(Integer schoolId, String name) {
        try {
            // Validate school exists
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            // Get staff by name for this school
            List<SchoolUser> staffList = schoolUserRepository.findBySchool_SchoolId(schoolId)
                    .stream()
                    .filter(schoolUser -> schoolUser.getUser().getUserName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
            
            List<Map<String, Object>> responseList = staffList.stream()
                    .map(this::mapToStaffResponse)
                    .collect(Collectors.toList());
            
            // Debug: Print detailed info
            System.out.println("=== DEBUG STAFF BY NAME: " + name + " ===");
            for (SchoolUser schoolUser : staffList) {
                System.out.println("User ID: " + schoolUser.getUser().getUId());
                System.out.println("User Name: " + schoolUser.getUser().getUserName());
                System.out.println("Role ID: " + schoolUser.getRole().getRoleId());
                System.out.println("Role Name: " + schoolUser.getRole().getRoleName());
                System.out.println("School ID: " + schoolUser.getSchool().getSchoolId());
                System.out.println("---");
            }
            System.out.println("=== END DEBUG ===");

            return new ApiResponse(true, "Staff found by name", responseList);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error finding staff by name: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse updateStaffRole(Integer staffId, Integer newRoleId, String updatedBy) {
        try {
            // Find the staff record
            SchoolUser schoolUser = schoolUserRepository.findById(staffId)
                    .orElseThrow(() -> new ResourceNotFoundException("Staff not found with ID: " + staffId));

            // Find the new role
            Role newRole = roleRepository.findById(newRoleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + newRoleId));

            // Update UserRole table
            List<UserRole> userRoles = userRoleRepository.findByUser(schoolUser.getUser());
            for (UserRole userRole : userRoles) {
                if (userRole.getRole().getRoleId().equals(schoolUser.getRole().getRoleId())) {
                    userRole.setRole(newRole);
                    userRole.setUpdatedBy(updatedBy);
                    userRole.setUpdatedDate(LocalDateTime.now());
                    userRoleRepository.save(userRole);
                    break;
                }
            }

            // Update the role in SchoolUser
            schoolUser.setRole(newRole);
            schoolUser.setUpdatedBy(updatedBy);
            schoolUser.setUpdatedDate(LocalDateTime.now());
            
            schoolUserRepository.save(schoolUser);

            return new ApiResponse(true, 
                "Staff role updated from " + schoolUser.getRole().getRoleName() + " to " + newRole.getRoleName() + " successfully", 
                mapToStaffResponse(schoolUser));
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error updating staff role: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse getAllUsersBySchool(Integer schoolId) {
        try {
            // Validate school exists
            School school = schoolRepository.findById(schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("School not found with ID: " + schoolId));

            // Get ALL users for this school (including PARENT)
            List<Map<String, Object>> allUsersList = schoolUserRepository.findBySchool_SchoolId(schoolId)
                    .stream()
                    .map(this::mapToStaffResponse)
                    .collect(Collectors.toList());
            
            // Debug: Print all users data (including PARENT)
            System.out.println("=== ALL USERS DATA DEBUG (INCLUDING PARENT) ===");
            for (Map<String, Object> user : allUsersList) {
                System.out.println("Name: " + user.get("name") + ", Role: " + user.get("role") + ", Email: " + user.get("email"));
            }
            System.out.println("=== END DEBUG ===");

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("allUsersList", allUsersList);
            responseData.put("totalCount", allUsersList.size());
            responseData.put("activeCount", allUsersList.stream().mapToInt(s -> (Boolean) s.get("isActive") ? 1 : 0).sum());

            return new ApiResponse(true, "All users fetched successfully", responseData);
            
        } catch (Exception e) {
            return new ApiResponse(false, "Error fetching all users: " + e.getMessage(), null);
        }
    }
    
}
