package com.hotel.management.repository;

import com.hotel.management.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(String status);
<<<<<<< HEAD
    java.util.Optional<Room> findByRoomNumber(String roomNumber);
=======
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
}