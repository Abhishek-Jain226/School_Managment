package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Role;
import com.app.entity.School;
import com.app.entity.SchoolUser;
import com.app.entity.User;

public interface SchoolUserRepository extends JpaRepository<SchoolUser, Integer> {
	
	boolean existsBySchoolAndUserAndRole(School school, User user, Role role);
	List<SchoolUser> findBySchool(School school);
	List<SchoolUser> findBySchool_SchoolId(Integer schoolId);
	List<SchoolUser> findAllByUser(User user);
	
	long countBySchool(School school);
    long countBySchoolAndIsActive(School school, boolean isActive);
	Optional<SchoolUser> findByUser(User user);

}
