package com.hotel.management.service;

import com.hotel.management.entity.Booking;
import com.hotel.management.entity.Room;
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Booking createBooking(Booking booking) {
        Room room = booking.getRoom();
        if (!"Available".equals(room.getStatus())) {
            throw new RuntimeException("Room is not available");
        }
        room.setStatus("Occupied");
        roomRepository.save(room);
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow();
        booking.setStatus("Cancelled");
        Room room = booking.getRoom();
        room.setStatus("Available");
        roomRepository.save(room);
        bookingRepository.save(booking);
    }
}