package com.app.entity;

import java.time.LocalDateTime;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "vehicle_driver", indexes = {
    @Index(name = "idx_vehicle_driver_driver_id", columnList = "driver_id"),
    @Index(name = "idx_vehicle_driver_vehicle_id", columnList = "vehicle_id"),
    @Index(name = "idx_vehicle_driver_school_id", columnList = "school_id"),
    @Index(name = "idx_vehicle_driver_is_active", columnList = "is_active"),
    @Index(name = "idx_vehicle_driver_is_primary", columnList = "is_primary")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDriver {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehicleDriverId;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    private Boolean isPrimary;
    private Boolean isActive;
    
    // Driver rotation and availability
    private Boolean isBackupDriver = false;
    private Boolean isAvailable = true;
    private String unavailabilityReason;

    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;

    // Assignment period
    private LocalDate startDate;
    private LocalDate endDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        if (this.startDate == null) {
            this.startDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

}
