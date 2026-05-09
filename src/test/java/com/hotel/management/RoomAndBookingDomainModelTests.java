package com.hotel.management;

import com.hotel.management.entity.Booking;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.User;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.repository.UserRepository;
import com.hotel.management.service.BookingService;
import com.hotel.management.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RoomAndBookingDomainModelTests {

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveRoomBackfillsExtendedDisplayFields() {
        Room room = new Room();
        room.setRoomNumber("509");
        room.setType("Đơn");
        room.setPrice(55.0);
        room.setStatus("Trống");

        Room savedRoom = roomService.saveRoom(room);

        assertThat(savedRoom.getCapacity()).isNotNull().isPositive();
        assertThat(savedRoom.getDescription()).isNotBlank();
        assertThat(savedRoom.getImageUrl()).isNotBlank();
        assertThat(savedRoom.getAmenities()).isNotBlank();
    }

    @Test
    void createBookingStoresSnapshotMetadata() {
        User user = userRepository.findByUsername("khachhang").orElseThrow();
        Room room = roomRepository.findAll().stream()
            .filter(existingRoom -> "101".equals(existingRoom.getRoomNumber()))
            .findFirst()
            .orElseThrow();

        room.setStatus("Trống");
        room.setCapacity(3);
        roomRepository.save(room);

        Booking booking = bookingService.createBooking(
            user,
            room.getId(),
            LocalDate.now().plusDays(2),
            LocalDate.now().plusDays(4),
            2
        );

        assertThat(booking.getBookingCode()).startsWith("BK-");
        assertThat(booking.getGuestCount()).isEqualTo(2);
        assertThat(booking.getUnitPrice()).isEqualTo(room.getPriceInVnd());
        assertThat(booking.getTotalPrice()).isEqualTo(room.getPriceInVnd() * 2);
        assertThat(booking.getCreatedAt()).isNotNull();
        assertThat(booking.getUpdatedAt()).isNotNull();
        assertThat(booking.getCancelReason()).isNull();
    }
}
