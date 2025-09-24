package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.School;
import com.app.entity.Trip;

public interface TripRepository extends JpaRepository<Trip, Integer> {
	
	List<Trip> findBySchool(School school);

}
