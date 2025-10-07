package com.app.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.app.entity.Driver;
import com.app.entity.School;
import com.app.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Integer> {
	
	List<Trip> findBySchool(School school);

	// Find trips assigned to a driver
	List<Trip> findByDriver(Driver driver);

	// Find trips assigned to a driver for today
	@Query("SELECT t FROM Trip t WHERE t.driver = :driver AND DATE(t.createdDate) = :date")
	List<Trip> findByDriverAndDate(Driver driver, LocalDate date);

	// Find active trips for a driver
	List<Trip> findByDriverAndIsActiveTrue(Driver driver);

	// Find trip by driver and trip ID
	Optional<Trip> findByDriverAndTripId(Driver driver, Integer tripId);

	// Find trips by status
	List<Trip> findByTripStatus(String tripStatus);

	// Find trips by driver and status
	List<Trip> findByDriverAndTripStatus(Driver driver, String tripStatus);

}
