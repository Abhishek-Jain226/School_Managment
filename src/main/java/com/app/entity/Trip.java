package com.app.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.app.Enum.TripType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "trips", 
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_trip_school_vehicle_number", columnNames = {"school_id", "vehicle_id", "trip_number"})
    },
    indexes = {
        @Index(name = "idx_trips_driver_id", columnList = "driver_id"),
        @Index(name = "idx_trips_school_id", columnList = "school_id"),
        @Index(name = "idx_trips_vehicle_id", columnList = "vehicle_id"),
        @Index(name = "idx_trips_trip_type", columnList = "trip_type")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

	@Enumerated(EnumType.STRING)
	@Column(name = "trip_type", length = 20)
	private TripType tripType;

	@Column(name = "scheduled_time", nullable = true)
	private LocalTime scheduledTime;

	@Column(name = "estimated_duration_minutes")
	private Integer estimatedDurationMinutes;

	@Column(name = "trip_status", length = 20, nullable = true)
	private String tripStatus; // Made nullable as requested

	@Column(name = "route_name", length = 200)
	private String routeName;

	@Column(name = "route_description", length = 500)
	private String routeDescription;

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
		// Calculate estimated duration when trip times are updated
		calculateEstimatedDuration();
	}

	/**
	 * Calculates estimated duration in minutes based on trip start and end times
	 */
	public void calculateEstimatedDuration() {
		if (tripStartTime != null && tripEndTime != null) {
			this.estimatedDurationMinutes = (int) ChronoUnit.MINUTES.between(tripStartTime, tripEndTime);
		}
	}

	/**
	 * Sets trip start time and calculates duration if end time is also set
	 */
	public void setTripStartTime(LocalDateTime tripStartTime) {
		this.tripStartTime = tripStartTime;
		calculateEstimatedDuration();
	}

	/**
	 * Sets trip end time and calculates duration if start time is also set
	 */
	public void setTripEndTime(LocalDateTime tripEndTime) {
		this.tripEndTime = tripEndTime;
		calculateEstimatedDuration();
	}

	@OneToMany(mappedBy = "trip")
	private List<TripStudent> students;

}
