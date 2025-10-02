package com.app.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleAssignmentRequestDto {
	
	private Integer vehicleId;
    private Integer ownerId;
    private Integer schoolId;
    private String createdBy;
    private String updatedBy;

}
