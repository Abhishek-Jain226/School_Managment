package com.app.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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
@Table(name = "vehicle_owner")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleOwner {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ownerId;

    @ManyToOne
    @JoinColumn(name = "u_id", nullable = false)
    private User user;

    @Column(length = 150)
    private String name;
    
    private String email;

    @Column(length = 20)
    private String contactNumber;

    private String address;

    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
	
	

}
