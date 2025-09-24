package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.School;

public interface SchoolRepository extends JpaRepository<School, Integer> {

	boolean existsByEmail(String email);

	boolean existsByRegistrationNumber(String registrationNumber);

	Optional<School> findByEmailAndRegistrationNumber(String userName, String password);

	Optional<School> findByCreatedBy(String valueOf);

	Optional<School> findBySchoolName(String schoolName);

}
