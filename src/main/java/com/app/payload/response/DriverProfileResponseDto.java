package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverProfileResponseDto {
    
    private Integer driverId;
    private String driverName;
    private String email;
    private String driverContactNumber;
    private String driverAddress;
    private String driverPhoto;
    private String schoolName;
    private String vehicleNumber;
    private String vehicleType;
    private Boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    // Additional profile information
    private String licenseNumber;
    private String emergencyContact;
    private String bloodGroup;
    private String experience;
    
}
