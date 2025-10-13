	package com.app.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.Driver;
import com.app.entity.PendingUser;
import com.app.entity.Role;
import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.StudentParent;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.entity.VehicleOwner;
import com.app.exception.ResourceNotFoundException;
import com.app.payload.request.PendingUserRequestDTO;
import com.app.payload.response.ApiResponse;
import com.app.payload.response.PendingUserResponseDTO;
import com.app.repository.DriverRepository;
import com.app.repository.PendingUserRepository;
import com.app.repository.RoleRepository;
import com.app.repository.SchoolRepository;
import com.app.repository.SchoolUserRepository;
import com.app.repository.StudentParentRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import com.app.repository.VehicleOwnerRepository;
import com.app.service.IPendingUserService;

import jakarta.mail.internet.MimeMessage;


@Service
@Transactional
public class PendingUserServiceImpl implements IPendingUserService {

	@Autowired
    private PendingUserRepository pendingUserRepository;
	@Autowired
    private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserRoleRepository userRoleRepository;
	@Autowired
	private SchoolRepository schoolRepository;
	@Autowired
	private SchoolUserRepository schoolUserRepository;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private StudentParentRepository studentParentRepository;
	
	@Autowired
	private DriverRepository driverRepository;
	
	@Autowired
	private VehicleOwnerRepository vehicleOwnerRepository;
	
	@Value("${app.frontend.activation-url}")
	private String activationBaseUrl;
	

