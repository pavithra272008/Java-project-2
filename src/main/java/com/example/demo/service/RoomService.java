package com.example.demo.service;

import com.example.demo.dto.RoomRequestDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.model.Room;
import com.example.demo.model.enums.BookingStatus;
import com.example.demo.model.enums.RoomStatus;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms(Integer minCapacity, RoomStatus status, String search) {
        if (minCapacity != null || status != null || (search != null && !search.isBlank())) {
            return roomRepository.searchRooms(minCapacity, status, search);
        }
        return roomRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meeting room not found with ID: " + id));
    }

    @Transactional
    public Room createRoom(RoomRequestDto dto) {
        if (roomRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
            throw new BadRequestException("Room with name '" + dto.getName() + "' already exists");
        }

        Room room = new Room();
        room.setName(dto.getName());
        room.setRoomCode(dto.getRoomCode() != null ? dto.getRoomCode() : generateRoomCode(dto.getName()));
        room.setFloor(dto.getFloor());
        room.setCapacity(dto.getCapacity());
        room.setAmenities(dto.getAmenities());
        room.setStatus(dto.getStatus() != null ? dto.getStatus() : RoomStatus.AVAILABLE);
        room.setImageUrl(dto.getImageUrl());
        room.setDescription(dto.getDescription());

        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Long id, RoomRequestDto dto) {
        Room room = getRoomById(id);

        if (!room.getName().equalsIgnoreCase(dto.getName()) &&
                roomRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
            throw new BadRequestException("Another room with name '" + dto.getName() + "' already exists");
        }

        room.setName(dto.getName());
        if (dto.getRoomCode() != null) room.setRoomCode(dto.getRoomCode());
        room.setFloor(dto.getFloor());
        room.setCapacity(dto.getCapacity());
        room.setAmenities(dto.getAmenities());
        if (dto.getStatus() != null) room.setStatus(dto.getStatus());
        if (dto.getImageUrl() != null) room.setImageUrl(dto.getImageUrl());
        if (dto.getDescription() != null) room.setDescription(dto.getDescription());

        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRoomAvailability(Long roomId, LocalDate date) {
        Room room = getRoomById(roomId);
        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        LocalDateTime dayStart = targetDate.atStartOfDay();
        LocalDateTime dayEnd = targetDate.plusDays(1).atStartOfDay();

        List<Booking> dayBookings = bookingRepository.findBookingsByRoomAndDay(roomId, dayStart, dayEnd)
                .stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.CHECKED_IN)
                .toList();

        Map<String, Object> result = new HashMap<>();
        result.put("room", room);
        result.put("date", targetDate);
        result.put("dayBookings", dayBookings);

        // Compute hourly availability grid from 08:00 to 20:00 (12 slots)
        List<Map<String, Object>> hourlySlots = new ArrayList<>();
        LocalTime currentSlotTime = LocalTime.of(8, 0);
        LocalTime dayEndTime = LocalTime.of(20, 0);

        while (currentSlotTime.isBefore(dayEndTime)) {
            LocalTime nextSlotTime = currentSlotTime.plusHours(1);
            LocalDateTime slotStart = LocalDateTime.of(targetDate, currentSlotTime);
            LocalDateTime slotEnd = LocalDateTime.of(targetDate, nextSlotTime);

            Optional<Booking> matchedBooking = dayBookings.stream()
                    .filter(b -> b.getStartTime().isBefore(slotEnd) && b.getEndTime().isAfter(slotStart))
                    .findFirst();

            Map<String, Object> slotMap = new HashMap<>();
            slotMap.put("startTime", slotStart);
            slotMap.put("endTime", slotEnd);
            slotMap.put("label", currentSlotTime.toString() + " - " + nextSlotTime.toString());
            slotMap.put("isAvailable", matchedBooking.isEmpty());
            slotMap.put("booking", matchedBooking.orElse(null));
            hourlySlots.add(slotMap);

            currentSlotTime = nextSlotTime;
        }

        result.put("hourlySlots", hourlySlots);
        return result;
    }

    private String generateRoomCode(String name) {
        String clean = name.replaceAll("[^a-zA-Z0-9]", "").toUpperCase();
        return clean.length() > 6 ? clean.substring(0, 6) : clean + "-" + (100 + (int)(Math.random() * 900));
    }
}
