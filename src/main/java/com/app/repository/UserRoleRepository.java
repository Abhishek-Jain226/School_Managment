package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Role;
import com.app.entity.User;
import com.app.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Integer> {

	boolean existsByUserAndRole(User user, Role role);
	
	boolean existsByUserAndRole_RoleName(User user, String roleName);

	  List<UserRole> findByUser(User user);

}
