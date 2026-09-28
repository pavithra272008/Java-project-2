package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ConflictCheckRequestDto {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    private Long excludeBookingId;

    public ConflictCheckRequestDto() {
    }

    public ConflictCheckRequestDto(Long roomId, LocalDateTime startTime, LocalDateTime endTime, Long excludeBookingId) {
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.excludeBookingId = excludeBookingId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Long getExcludeBookingId() {
        return excludeBookingId;
    }

    public void setExcludeBookingId(Long excludeBookingId) {
        this.excludeBookingId = excludeBookingId;
    }
}
