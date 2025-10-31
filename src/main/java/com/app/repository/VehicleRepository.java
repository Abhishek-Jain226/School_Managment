package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

	boolean existsByRegistrationNumber(String registrationNumber);
	
	@Query("SELECT v FROM Vehicle v JOIN SchoolVehicle sv ON v.id = sv.vehicle.id WHERE sv.school.id = :schoolId AND sv.isActive = true")
	List<Vehicle> findBySchoolId(@Param("schoolId") Integer schoolId);
	
	List<Vehicle> findByCreatedBy(String createdBy);

	// Custom query to find vehicles by vehicle owner through createdBy field
	@Query("SELECT v FROM Vehicle v WHERE v.createdBy = :ownerName")
	List<Vehicle> findByVehicleOwner(@Param("ownerName") String ownerName);
}