package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	//Optional<School> findByRoleName(String string);
	

    // Case-insensitive search
    Optional<Role> findByRoleNameIgnoreCase(String roleName);
    

    // Find role by role name (e.g. SUPER_ADMIN, ADMIN, DRIVER, PARENT)
    Optional<Role> findByRoleName(String roleName);

    // Check if role already exists
    boolean existsByRoleName(String roleName);

}
