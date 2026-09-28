package com.example.demo.exception;

import com.example.demo.model.Booking;
import java.util.Collections;
import java.util.List;

public class BookingConflictException extends RuntimeException {

    private final List<Booking> conflictingBookings;

    public BookingConflictException(String message) {
        super(message);
        this.conflictingBookings = Collections.emptyList();
    }

    public BookingConflictException(String message, List<Booking> conflictingBookings) {
        super(message);
        this.conflictingBookings = conflictingBookings != null ? conflictingBookings : Collections.emptyList();
    }

    public List<Booking> getConflictingBookings() {
        return conflictingBookings;
    }
}
