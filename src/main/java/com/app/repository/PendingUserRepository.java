package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.PendingUser;
import com.app.entity.School;

public interface PendingUserRepository extends JpaRepository<PendingUser, Integer> {

	Optional<PendingUser> findByToken(String token);

	Optional<PendingUser> findByEmailAndIsUsedFalse(String email);

	Optional<PendingUser> findByContactNumberAndIsUsedFalse(String contactNumber);

	Optional<School> findByEntityTypeAndEntityId(String string, long longValue);

}
