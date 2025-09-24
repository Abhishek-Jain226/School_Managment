package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
	
	 Optional<PasswordResetToken> findByLoginIdAndOtpAndUsedFalse(String loginId, String otp);

}
