package com.srilanka.realestate.dto;

import java.time.LocalDateTime;

public class BookingCreateDTO {
    private Long propertyId;
    private LocalDateTime scheduledAt;
    private String message;

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}



