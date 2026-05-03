package com.hotel.management.controller;

import com.hotel.management.entity.Booking;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.User;
import com.hotel.management.service.BookingService;
import com.hotel.management.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        User user = (User) auth.getPrincipal(); // Assuming UserDetails is User
        model.addAttribute("bookings", bookingService.getBookingsByUser(user.getId()));
        return "customer/dashboard";
    }

    @GetMapping("/rooms")
    public String rooms(Model model) {
        model.addAttribute("rooms", roomService.getAvailableRooms());
        return "customer/rooms";
    }

    @GetMapping("/book/{roomId}")
    public String bookForm(@PathVariable Long roomId, Model model) {
        Room room = roomService.getAllRooms().stream().filter(r -> r.getId().equals(roomId)).findFirst().orElse(null);
        model.addAttribute("room", room);
        model.addAttribute("booking", new Booking());
        return "customer/book";
    }

    @PostMapping("/book")
    public String bookRoom(Booking booking, Authentication auth) {
        User user = (User) auth.getPrincipal();
        booking.setUser(user);
        booking.setStatus("Confirmed");
        bookingService.createBooking(booking);
        return "redirect:/customer/dashboard";
    }
}