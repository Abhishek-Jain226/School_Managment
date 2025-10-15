package com.app.config;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.app.entity.Role;
import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.repository.RoleRepository;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Create default roles including APP_ADMIN
        List<String> defaultRoles = List.of("SUPER_ADMIN", "SCHOOL_ADMIN", "PARENT", "DRIVER", "VEHICLE_OWNER", "GATE_STAFF", "APP_ADMIN");

        for (String roleName : defaultRoles) {
            roleRepository.findByRoleName(roleName).orElseGet(() -> {
                Role role = Role.builder()
                        .roleName(roleName)
                        .description(roleName + " role")
                        .isActive(true)
                        .createdBy("system")
                        .createdDate(LocalDateTime.now())
                        .build();
                return roleRepository.save(role);
            });
        }

        // Clean up duplicate AppAdmin entries first
        cleanupDuplicateAppAdminUsers();
        
        // Create APP_ADMIN user if not exists
        createAppAdminUser();

        System.out.println("Default roles and APP_ADMIN user ensured in DB");
    }

    private void createAppAdminUser() {
        // AppAdmin user details - Change these values as needed
        String appAdminUserName = "rishabh Jain";
        String appAdminEmail = "rishabhmca2016@gmail.com";
        String appAdminMobile = "9999999999";
        String appAdminPassword = "123456";
        
        // Check if APP_ADMIN user already exists (check by old username first, then by new username)
        Optional<User> existingUserOpt = userRepository.findByUserName("appadmin");
        if (existingUserOpt.isEmpty()) {
            existingUserOpt = userRepository.findByUserName(appAdminUserName);
        }
        
        if (existingUserOpt.isPresent()) {
            // Update existing user
            User existingUser = existingUserOpt.get();
            boolean updated = false;
            
            // Update username if different
            if (!appAdminUserName.equals(existingUser.getUserName())) {
                existingUser.setUserName(appAdminUserName);
                updated = true;
                System.out.println("👤 Updated AppAdmin username to: " + appAdminUserName);
            }
            
            // Update email if different
            if (!appAdminEmail.equals(existingUser.getEmail())) {
                existingUser.setEmail(appAdminEmail);
                updated = true;
                System.out.println("📧 Updated AppAdmin email to: " + appAdminEmail);
            }
            
            // Update mobile number if different
            if (!appAdminMobile.equals(existingUser.getContactNumber())) {
                existingUser.setContactNumber(appAdminMobile);
                updated = true;
                System.out.println("📱 Updated AppAdmin mobile to: " + appAdminMobile);
            }
            
            // Update password if different (always update for security)
            String encodedPassword = passwordEncoder.encode(appAdminPassword);
            if (!passwordEncoder.matches(appAdminPassword, existingUser.getPassword())) {
                existingUser.setPassword(encodedPassword);
                updated = true;
                System.out.println("🔒 Updated AppAdmin password");
            }
            
            if (updated) {
                userRepository.save(existingUser);
                System.out.println("✅ AppAdmin user updated successfully!");
            } else {
                System.out.println("ℹ️ AppAdmin user already up to date!");
            }
            
        } else {
            // Create new APP_ADMIN user
            User appAdminUser = User.builder()
                    .userName(appAdminUserName)
                    .password(passwordEncoder.encode(appAdminPassword))
                    .email(appAdminEmail)
                    .contactNumber(appAdminMobile)
                    .isActive(true)
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .build();

            User savedUser = userRepository.save(appAdminUser);

            // Get APP_ADMIN role
            Role appAdminRole = roleRepository.findByRoleName("APP_ADMIN")
                    .orElseThrow(() -> new RuntimeException("APP_ADMIN role not found"));

            // Assign APP_ADMIN role to user
            UserRole userRole = UserRole.builder()
                    .user(savedUser)
                    .role(appAdminRole)
                    .isActive(true)
                    .createdBy("system")
                    .createdDate(LocalDateTime.now())
                    .build();

            userRoleRepository.save(userRole);

            System.out.println("✅ APP_ADMIN user created successfully!");
        }
        
        // Print current AppAdmin details
        System.out.println("📋 Current AppAdmin Details:");
        System.out.println("   Username: " + appAdminUserName);
        System.out.println("   Email: " + appAdminEmail);
        System.out.println("   Mobile: " + appAdminMobile);
        System.out.println("   Password: " + appAdminPassword);
    }
    
    private void cleanupDuplicateAppAdminUsers() {
        System.out.println("🧹 Cleaning up duplicate AppAdmin users...");
        
        // Get APP_ADMIN role
        Optional<Role> appAdminRoleOpt = roleRepository.findByRoleName("APP_ADMIN");
        if (appAdminRoleOpt.isEmpty()) {
            System.out.println("⚠️ APP_ADMIN role not found, skipping cleanup");
            return;
        }
        
        Role appAdminRole = appAdminRoleOpt.get();
        
        // Find all users with APP_ADMIN role
        List<UserRole> appAdminUserRoles = userRoleRepository.findByRole(appAdminRole);
        
        if (appAdminUserRoles.size() <= 1) {
            System.out.println("✅ No duplicate AppAdmin users found");
            return;
        }
        
        System.out.println("🔍 Found " + appAdminUserRoles.size() + " AppAdmin users, cleaning up...");
        
        // Keep the first one, delete the rest
        UserRole keepUserRole = appAdminUserRoles.get(0);
        User keepUser = keepUserRole.getUser();
        
        System.out.println("✅ Keeping AppAdmin user: " + keepUser.getUserName() + " (ID: " + keepUser.getUId() + ")");
        
        // Delete other AppAdmin users
        for (int i = 1; i < appAdminUserRoles.size(); i++) {
            UserRole deleteUserRole = appAdminUserRoles.get(i);
            User deleteUser = deleteUserRole.getUser();
            
            System.out.println("🗑️ Deleting duplicate AppAdmin user: " + deleteUser.getUserName() + " (ID: " + deleteUser.getUId() + ")");
            
            // Delete user role first
            userRoleRepository.delete(deleteUserRole);
            
            // Delete user
            userRepository.delete(deleteUser);
        }
        
        System.out.println("✅ Cleanup completed! Only one AppAdmin user remains.");
    }
    
}
