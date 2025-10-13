package com.app.entity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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
@Table(name = "trip_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_status_id")
    private Integer tripStatusId;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TripStatusType status;

    @Column(name = "status_time", nullable = false)
    private LocalDateTime statusTime;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "total_time_minutes")
    private Integer totalTimeMinutes;

    @Column(name = "remarks", length = 255)
    private String remarks;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (statusTime == null) {
            statusTime = LocalDateTime.now();
        }
        calculateTotalTime();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateTotalTime();
    }

    /**
     * Calculates total time in minutes between start and end time
     */
    public void calculateTotalTime() {
        if (startTime != null && endTime != null) {
            this.totalTimeMinutes = (int) ChronoUnit.MINUTES.between(startTime, endTime);
        }
    }

    /**
     * Sets start time and calculates total time if end time is also set
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        calculateTotalTime();
    }

    /**
     * Sets end time and calculates total time if start time is also set
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        calculateTotalTime();
    }

    public enum TripStatusType {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED,
        DELAYED
    }
}
