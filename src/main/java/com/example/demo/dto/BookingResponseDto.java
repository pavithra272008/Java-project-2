package com.example.demo.dto;

import com.example.demo.model.Booking;
import com.example.demo.model.Employee;
import com.example.demo.model.Room;
import com.example.demo.model.enums.BookingStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class BookingResponseDto {

    private Long id;
    private Room room;
    private Employee employee;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private Integer attendeesCount;
    private LocalDateTime createdAt;
    private LocalDateTime checkedInAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    // Computed UI helper fields
    private long durationMinutes;
    private boolean canCheckIn;
    private boolean isGracePeriodActive;
    private long minutesRemainingForGrace;
    private boolean isOngoing;
    private boolean isPast;

    public BookingResponseDto() {
    }

    public static BookingResponseDto fromEntity(Booking booking) {
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getId());
        dto.setRoom(booking.getRoom());
        dto.setEmployee(booking.getEmployee());
        dto.setTitle(booking.getTitle());
        dto.setDescription(booking.getDescription());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());
        dto.setStatus(booking.getStatus());
        dto.setAttendeesCount(booking.getAttendeesCount());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setCheckedInAt(booking.getCheckedInAt());
        dto.setCancelledAt(booking.getCancelledAt());
        dto.setCancellationReason(booking.getCancellationReason());

        LocalDateTime now = LocalDateTime.now();
        if (booking.getStartTime() != null && booking.getEndTime() != null) {
            dto.setDurationMinutes(Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes());
            dto.setOngoing(now.isAfter(booking.getStartTime()) && now.isBefore(booking.getEndTime()));
            dto.setPast(now.isAfter(booking.getEndTime()));

            LocalDateTime graceEnd = booking.getStartTime().plusMinutes(10);
            LocalDateTime checkInOpen = booking.getStartTime().minusMinutes(15);

            // Check-in is valid from 15 mins before start until 10 mins after start
            boolean canCheck = booking.getStatus() == BookingStatus.CONFIRMED &&
                               now.isAfter(checkInOpen) &&
                               now.isBefore(graceEnd);
            dto.setCanCheckIn(canCheck);

            boolean graceActive = booking.getStatus() == BookingStatus.CONFIRMED &&
                                  now.isAfter(booking.getStartTime()) &&
                                  now.isBefore(graceEnd);
            dto.setGracePeriodActive(graceActive);

            if (graceActive) {
                dto.setMinutesRemainingForGrace(Math.max(0, Duration.between(now, graceEnd).toMinutes()));
            } else {
                dto.setMinutesRemainingForGrace(0);
            }
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Integer getAttendeesCount() {
        return attendeesCount;
    }

    public void setAttendeesCount(Integer attendeesCount) {
        this.attendeesCount = attendeesCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isCanCheckIn() {
        return canCheckIn;
    }

    public void setCanCheckIn(boolean canCheckIn) {
        this.canCheckIn = canCheckIn;
    }

    public boolean isGracePeriodActive() {
        return isGracePeriodActive;
    }

    public void setGracePeriodActive(boolean gracePeriodActive) {
        isGracePeriodActive = gracePeriodActive;
    }

    public long getMinutesRemainingForGrace() {
        return minutesRemainingForGrace;
    }

    public void setMinutesRemainingForGrace(long minutesRemainingForGrace) {
        this.minutesRemainingForGrace = minutesRemainingForGrace;
    }

    public boolean isOngoing() {
        return isOngoing;
    }

    public void setOngoing(boolean ongoing) {
        isOngoing = ongoing;
    }

    public boolean isPast() {
        return isPast;
    }

    public void setPast(boolean past) {
        isPast = past;
    }
}
