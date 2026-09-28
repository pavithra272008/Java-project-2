package com.example.demo.repository;
import com.example.demo.model.Booking;
import com.example.demo.model.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByRoomIdOrderByStartTimeAsc(Long roomId);

    List<Booking> findByEmployeeIdOrderByStartTimeDesc(Long employeeId);

    List<Booking> findByStatusOrderByStartTimeAsc(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.status IN :statuses " +
           "AND (b.startTime < :endTime AND b.endTime > :startTime) " +
           "AND (:excludeBookingId IS NULL OR b.id <> :excludeBookingId)")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("statuses") Collection<BookingStatus> statuses,
                                         @Param("excludeBookingId") Long excludeBookingId);

    @Query("SELECT b FROM Booking b WHERE b.status = :status " +
           "AND b.startTime <= :graceCutoffTime")
    List<Booking> findNoShowCandidates(@Param("status") BookingStatus status,
                                       @Param("graceCutoffTime") LocalDateTime graceCutoffTime);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
           "AND b.startTime >= :dayStart AND b.startTime < :dayEnd " +
           "ORDER BY b.startTime ASC")
    List<Booking> findBookingsByRoomAndDay(@Param("roomId") Long roomId,
                                          @Param("dayStart") LocalDateTime dayStart,
                                          @Param("dayEnd") LocalDateTime dayEnd);

    @Query("SELECT b FROM Booking b WHERE b.startTime >= :dayStart AND b.startTime < :dayEnd " +
           "ORDER BY b.startTime ASC")
    List<Booking> findAllBookingsForDay(@Param("dayStart") LocalDateTime dayStart,
                                       @Param("dayEnd") LocalDateTime dayEnd);

    @Query("SELECT b FROM Booking b WHERE " +
           "(:roomId IS NULL OR b.room.id = :roomId) AND " +
           "(:employeeId IS NULL OR b.employee.id = :employeeId) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:startDate IS NULL OR b.startTime >= :startDate) AND " +
           "(:endDate IS NULL OR b.endTime <= :endDate) " +
           "ORDER BY b.startTime DESC")
    List<Booking> filterBookings(@Param("roomId") Long roomId,
                                @Param("employeeId") Long employeeId,
                                @Param("status") BookingStatus status,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
}