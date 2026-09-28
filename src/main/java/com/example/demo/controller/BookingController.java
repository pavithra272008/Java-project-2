package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getAllBookings(
            @RequestParam(required = false) Long roomId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<BookingResponseDto> bookings = bookingService.getAllBookings(roomId, employeeId, status, startDate, endDate);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long id) {
        BookingResponseDto booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto dto) {
        BookingResponseDto created = bookingService.createBooking(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDto> cancelBooking(
            @PathVariable Long id,
            @RequestBody(required = false) CancelBookingRequestDto dto) {
        BookingResponseDto cancelled = bookingService.cancelBooking(id, dto);
        return ResponseEntity.ok(cancelled);
    }

    @PutMapping("/{id}/check-in")
    public ResponseEntity<BookingResponseDto> checkIn(@PathVariable Long id) {
        BookingResponseDto checkedIn = bookingService.checkIn(id);
        return ResponseEntity.ok(checkedIn);
    }

    @PostMapping("/check-conflicts")
    public ResponseEntity<ConflictCheckResponseDto> checkConflicts(@Valid @RequestBody ConflictCheckRequestDto dto) {
        ConflictCheckResponseDto result = bookingService.checkConflicts(dto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/run-auto-release")
    public ResponseEntity<Map<String, Object>> runAutoRelease() {
        int releasedCount = bookingService.releaseNoShowBookings();
        return ResponseEntity.ok(Map.of(
                "releasedCount", releasedCount,
                "message", releasedCount > 0
                        ? "Successfully auto-released " + releasedCount + " no-show bookings."
                        : "No expired unconfirmed bookings found to release."
        ));
    }

    @PostMapping("/{id}/simulate-no-show")
    public ResponseEntity<BookingResponseDto> simulateNoShow(@PathVariable Long id) {
        BookingResponseDto updated = bookingService.simulateNoShowRelease(id);
        return ResponseEntity.ok(updated);
    }
}
