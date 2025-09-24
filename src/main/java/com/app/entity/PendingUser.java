package com.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pending_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pending_user_id")
	private Integer pendingUserId;

	@Column(name = "entity_type", nullable = false, length = 50)
	private String entityType; // SCHOOL, VEHICLE_OWNER, DRIVER, PARENT

	@Column(name = "entity_id", nullable = false)
	private Long entityId; // foreign key reference (school_id, owner_id, etc.)

	@Column(name = "email", length = 150)
	private String email;

	@Column(name = "contact_number", length = 20)
	private String contactNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Column(name = "token", nullable = false, unique = true, length = 255)
	private String token;

	@Column(name = "token_expiry", nullable = false)
	private LocalDateTime tokenExpiry;

	@Column(name = "is_used", nullable = false)
	private Boolean isUsed = false;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive = true;

	@Column(name = "created_by", length = 50)
	private String createdBy;

	@Column(name = "created_date")
	private LocalDateTime createdDate;

	@Column(name = "updated_by", length = 50)
	private String updatedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

}
