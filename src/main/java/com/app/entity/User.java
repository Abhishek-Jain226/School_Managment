package com.app.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(
	    name = "user",
	    indexes = { 
	        @Index(name = "idx_email", columnList = "email"),
	        @Index(name = "idx_is_active", columnList = "is_active") // ✅ Correct
	    }
	)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "u_id")
	private Integer uId;

	@NotBlank(message = "User name is required")
	@Column(name = "user_name", nullable = false, unique = true, length = 50)
	private String userName;

//	@NotBlank(message = "Password is required")
	@Column(name = "password")
	private String password;

//	@Email(message = "Email should be valid")
//	@NotBlank(message = "Email is required")
//	@Column(name = "email", nullable = false, unique = true, length = 100)
	private String email;

	@NotBlank(message = "Contact number is required")
	@Size(min = 10, max = 15)
	@Column(name = "contact_number", nullable = false, length = 15)
	private String contactNumber;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "created_by", length = 50)
	private String createdBy;

	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate;

	@Column(name = "updated_by", length = 50)
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@PrePersist
	protected void onCreate() {
		createdDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedDate = LocalDateTime.now();
	}
}
