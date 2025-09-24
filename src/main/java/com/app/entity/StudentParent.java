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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_parent")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentParent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer studentParentId;

	@ManyToOne
	@JoinColumn(name = "student_id", nullable = false)
	private Student student;

	@ManyToOne
	@JoinColumn(name = "parent_user_id", nullable = true)
	private User parentUser;

	@Column(length = 50)
	private String relation;

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
