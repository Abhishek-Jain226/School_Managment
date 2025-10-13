package com.app.payload.response;

import com.app.entity.TripStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripStatusResponseDto {

    private Integer tripStatusId;
    private Integer tripId;
    private String tripName;
    private String status;
    private String statusDisplay;
    private LocalDateTime statusTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalTimeMinutes;
    private String totalTimeDisplay;
    private String remarks;
    private String createdBy;
    private LocalDateTime createdDate;

    /**
     * Get formatted total time display
     */
    public String getTotalTimeDisplay() {
        if (totalTimeMinutes == null) {
            return "N/A";
        }
        
        int hours = totalTimeMinutes / 60;
        int minutes = totalTimeMinutes % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else {
            return String.format("%dm", minutes);
        }
    }

    /**
     * Get status display name
     */
    public String getStatusDisplay() {
        if (status == null) return "Unknown";
        
        switch (status) {
            case "NOT_STARTED":
                return "Not Started";
            case "IN_PROGRESS":
                return "In Progress";
            case "COMPLETED":
                return "Completed";
            case "CANCELLED":
                return "Cancelled";
            case "DELAYED":
                return "Delayed";
            default:
                return status;
        }
    }
}
