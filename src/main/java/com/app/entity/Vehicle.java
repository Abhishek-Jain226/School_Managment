package com.app.entity;

import java.time.LocalDateTime;

import com.app.Enum.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vehicle_id")
	private Integer vehicleId;

	@NotBlank
	@Column(name = "vehicle_number", nullable = false, length = 10)
	private String vehicleNumber; // e.g. 28, 29, 30

	@NotBlank
	@Column(name = "registration_number", nullable = false, unique = true, length = 20)
	private String registrationNumber;

	@Lob
	@Column(name = "vehicle_photo", columnDefinition = "LONGTEXT")
	private String vehiclePhoto;
	
	@Enumerated(EnumType.STRING)
	private VehicleType vehicleType;
	
	

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
