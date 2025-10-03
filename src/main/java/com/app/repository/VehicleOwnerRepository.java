package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.User;
import com.app.entity.VehicleOwner;

public interface VehicleOwnerRepository extends JpaRepository<VehicleOwner, Integer> {
	
	Optional<VehicleOwner> findByUser_uId(Integer uId);

	Optional<VehicleOwner> findByUser(User user);
	
	boolean existsByUser(User user);
	
	boolean existsByEmail(String email);
	
	boolean existsByContactNumber(String contactNumber);
	
	Optional<VehicleOwner> findByEmail(String email);
	
	Optional<VehicleOwner> findByContactNumber(String contactNumber);

}
