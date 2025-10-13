package com.app.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.Driver;
import com.app.entity.User;
import com.app.entity.Vehicle;

public interface DriverRepository extends JpaRepository<Driver, Integer> {

	boolean existsByDriverContactNumber(String driverContactNumber);
	
	Optional<Driver> findByUser(User user);
    boolean existsByUser(User user);
    
    // Find drivers by createdBy field
    List<Driver> findByCreatedBy(String createdBy);
    
    // Find drivers with user credentials (activated drivers)
    @Query("SELECT d FROM Driver d WHERE d.user IS NOT NULL AND d.isActive = true")
    List<Driver> findActivatedDrivers();
    
    // Find drivers with user credentials by createdBy
    @Query("SELECT d FROM Driver d WHERE d.user IS NOT NULL AND d.isActive = true AND d.createdBy = :createdBy")
    List<Driver> findActivatedDriversByCreatedBy(@Param("createdBy") String createdBy);
    
    // Find driver by vehicle through VehicleDriver relationship
    @Query("SELECT d FROM Driver d JOIN VehicleDriver vd ON d.driverId = vd.driver.driverId WHERE vd.vehicle = :vehicle AND vd.isActive = true")
    Optional<Driver> findByVehicle(@Param("vehicle") Vehicle vehicle);
    
    // Find all drivers assigned to a vehicle
    @Query("SELECT d FROM Driver d JOIN VehicleDriver vd ON d.driverId = vd.driver.driverId WHERE vd.vehicle = :vehicle AND vd.isActive = true")
    List<Driver> findAllByVehicle(@Param("vehicle") Vehicle vehicle);
}
