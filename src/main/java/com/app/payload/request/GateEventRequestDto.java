package com.app.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GateEventRequestDto {
	
	@NotNull(message = "studentId is required")
    private Integer studentId;

    @NotNull(message = "tripId is required")
    private Integer tripId;

    @NotNull(message = "staffUserId is required")
    private Integer staffUserId; // Logged in Gate Staff ID

}
