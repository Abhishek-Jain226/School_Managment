package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.SchoolVehicle;
import com.app.entity.Vehicle;
import com.app.entity.School;

public interface SchoolVehicleRepository extends JpaRepository<SchoolVehicle, Integer> {

	long countBySchool_SchoolId(Integer schoolId);

//	@Query("SELECT sv.vehicle FROM SchoolVehicle sv WHERE sv.owner.ownerId = :ownerId AND sv.isActive = true")
//	List<Vehicle> findVehiclesByOwnerId(@Param("ownerId") Integer ownerId);
	
	List<SchoolVehicle> findByOwner_OwnerId(Integer ownerId);

    boolean existsBySchoolAndVehicle(School school, Vehicle vehicle);
    
    // Check if mapping exists by school and vehicle IDs
    boolean existsBySchool_SchoolIdAndVehicle_VehicleId(Integer schoolId, Integer vehicleId);
    
    // Find mapping by school and vehicle IDs
    java.util.Optional<SchoolVehicle> findBySchool_SchoolIdAndVehicle_VehicleId(Integer schoolId, Integer vehicleId);
    
    // Find vehicles by owner and school
    List<SchoolVehicle> findByOwner_OwnerIdAndSchool_SchoolId(Integer ownerId, Integer schoolId);
    
    // Find mapping by vehicle and school IDs
    java.util.Optional<SchoolVehicle> findByVehicle_VehicleIdAndSchool_SchoolId(Integer vehicleId, Integer schoolId);

}
