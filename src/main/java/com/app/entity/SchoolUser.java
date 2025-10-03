package com.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "school_user", uniqueConstraints = {
    @UniqueConstraint(name = "uk_school_user", columnNames = {"school_id", "u_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer schoolUserId;

	@ManyToOne
	@JoinColumn(name = "school_id", nullable = false)
	private School school;

	@ManyToOne
	@JoinColumn(name = "u_id", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

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
