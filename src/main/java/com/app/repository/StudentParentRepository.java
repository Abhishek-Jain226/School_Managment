package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.StudentParent;
import com.app.entity.User;

public interface StudentParentRepository extends JpaRepository<StudentParent, Integer> {
	
	List<StudentParent> findByStudent_School_SchoolId(Integer schoolId);
	
	Optional<StudentParent> findByParentUser_uId(Integer uId);
	
	Optional<StudentParent> findByParentUser(User parentUser);

}
