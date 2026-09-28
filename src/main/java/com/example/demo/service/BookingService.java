package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.BookingConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.Employee;
import com.example.demo.model.Room;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.model.enums.RoomStatus;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.EmployeeRepository;
import com.example.demo.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final EmployeeRepository employeeRepository;

    public BookingService(BookingRepository bookingRepository,
                          RoomRepository roomRepository,
                          EmployeeRepository employeeRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getAllBookings(Long roomId, Long employeeId, BookingStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        return bookingRepository.filterBookings(roomId, employeeId, status, startDate, endDate)
                .stream()
                .map(BookingResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));
        return BookingResponseDto.fromEntity(booking);
    }

    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto dto) {
        // 1. Basic validation
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new BadRequestException("Start time and end time are required");
        }
        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new BadRequestException("Meeting start time must be before end time");
        }

        // 2. Fetch Room and Employee
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Meeting room not found with ID: " + dto.getRoomId()));

        if (room.getStatus() != RoomStatus.AVAILABLE) {
            throw new BadRequestException("Room '" + room.getName() + "' is currently under maintenance and cannot be booked");
        }

        if (dto.getAttendeesCount() != null && dto.getAttendeesCount() > room.getCapacity()) {
            throw new BadRequestException("Attendees count (" + dto.getAttendeesCount() + ") exceeds room capacity (" + room.getCapacity() + ")");
        }

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + dto.getEmployeeId()));

        // 3. Enforce Business Rule: No two confirmed bookings for the same room may overlap in time.
        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                room.getId(),
                dto.getStartTime(),
                dto.getEndTime(),
                List.of(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN),
                null
        );

        if (!conflicts.isEmpty()) {
            Booking firstConflict = conflicts.get(0);
            String errorMsg = String.format(
                    "Conflict detected: Room '%s' is already booked for '%s' by %s (%s to %s on %s).",
                    room.getName(),
                    firstConflict.getTitle(),
                    firstConflict.getEmployee().getName(),
                    firstConflict.getStartTime().format(TIME_FMT),
                    firstConflict.getEndTime().format(TIME_FMT),
                    firstConflict.getStartTime().format(DATE_FMT)
            );
            log.warn("Booking conflict rejected for room {}: {}", room.getId(), errorMsg);
            throw new BookingConflictException(errorMsg, conflicts);
        }

        // 4. Save booking
        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setEmployee(employee);
        booking.setTitle(dto.getTitle());
        booking.setDescription(dto.getDescription());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setAttendeesCount(dto.getAttendeesCount() != null ? dto.getAttendeesCount() : 1);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setCreatedAt(LocalDateTime.now());

        Booking saved = bookingRepository.save(booking);
        log.info("Booking confirmed: ID={} for Room='{}' from {} to {}", saved.getId(), room.getName(), saved.getStartTime(), saved.getEndTime());
        return BookingResponseDto.fromEntity(saved);
    }

    @Transactional
    public BookingResponseDto cancelBooking(Long id, CancelBookingRequestDto dto) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Booking #" + id + " is already cancelled");
        }
        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel an already completed booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setCancellationReason(dto != null && dto.getReason() != null ? dto.getReason() : "Cancelled by user");

        Booking saved = bookingRepository.save(booking);
        log.info("Booking cancelled: ID={}, room slot freed immediately", id);
        return BookingResponseDto.fromEntity(saved);
    }

    @Transactional
    public BookingResponseDto checkIn(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (booking.getStatus() == BookingStatus.CHECKED_IN) {
            return BookingResponseDto.fromEntity(booking);
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("Cannot check in to a cancelled booking");
        }

        if (booking.getStatus() == BookingStatus.NO_SHOW) {
            throw new BadRequestException("This booking was already auto-released due to 10-minute no-show");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkInWindowStart = booking.getStartTime().minusMinutes(15);
        LocalDateTime gracePeriodEnd = booking.getStartTime().plusMinutes(10);

        if (now.isBefore(checkInWindowStart)) {
            throw new BadRequestException("Check-in opens 15 minutes before the meeting start time (" +
                    booking.getStartTime().format(TIME_FMT) + ")");
        }

        if (now.isAfter(gracePeriodEnd)) {
            // Expired! Mark as NO_SHOW
            booking.setStatus(BookingStatus.NO_SHOW);
            bookingRepository.save(booking);
            throw new BadRequestException("Check-in grace period (10 minutes) has expired. The booking was marked as No-Show and released.");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setCheckedInAt(now);
        Booking saved = bookingRepository.save(booking);
        log.info("Employee checked in for booking ID={}", id);
        return BookingResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ConflictCheckResponseDto checkConflicts(ConflictCheckRequestDto dto) {
        if (dto.getRoomId() == null || dto.getStartTime() == null || dto.getEndTime() == null) {
            return new ConflictCheckResponseDto(false, "Incomplete parameters", List.of());
        }

        List<Booking> conflicts = bookingRepository.findConflictingBookings(
                dto.getRoomId(),
                dto.getStartTime(),
                dto.getEndTime(),
                List.of(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN),
                dto.getExcludeBookingId()
        );

        if (conflicts.isEmpty()) {
            return new ConflictCheckResponseDto(false, "Room is free for the selected time range! No conflicts found.", List.of());
        }

        List<BookingResponseDto> conflictDtos = conflicts.stream()
                .map(BookingResponseDto::fromEntity)
                .collect(Collectors.toList());

        Booking first = conflicts.get(0);
        String msg = String.format("Overlaps with '%s' (%s - %s) booked by %s.",
                first.getTitle(),
                first.getStartTime().format(TIME_FMT),
                first.getEndTime().format(TIME_FMT),
                first.getEmployee().getName()
        );

        return new ConflictCheckResponseDto(true, msg, conflictDtos);
    }

    /**
     * Business Rule 5: Auto-release a booking if the organizer does not check in within 10 minutes (no-show).
     */
    @Transactional
    public int releaseNoShowBookings() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(10);
        List<Booking> noShows = bookingRepository.findNoShowCandidates(BookingStatus.CONFIRMED, cutoff);

        if (noShows.isEmpty()) {
            return 0;
        }

        for (Booking booking : noShows) {
            booking.setStatus(BookingStatus.NO_SHOW);
            bookingRepository.save(booking);
            log.info("AUTO-RELEASE: Booking ID={} for room '{}' released due to no-show (> 10m grace period elapsed).",
                    booking.getId(), booking.getRoom().getName());
        }

        return noShows.size();
    }

    /**
     * Simulation method for testing: immediately fast-forward a booking into NO_SHOW status
     * to demonstrate the automatic 10-minute release rule!
     */
    @Transactional
    public BookingResponseDto simulateNoShowRelease(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new BadRequestException("Can only auto-release bookings in CONFIRMED state (current: " + booking.getStatus() + ")");
        }

        booking.setStatus(BookingStatus.NO_SHOW);
        booking.setCancellationReason("Auto-released after 10-minute check-in grace period expired (Simulated)");
        Booking saved = bookingRepository.save(booking);
        log.info("SIMULATED AUTO-RELEASE: Booking ID={} marked as NO_SHOW", id);
        return BookingResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.findByStatus(RoomStatus.AVAILABLE).size();
        stats.setTotalRooms(totalRooms);
        stats.setAvailableRooms(availableRooms);
        stats.setMaintenanceRooms(totalRooms - availableRooms);

        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.plusDays(1).atStartOfDay();
        List<Booking> todayBookings = bookingRepository.findAllBookingsForDay(dayStart, dayEnd);

        stats.setTotalBookingsToday(todayBookings.size());

        LocalDateTime now = LocalDateTime.now();
        long activeNow = todayBookings.stream()
                .filter(b -> (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN)
                        && now.isAfter(b.getStartTime()) && now.isBefore(b.getEndTime()))
                .count();
        stats.setActiveMeetingsNow(activeNow);

        long upcoming = todayBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED && now.isBefore(b.getStartTime()))
                .count();
        stats.setTotalUpcomingBookings(upcoming);

        long checkedIn = todayBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CHECKED_IN)
                .count();
        stats.setTotalCheckedInBookings(checkedIn);

        long cancelled = todayBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                .count();
        stats.setTotalCancelledBookings(cancelled);

        long noShows = todayBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.NO_SHOW)
                .count();
        stats.setAutoReleasedNoShows(noShows);

        // Utilization % (Active or completed meetings vs room day capacity)
        if (totalRooms > 0) {
            double rate = Math.min(100.0, (todayBookings.size() / (double)(totalRooms * 6)) * 100.0);
            stats.setRoomUtilizationRate(Math.round(rate * 10.0) / 10.0);
        } else {
            stats.setRoomUtilizationRate(0.0);
        }

        return stats;
    }
}
