package com.app.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "trips", uniqueConstraints = {
    @UniqueConstraint(name = "uk_trip_school_vehicle_number", columnNames = {"school_id", "vehicle_id", "trip_number"})
})
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "trip_id")
	private Integer tripId;

	@ManyToOne
	@JoinColumn(name = "school_id", nullable = false)
	private School school;

	@ManyToOne
	@JoinColumn(name = "vehicle_id", nullable = false)
	private Vehicle vehicle;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@Column(nullable = false, length = 100)
	private String tripName;

	private Integer tripNumber;

	@Column(name = "trip_type", length = 20)
	private String tripType; // MORNING, AFTERNOON

	@Column(name = "scheduled_time")
	private LocalTime scheduledTime;

	@Column(name = "estimated_duration_minutes")
	private Integer estimatedDurationMinutes;

	@Column(name = "trip_status", length = 20)
	private String tripStatus = "NOT_STARTED"; // NOT_STARTED, IN_PROGRESS, COMPLETED, CANCELLED

	@Column(name = "trip_start_time")
	private LocalDateTime tripStartTime;

	@Column(name = "trip_end_time")
	private LocalDateTime tripEndTime;

	@Column(nullable = false)
	private Boolean isActive = true;

	private String createdBy;

	private LocalDateTime createdDate;

	private String updatedBy;

	private LocalDateTime updatedDate;

	@PrePersist
	protected void onCreate() {
		createdDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedDate = LocalDateTime.now();
	}

	@OneToMany(mappedBy = "trip")
	private List<TripStudent> students;

}
