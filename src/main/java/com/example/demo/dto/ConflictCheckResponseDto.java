package com.example.demo.dto;

import java.util.List;

public class ConflictCheckResponseDto {

    private boolean hasConflict;
    private String message;
    private List<BookingResponseDto> conflictingBookings;

    public ConflictCheckResponseDto() {
    }

    public ConflictCheckResponseDto(boolean hasConflict, String message, List<BookingResponseDto> conflictingBookings) {
        this.hasConflict = hasConflict;
        this.message = message;
        this.conflictingBookings = conflictingBookings;
    }

    public boolean isHasConflict() {
        return hasConflict;
    }

    public void setHasConflict(boolean hasConflict) {
        this.hasConflict = hasConflict;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BookingResponseDto> getConflictingBookings() {
        return conflictingBookings;
    }

    public void setConflictingBookings(List<BookingResponseDto> conflictingBookings) {
        this.conflictingBookings = conflictingBookings;
    }
}
