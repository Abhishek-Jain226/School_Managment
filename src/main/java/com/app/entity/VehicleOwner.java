package com.app.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vehicle_owner", uniqueConstraints = {
    @UniqueConstraint(name = "uk_vehicle_owner_user", columnNames = {"u_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleOwner {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ownerId;

    @OneToOne
    @JoinColumn(name = "u_id", unique = true, nullable = true)
    private User user;

    @Column(length = 150)
    private String name;
    
    private String email;

    @Column(length = 20)
    private String contactNumber;
    
    @Lob
    @Column(name = "owner_photo", columnDefinition = "LONGTEXT")
    private String ownerPhoto;

    private String address;

    private String createdBy;
    private LocalDateTime createdDate;
    private String updatedBy;
    private LocalDateTime updatedDate;
	
	

}
