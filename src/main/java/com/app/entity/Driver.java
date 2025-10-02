package com.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "driver_id")
	private Integer driverId;

	@OneToOne
	@JoinColumn(name = "u_id", unique = true)
	private User user;

	@NotBlank(message = "Driver name is required")
	@Column(name = "driver_name", nullable = false, length = 100)
	private String driverName;

	@Lob
	@Column(name = "driver_photo", columnDefinition = "LONGTEXT")
	private String driverPhoto;

	@NotBlank(message = "Driver contact number is required")
	@Size(min = 10, max = 15, message = "Driver contact number must be between 10 and 15 characters")
	@Column(name = "driver_contact_number", nullable = false, length = 15, unique = true)
	private String driverContactNumber;

	@NotBlank(message = "Driver address is required")
	@Column(name = "driver_address", nullable = false, length = 255)
	private String driverAddress;
	
	private String email;

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
		this.createdDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedDate = LocalDateTime.now();
	}

}
