package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.DispatchLog;
import com.app.entity.Trip;
import com.app.entity.Vehicle;

public interface DispatchLogRepository extends JpaRepository<DispatchLog, Integer> {

	List<DispatchLog> findByTrip(Trip trip);

	List<DispatchLog> findByVehicle(Vehicle vehicle);

}
