package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.VehicleOwner;

public interface VehicleOwnerRepository extends JpaRepository<VehicleOwner, Integer> {
	
	Optional<VehicleOwner> findByUser_uId(Integer uId);

}
