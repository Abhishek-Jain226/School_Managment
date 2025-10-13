package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.app.entity.Trip;
import com.app.entity.TripStatus;

@Repository
public interface TripStatusRepository extends JpaRepository<TripStatus, Integer> {

    // Find latest status for a trip
    @Query("SELECT ts FROM TripStatus ts WHERE ts.trip = :trip ORDER BY ts.statusTime DESC")
    List<TripStatus> findByTripOrderByStatusTimeDesc(Trip trip);

    // Find latest status for a trip
    Optional<TripStatus> findTopByTripOrderByStatusTimeDesc(Trip trip);

    // Find all statuses for a trip
    List<TripStatus> findByTrip(Trip trip);

    // Find trips by status
    @Query("SELECT ts FROM TripStatus ts WHERE ts.status = :status ORDER BY ts.statusTime DESC")
    List<TripStatus> findByStatusOrderByStatusTimeDesc(TripStatus.TripStatusType status);

    // Find trip statuses by trip and status
    @Query("SELECT ts FROM TripStatus ts WHERE ts.trip = :trip AND ts.status = :status ORDER BY ts.statusTime DESC")
    List<TripStatus> findByTripAndStatusOrderByStatusTimeDesc(Trip trip, TripStatus.TripStatusType status);
}
