package com.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.entity.SectionMaster;
import com.app.entity.School;

@Repository
public interface SectionMasterRepository extends JpaRepository<SectionMaster, Integer> {

    // Find all active sections for a specific school ordered by section name
    @Query("SELECT s FROM SectionMaster s WHERE s.school = :school AND s.isActive = true ORDER BY s.sectionName ASC")
    List<SectionMaster> findAllActiveBySchoolOrderBySectionName(School school);

    // Find all sections for a specific school ordered by section name
    @Query("SELECT s FROM SectionMaster s WHERE s.school = :school ORDER BY s.sectionName ASC")
    List<SectionMaster> findAllBySchoolOrderBySectionName(School school);


    // Check if section name exists for a specific school (excluding current section)
    @Query("SELECT COUNT(s) > 0 FROM SectionMaster s WHERE s.sectionName = :sectionName AND s.school = :school AND s.sectionId != :sectionId")
    boolean existsBySectionNameAndSchoolAndSectionIdNot(String sectionName, School school, Integer sectionId);

    // Check if section name exists for a specific school
    @Query("SELECT COUNT(s) > 0 FROM SectionMaster s WHERE s.sectionName = :sectionName AND s.school = :school")
    boolean existsBySectionNameAndSchool(String sectionName, School school);

    // Find by section name and school
    Optional<SectionMaster> findBySectionNameAndSchool(String sectionName, School school);
    
    // Find by section ID and school ID
    @Query("SELECT s FROM SectionMaster s WHERE s.sectionId = :sectionId AND s.school.schoolId = :schoolId")
    SectionMaster findBySectionIdAndSchoolSchoolId(@Param("sectionId") Integer sectionId, @Param("schoolId") Integer schoolId);

	//boolean existsBySectionNameAndSectionIdNot(String sectionName, Integer sectionId);

	//boolean existsBySectionName(String sectionName);

}
