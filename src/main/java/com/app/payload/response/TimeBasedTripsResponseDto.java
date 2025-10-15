package com.app.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeBasedTripsResponseDto {
    
    private String currentTime;
    private String timeSlot; // MORNING, AFTERNOON, EVENING, NIGHT
    private String message;
    private List<TripResponseDto> availableTrips;
    private List<TripResponseDto> allTrips;
    
    // Time-based filtering information
    private Boolean isWorkingHours;
    private String nextTripTime;
    private String workingHoursMessage;
    
}
