package com.app.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.DispatchLog;
import com.app.entity.Driver;
import com.app.entity.School;
import com.app.entity.Student;
import com.app.entity.Trip;
import com.app.entity.Vehicle;

public interface DispatchLogRepository extends JpaRepository<DispatchLog, Integer> {

	List<DispatchLog> findByTrip(Trip trip);

	List<DispatchLog> findByVehicle(Vehicle vehicle);

	List<DispatchLog> findBySchoolAndCreatedDateBetween(School school, LocalDateTime startDate, LocalDateTime endDate);

	List<DispatchLog> findByVehicle_VehicleIdAndCreatedDateBetween(Integer vehicleId, LocalDateTime startDate, LocalDateTime endDate);

	List<DispatchLog> findByVehicle_VehicleIdOrderByCreatedDateDesc(Integer vehicleId);

	List<DispatchLog> findByStudent(Student student);
	
	List<DispatchLog> findByTripAndVehicle(Trip trip, Vehicle vehicle);
	
	List<DispatchLog> findByTripAndStudent(Trip trip, Student student);

	List<DispatchLog> findByDriverOrderByCreatedDateDesc(Driver driver);

}
