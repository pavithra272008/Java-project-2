package com.example.demo.repository;

import com.example.demo.model.Room;
import com.example.demo.model.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByNameIgnoreCase(String name);

    Optional<Room> findByRoomCodeIgnoreCase(String roomCode);

    List<Room> findByStatus(RoomStatus status);

    @Query("SELECT r FROM Room r WHERE " +
           "(:minCapacity IS NULL OR r.capacity >= :minCapacity) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:search IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(r.floor) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(r.amenities) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Room> searchRooms(@Param("minCapacity") Integer minCapacity,
                           @Param("status") RoomStatus status,
                           @Param("search") String search);
}
