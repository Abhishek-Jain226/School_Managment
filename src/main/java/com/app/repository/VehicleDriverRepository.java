package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Vehicle;
import com.app.entity.VehicleDriver;

public interface VehicleDriverRepository extends JpaRepository<VehicleDriver, Integer> {

	boolean existsByVehicleAndIsPrimaryTrue(Vehicle vehicle);

	List<VehicleDriver> findByVehicle(Vehicle vehicle);
	
	

}
