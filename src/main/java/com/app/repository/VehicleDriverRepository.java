package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.Vehicle;
import com.app.entity.School;
import com.app.entity.Driver;
import com.app.entity.VehicleDriver;

public interface VehicleDriverRepository extends JpaRepository<VehicleDriver, Integer> {

	boolean existsByVehicleAndIsPrimaryTrue(Vehicle vehicle);

	List<VehicleDriver> findByVehicle(Vehicle vehicle);

    @Query("SELECT COUNT(vd) > 0 FROM VehicleDriver vd WHERE vd.vehicle = :vehicle AND vd.driver = :driver AND vd.school = :school AND vd.isActive = true")
    boolean existsActiveAssignment(@Param("vehicle") Vehicle vehicle, @Param("driver") Driver driver, @Param("school") School school);
	
	List<VehicleDriver> findByDriverAndIsActiveTrue(Driver driver);
	
	List<VehicleDriver> findByVehicleAndIsActiveTrue(Vehicle vehicle);
	
	// Find VehicleDriver assignments by owner through vehicle relationship
	@Query("SELECT vd FROM VehicleDriver vd JOIN vd.vehicle v JOIN SchoolVehicle sv ON v.vehicleId = sv.vehicle.vehicleId WHERE sv.owner.ownerId = :ownerId")
	List<VehicleDriver> findByOwnerId(@Param("ownerId") Integer ownerId);

}
