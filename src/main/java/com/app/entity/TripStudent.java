package com.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
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
@Table(name = "trip_students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripStudent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tripStudentId;

	@ManyToOne
	@JoinColumn(name = "trip_id", nullable = false)
	private Trip trip;

	@ManyToOne
	@JoinColumn(name = "student_id", nullable = false)
	private Student student;

	private Integer pickupOrder;

	private String createdBy;
	private LocalDateTime createdDate;
	private String updatedBy;
	private LocalDateTime updatedDate;

}
