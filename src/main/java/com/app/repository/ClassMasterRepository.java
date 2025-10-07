package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entity.ClassMaster;
import com.app.entity.School;

@Repository
public interface ClassMasterRepository extends JpaRepository<ClassMaster, Integer> {

    // Find all active classes for a specific school ordered by class order
    @Query("SELECT c FROM ClassMaster c WHERE c.school = :school AND c.isActive = true ORDER BY c.classOrder ASC")
    List<ClassMaster> findAllActiveBySchoolOrderByClassOrder(School school);

    // Find all classes for a specific school ordered by class order
    @Query("SELECT c FROM ClassMaster c WHERE c.school = :school ORDER BY c.classOrder ASC")
    List<ClassMaster> findAllBySchoolOrderByClassOrder(School school);

    // Check if class name exists for a specific school (excluding current class)
    @Query("SELECT COUNT(c) > 0 FROM ClassMaster c WHERE c.className = :className AND c.school = :school AND c.classId != :classId")
    boolean existsByClassNameAndSchoolAndClassIdNot(String className, School school, Integer classId);

    // Check if class name exists for a specific school
    @Query("SELECT COUNT(c) > 0 FROM ClassMaster c WHERE c.className = :className AND c.school = :school")
    boolean existsByClassNameAndSchool(String className, School school);

    // Find by class name and school
    Optional<ClassMaster> findByClassNameAndSchool(String className, School school);

    // Find by class order and school
    Optional<ClassMaster> findByClassOrderAndSchool(Integer classOrder, School school);
    
    // Find by class ID and school ID
    @Query("SELECT c FROM ClassMaster c WHERE c.classId = :classId AND c.school.schoolId = :schoolId")
    ClassMaster findByClassIdAndSchoolSchoolId(@Param("classId") Integer classId, @Param("schoolId") Integer schoolId);
}
