package com.app.entity;

import java.time.LocalDateTime;
import com.app.Enum.EventType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dispatch_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DispatchLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dispatch_log_id")
	private Integer dispatchLogId;

	@ManyToOne
	@JoinColumn(name = "trip_id", nullable = false)
	private Trip trip;

	@ManyToOne
	@JoinColumn(name = "student_id", nullable = false)
	private Student student;

	@ManyToOne
	@JoinColumn(name = "school_id", nullable = false)
	private School school;

	@ManyToOne
	@JoinColumn(name = "vehicle_id", nullable = false)
	private Vehicle vehicle;

	// Event Type ENUM
	@Enumerated(EnumType.STRING)
	@Column(name = "event_type", nullable = false, length = 50)
	private EventType eventType;

	@Column(name = "remarks", length = 255)
	private String remarks;

	// Location tracking fields
	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "address", length = 500)
	private String address;

	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;

	@Column(name = "created_by", length = 50)
	private String createdBy;

	@Column(name = "created_date")
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