    // ---------------- Create ----------------
    @Override
    public ApiResponse createPendingUser(PendingUserRequestDTO request) {
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + request.getRoleId()));

        PendingUser pendingUser = PendingUser.builder()
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .email(request.getEmail())
                .contactNumber(request.getContactNumber())
                .role(role)
                .token(UUID.randomUUID().toString()) // random unique token
                .tokenExpiry(LocalDateTime.now().plusDays(1)) // 1 day validity
                .isUsed(false)
                .isActive(true)
                .createdBy(request.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        PendingUser saved = pendingUserRepository.save(pendingUser);
        // Build Activation Link
        String activationUrl = activationBaseUrl + "?token=" + saved.getToken();
        sendActivationEmail(saved.getEmail(), activationUrl);

        return new ApiResponse(true, "Pending user created. Activation link sent to email.",
                Map.of("pendingUserId", saved.getPendingUserId(), "activationLink", activationUrl));
    }

    private void sendActivationEmail(String to, String activationUrl) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);

            String html = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background-color: #1E3A8A; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                        <h2 style="margin: 0;">Welcome to Kids Vehicle Tracking System</h2>
                    </div>
                    <div style="background-color: #f8f9fa; padding: 30px; border-radius: 0 0 8px 8px;">
                        <p style="font-size: 16px; color: #333;">Thank you for registering your school with us!</p>
                        <p style="color: #666;">To complete your registration and activate your school admin account, please click the button below:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s" style="background-color: #1E3A8A; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block; font-weight: bold;">Activate Account</a>
                        </div>
                        <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <p style="margin: 0; color: #856404;"><strong>Important:</strong> This link is valid for 24 hours only.</p>
                        </div>
                        <p style="color: #666; font-size: 14px;">If you didn't request this registration, please ignore this email.</p>
                        <hr style="margin: 30px 0; border: none; border-top: 1px solid #dee2e6;">
                        <p style="color: #6c757d; font-size: 12px; text-align: center; margin: 0;">Kids Vehicle Tracking System - School Management</p>
                    </div>
                </div>
                """.formatted(activationUrl);

            helper.setText(html, true);
            helper.setTo(to);
            helper.setSubject("Activate Your School Account - Kids Vehicle Tracking");
            helper.setFrom("noreply@kidsvt.com");
            mailSender.send(msg);

            System.out.println("Activation email sent successfully to: " + to);
        } catch (Exception ex) {
            System.err.println("Failed to send activation email to: " + to);
            ex.printStackTrace();
        }
    }

 // -------- VERIFY PendingUser Token --------
    @Override
    public ApiResponse verifyPendingUser(String token) {
        PendingUser pending = pendingUserRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (pending.getIsUsed())
            return new ApiResponse(false, "Token already used", null);

        if (pending.getTokenExpiry().isBefore(LocalDateTime.now()))
            return new ApiResponse(false, "Token expired", null);

        return new ApiResponse(true, "Token valid",
                Map.of("email", pending.getEmail(), "entityType", pending.getEntityType(), "entityId", pending.getEntityId()));
    }

    // ---------------- Get By Id ----------------
    @Override
    public ApiResponse getPendingUserById(Integer pendingUserId) {
        PendingUser pendingUser = pendingUserRepository.findById(pendingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Pending user not found with ID: " + pendingUserId));

        return new ApiResponse(true, "Pending user fetched successfully", mapToResponse(pendingUser));
    }

    // ---------------- Get All ----------------
    @Override
    public ApiResponse getAllPendingUsers() {
        List<PendingUserResponseDTO> users = pendingUserRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse(true, "All pending users fetched successfully", users);
    }

    // ---------------- Delete ----------------
    @Override
    public ApiResponse deletePendingUser(Integer pendingUserId) {
        PendingUser pendingUser = pendingUserRepository.findById(pendingUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Pending user not found with ID: " + pendingUserId));

        pendingUserRepository.delete(pendingUser);

        return new ApiResponse(true, "Pending user deleted successfully", null);
    }

    // ---------------- Mapper ----------------
    private PendingUserResponseDTO mapToResponse(PendingUser pendingUser) {
        return PendingUserResponseDTO.builder()
                .pendingUserId(pendingUser.getPendingUserId())
                .entityType(pendingUser.getEntityType())
                .entityId(pendingUser.getEntityId())
                .email(pendingUser.getEmail())
                .contactNumber(pendingUser.getContactNumber())
                .roleId(pendingUser.getRole().getRoleId())
                .roleName(pendingUser.getRole().getRoleName())
                .token(pendingUser.getToken())
                .tokenExpiry(pendingUser.getTokenExpiry())
                .isUsed(pendingUser.getIsUsed())
                .isActive(pendingUser.getIsActive())
                .createdBy(pendingUser.getCreatedBy())
                .createdDate(pendingUser.getCreatedDate())
                .updatedBy(pendingUser.getUpdatedBy())
                .updatedDate(pendingUser.getUpdatedDate())
                .build();
    }

 // -------- COMPLETE Registration --------
    @Override
    public ApiResponse completeRegistration(String token, String rawPassword, String userName) {
        PendingUser pending = pendingUserRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid token"));

        if (pending.getIsUsed())
            return new ApiResponse(false, "Token already used", null);

        if (pending.getTokenExpiry().isBefore(LocalDateTime.now()))
            return new ApiResponse(false, "Token expired", null);

        // 1. Create User
        User user = User.builder()
                .userName(userName != null && !userName.isBlank() ? userName : pending.getEmail().split("@")[0])
                .password(passwordEncoder.encode(rawPassword))
                .email(pending.getEmail())
                .contactNumber(pending.getContactNumber())
                .isActive(true)
                .createdBy(pending.getCreatedBy())
                .createdDate(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        // 2. Assign Role (will be done in entity-specific sections)
        // UserRole creation moved to entity-specific sections to avoid duplicates

        // 3. Entity specific linking
        if ("SCHOOL".equalsIgnoreCase(pending.getEntityType())) {
            // Create UserRole for SCHOOL entity
            UserRole userRole = UserRole.builder()
                    .user(savedUser)
                    .role(pending.getRole())
                    .isActive(true)
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .build();
            userRoleRepository.save(userRole);
            
            // link SchoolUser
            School school = schoolRepository.findById(pending.getEntityId().intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("School not found"));
            SchoolUser schoolUser = SchoolUser.builder()
                    .school(school)
                    .user(savedUser)
                    .role(pending.getRole())
                    .isActive(true)
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .build();
            schoolUserRepository.save(schoolUser);

        } else if ("PARENT".equalsIgnoreCase(pending.getEntityType())) {
            // Create UserRole for PARENT entity
            UserRole userRole = UserRole.builder()
                    .user(savedUser)
                    .role(pending.getRole())
                    .isActive(true)
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .build();
            userRoleRepository.save(userRole);
            
            // link StudentParent
            StudentParent sp = studentParentRepository.findById(pending.getEntityId().intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("StudentParent mapping not found"));

            sp.setParentUser(savedUser);
            sp.setUpdatedBy("system");
            sp.setUpdatedDate(LocalDateTime.now());
            studentParentRepository.save(sp);
            
            // ✅ Create SchoolUser entry for student when parent activates
            // Get student's school from StudentParent relationship
            School studentSchool = sp.getStudent().getSchool();
            
            // Get STUDENT role (if exists) or use PARENT role as fallback
            Role studentRole = roleRepository.findByRoleName("STUDENT")
                    .orElse(pending.getRole()); // Fallback to PARENT role if STUDENT role doesn't exist
            
            // Create SchoolUser entry for student
            SchoolUser studentSchoolUser = SchoolUser.builder()
                    .user(savedUser) // Link to parent's User account (student will use parent's account)
                    .school(studentSchool)
                    .role(studentRole)
                    .isActive(true)
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .build();
            
            schoolUserRepository.save(studentSchoolUser);
        }
        
        else if ("VEHICLE_OWNER".equalsIgnoreCase(pending.getEntityType())) {
            // Create UserRole for VEHICLE_OWNER entity
            UserRole userRole = UserRole.builder()
                    .user(savedUser)
                    .role(pending.getRole())
                    .isActive(true)
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .build();
            userRoleRepository.save(userRole);
            
            VehicleOwner owner = vehicleOwnerRepository.findById(pending.getEntityId().intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("VehicleOwner not found"));

            owner.setUser(savedUser);
            owner.setUpdatedBy("system");
            owner.setUpdatedDate(LocalDateTime.now());
            vehicleOwnerRepository.save(owner);

            // ✅ School detect from PendingUser.createdBy (admin)
            User adminUser = userRepository.findByUserName(pending.getCreatedBy())
                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

            SchoolUser adminSchoolUser = schoolUserRepository.findByUser(adminUser)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin is not mapped to any school"));

            School school = adminSchoolUser.getSchool();

            // ✅ VehicleOwner ko bhi school ke sath map karo
            SchoolUser schoolUser = SchoolUser.builder()
                    .school(school)
                    .user(savedUser)
                    .role(pending.getRole())
                    .isActive(true)
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .build();
            schoolUserRepository.save(schoolUser);
        }
//        }else if ("VEHICLE_OWNER".equalsIgnoreCase(pending.getEntityType())) {
//            VehicleOwner owner = vehicleOwnerRepository.findById(pending.getEntityId().intValue())
//                    .orElseThrow(() -> new ResourceNotFoundException("VehicleOwner not found"));
//
//            owner.setUser(savedUser);   // 
//            owner.setUpdatedBy("system");
//            owner.setUpdatedDate(LocalDateTime.now());
//            vehicleOwnerRepository.save(owner);
//        }
        else if ("DRIVER".equalsIgnoreCase(pending.getEntityType())) {
            Driver driver = driverRepository.findById(pending.getEntityId().intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

            driver.setUser(savedUser);
            driver.setUpdatedBy("system");
            driver.setUpdatedDate(LocalDateTime.now());
            driverRepository.save(driver);
            
            // ✅ Create UserRole entry for DRIVER role
            UserRole driverUserRole = UserRole.builder()
                    .user(savedUser)
                    .role(pending.getRole()) // DRIVER role (ID = 4)
                    .isActive(true)
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .build();
            
            userRoleRepository.save(driverUserRole);
        }

        // 4. Mark token used
        pending.setIsUsed(true);
        pending.setUpdatedBy("system");
        pending.setUpdatedDate(LocalDateTime.now());
        pendingUserRepository.save(pending);

        // 5. Return response
        return new ApiResponse(true, "Registration completed. You can now login.",
                Map.of("userId", savedUser.getUId(), "email", savedUser.getEmail(), "role", pending.getRole().getRoleName()));
    }


 
}

