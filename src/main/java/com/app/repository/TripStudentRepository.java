package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Trip;
import com.app.entity.TripStudent;

public interface TripStudentRepository extends JpaRepository<TripStudent, Integer> {
	
	List<TripStudent> findByTrip(Trip trip);
	
	int countByTrip(Trip trip);
	
	int countByTripTripIdIn(List<Integer> tripIds);

}
