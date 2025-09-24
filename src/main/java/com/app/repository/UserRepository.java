package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByEmailAndPassword(String userName, String password);

	// Find by username
	Optional<User> findByUserName(String userName);

	// Find by email
	Optional<User> findByEmail(String email);
	
	 Optional<User> findByContactNumber(String contactNumber);
	 
	 Optional<User> findByUserNameOrContactNumber(String userName, String contactNumber);

	// Check duplicate email
	boolean existsByEmail(String email);

	// Check duplicate username
	boolean existsByUserName(String userName);
	
	Optional<User> findByUserNameOrContactNumberOrEmail(String userName, String contactNumber, String email);

	List<User> findAllByEmail(String email);
	
	



}
