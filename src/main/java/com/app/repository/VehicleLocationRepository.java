package com.app.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Trip;
import com.app.entity.VehicleLocation;

public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, Integer> {

    /**
     * Find the most recent location for a trip
     */
    Optional<VehicleLocation> findTopByTripOrderByCreatedDateDesc(Trip trip);

    /**
     * Find all locations for a trip, ordered by most recent first
     */
    List<VehicleLocation> findByTripOrderByCreatedDateDesc(Trip trip);

    /**
     * Find all locations for a trip within a date range
     */
    List<VehicleLocation> findByTripAndCreatedDateBetween(Trip trip, LocalDateTime startDate, LocalDateTime endDate);
}
