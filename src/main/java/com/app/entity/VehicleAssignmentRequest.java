package com.app.entity;

import java.time.LocalDateTime;

import com.app.Enum.RequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "vehicle_assignment_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleAssignmentRequest {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer requestId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private VehicleOwner owner;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;  // PENDING, APPROVED, REJECTED

    private String createdBy;
    private LocalDateTime createdDate;

    private String updatedBy;
    private LocalDateTime updatedDate;

    @PrePersist
    public void onCreate() {
        createdDate = LocalDateTime.now();
        status = RequestStatus.PENDING;
    }

    @PreUpdate
    public void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

}
