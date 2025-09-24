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

            String html = "<p>Hello,</p>"
                    + "<p>Please activate your account by clicking the link below:</p>"
                    + "<p><a href=\"" + activationUrl + "\">" + activationUrl + "</a></p>"
                    + "<p>This link is valid for 24 hours.</p>";

            helper.setText(html, true);
            helper.setTo(to);
            helper.setSubject("Account Activation - Kids Vehicle Tracking");
            helper.setFrom("jainshab751@gmail.com");
            mailSender.send(msg);

            System.out.println("Activation link (debug): " + activationUrl);
        } catch (Exception ex) {
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

        // 2. Assign Role
        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(pending.getRole())
                .isActive(true)
                .createdBy("system")
                .createdDate(LocalDateTime.now())
                .build();
        userRoleRepository.save(userRole);

        // 3. Entity specific linking
        if ("SCHOOL".equalsIgnoreCase(pending.getEntityType())) {
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
            // link StudentParent
            StudentParent sp = studentParentRepository.findById(pending.getEntityId().intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("StudentParent mapping not found"));

            sp.setParentUser(savedUser);
            sp.setUpdatedBy("system");
            sp.setUpdatedDate(LocalDateTime.now());
            studentParentRepository.save(sp);
        }else if ("DRIVER".equalsIgnoreCase(pending.getEntityType())) {
            Driver driver = driverRepository.findById(pending.getEntityId().intValue())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver not found"));

            driver.setUser(savedUser);
            driver.setUpdatedBy("system");
            driver.setUpdatedDate(LocalDateTime.now());
            driverRepository.save(driver);
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

