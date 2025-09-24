package com.app.config;

import com.app.entity.Role;
import com.app.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        List<String> defaultRoles = List.of("SUPER_ADMIN", "SCHOOL_ADMIN", "PARENT", "DRIVER", "VEHICLE_OWNER", "GATE_STAFF");

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

        System.out.println("Default roles ensured in DB");
    }
}
