package com.app.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Driver;
import com.app.entity.User;

public interface DriverRepository extends JpaRepository<Driver, Integer> {

	boolean existsByDriverContactNumber(String driverContactNumber);
	
	Optional<Driver> findByUser(User user);
    boolean existsByUser(User user);
}
