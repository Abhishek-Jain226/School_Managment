package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.SchoolVehicle;

public interface SchoolVehicleRepository extends JpaRepository<SchoolVehicle, Integer> {

	long countBySchool_SchoolId(Integer schoolId);

	List<SchoolVehicle> findByOwner_OwnerId(Integer ownerId);

}
