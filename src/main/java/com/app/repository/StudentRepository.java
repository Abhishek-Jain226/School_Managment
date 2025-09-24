package com.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {

	boolean existsByEmail(String email);

	boolean existsByPrimaryContactNumber(String primaryContactNumber);

	List<Student> findBySchoolSchoolId(Integer schoolId);
	
	// ---- Custom Query to fetch Students by TripId ----
    @Query("SELECT ts.student FROM TripStudent ts WHERE ts.trip.tripId = :tripId")
    List<Student> findStudentsByTripId(@Param("tripId") Integer tripId);
    
    long countBySchool_SchoolId(Integer schoolId);
    
   

}
