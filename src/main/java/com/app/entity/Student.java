package com.app.entity;

import java.time.LocalDateTime;

import com.app.Enum.GenderType;

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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_id")
	private Integer studentId;

	@NotBlank(message = "First name is required")
	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@Column(name = "middle_name", length = 50)
	private String middleName;

	@NotBlank(message = "Last name is required")
	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@Enumerated(EnumType.STRING)
	private GenderType gender;

	@NotBlank(message = "Class is required")
	@Column(name = "class_name", nullable = false, length = 20)
	private String className; // renamed from "Class" to avoid keyword conflict

	@NotBlank(message = "Section is required")
	@Column(name = "section", nullable = false, length = 10)
	private String section;

	@Lob
	@Column(name = "student_photo", columnDefinition = "LONGTEXT")
	private String studentPhoto;

	@ManyToOne
	@JoinColumn(name = "school_id", nullable = false)
	private School school;

	@NotBlank(message = "Mother name is required")
	@Column(name = "mother_name", nullable = false, length = 100)
	private String motherName;

	@NotBlank(message = "Father name is required")
	@Column(name = "father_name", nullable = false, length = 100)
	private String fatherName;

	@NotBlank(message = "Primary contact number is required")
	@Size(min = 10, max = 15)
	@Column(name = "primary_contact_number", nullable = false, length = 15)
	private String primaryContactNumber;

	@Size(min = 10, max = 15)
	@Column(name = "alternate_contact_number", length = 15)
	private String alternateContactNumber;

	//@Email(message = "Email should be valid")
	//@Column(name = "email", unique = true, length = 100)
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
		createdDate = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedDate = LocalDateTime.now();
	}
}
