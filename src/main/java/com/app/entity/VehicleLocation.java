package com.app.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_locations", indexes = {
    @Index(name = "idx_vehicle_locations_trip_id", columnList = "trip_id"),
    @Index(name = "idx_vehicle_locations_created_date", columnList = "created_date"),
    @Index(name = "idx_vehicle_locations_vehicle_id", columnList = "vehicle_id"),
    @Index(name = "idx_vehicle_locations_driver_id", columnList = "driver_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer locationId;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "bearing")
    private Double bearing;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }
}
