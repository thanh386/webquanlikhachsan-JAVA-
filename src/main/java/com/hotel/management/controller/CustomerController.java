package com.hotel.management.controller;

import com.hotel.management.entity.Booking;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.User;
import com.hotel.management.service.BookingService;
import com.hotel.management.service.RoomService;
<<<<<<< HEAD
import com.hotel.management.service.UserService;
import com.hotel.management.util.DebugRuntimeLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Controller
@RequestMapping("/khach-hang")
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customer")
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
public class CustomerController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

<<<<<<< HEAD
    @Autowired
    private UserService userService;

    @GetMapping("/bang-dieu-khien")
    public String dashboard() {
        return "redirect:/khach-hang/lich-su-dat-phong";
    }

    @GetMapping("/lich-su-dat-phong")
    public String myBookings(Model model, Authentication auth) {
        User user = userService.getRequiredUserByUsername(auth.getName());
        model.addAttribute("bookings", bookingService.getBookingsByUser(user.getId()));
        model.addAttribute("activePage", "bookings");
        return "customer/my-bookings";
    }

    @GetMapping({"/dat-phong/{roomId}", "/phong/{roomId}"})
    public String bookForm(@PathVariable Long roomId,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
                           Model model) {
        Room room = roomService.getRoomById(roomId);
        LocalDate today = LocalDate.now();
        LocalDate defaultCheckIn  = (checkIn != null && !checkIn.isBefore(today))  ? checkIn  : today.plusDays(1);
        LocalDate defaultCheckOut = (checkOut != null && checkOut.isAfter(defaultCheckIn)) ? checkOut : defaultCheckIn.plusDays(1);

        model.addAttribute("room", room);
        model.addAttribute("today", today);
        model.addAttribute("tomorrow", today.plusDays(1));
        model.addAttribute("defaultCheckIn", defaultCheckIn);
        model.addAttribute("defaultCheckOut", defaultCheckOut);
        model.addAttribute("activePage", "rooms");
        model.addAttribute("galleryImages", buildGallery(room));
        return "customer/room-details";
    }

    @PostMapping("/dat-phong")
    public String bookRoom(@RequestParam Long roomId,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                           @RequestParam(defaultValue = "1") Integer guestCount,
                           @RequestParam(required = false) String guestName,
                           @RequestParam(required = false) String guestPhone,
                           @RequestParam(required = false) String specialRequests,
                           Authentication auth,
                           RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getRequiredUserByUsername(auth.getName());
            // #region agent log
            DebugRuntimeLogger.log(
                "baseline",
                "H5",
                "CustomerController:bookRoom",
                "Customer booking request received",
                new LinkedHashMap<>() {{
                    put("username", user.getUsername());
                    put("userId", user.getId());
                    put("roomId", roomId);
                    put("checkInDate", String.valueOf(checkInDate));
                    put("checkOutDate", String.valueOf(checkOutDate));
                    put("guestCount", guestCount);
                }}
            );
            // #endregion
            Booking booking = bookingService.createBooking(
                user, roomId, checkInDate, checkOutDate, guestCount,
                guestName, guestPhone, specialRequests
            );
            redirectAttributes.addFlashAttribute(
                "success",
                "Yêu cầu đặt phòng đã được ghi nhận với mã " + booking.getResolvedBookingCode()
                    + ". Vui lòng chờ nhân viên xác nhận."
            );
            return "redirect:/khach-hang/lich-su-dat-phong";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/khach-hang/dat-phong/" + roomId;
        }
    }

    @PostMapping("/luot-dat/{id}/huy")
    public String cancelBooking(@PathVariable Long id,
                                @RequestParam(required = false) String cancelReason,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        User user = userService.getRequiredUserByUsername(auth.getName());
        Booking booking = bookingService.getBookingById(id);

        // Kiểm tra quyền sở hữu
        // #region agent log
        DebugRuntimeLogger.log(
            "baseline",
            "H5",
            "CustomerController:cancelBooking",
            "Customer booking ownership check",
            new LinkedHashMap<>() {{
                put("username", user.getUsername());
                put("actorUserId", user.getId());
                put("bookingId", booking.getId());
                put("bookingOwnerUserId", booking.getUser().getId());
                put("bookingStatus", booking.getStatus());
            }}
        );
        // #endregion
        if (!booking.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn chỉ được hủy lượt đặt phòng của chính mình");
            return "redirect:/khach-hang/lich-su-dat-phong";
        }

        // Khách hàng chỉ hủy được khi chưa check-in (cho phép hủy cả khi đã xác nhận)
        if (!booking.isCancellableByCustomer()) {
            redirectAttributes.addFlashAttribute(
                "error",
                "Không thể hủy: đơn đang ở trạng thái '" + booking.getStatus() + "'. "
                    + "Vui lòng liên hệ nhân viên để được hỗ trợ."
            );
            return "redirect:/khach-hang/lich-su-dat-phong";
        }

        try {
            String reason = (cancelReason != null && !cancelReason.isBlank())
                ? "Khách hàng hủy: " + cancelReason.trim()
                : "Khách hàng chủ động hủy đơn";
            bookingService.cancelBooking(id, reason);
            redirectAttributes.addFlashAttribute("success", "Hủy lượt đặt phòng thành công");
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/khach-hang/lich-su-dat-phong";
    }

    // ── Quản lý hồ sơ ────────────────────────────────────────────────────────

    @GetMapping("/ho-so")
    public String profile(Model model, Authentication auth) {
        User user = userService.getRequiredUserByUsername(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("activePage", "profile");
        return "customer/profile";
    }

    @PostMapping("/ho-so")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phone,
                                @RequestParam(required = false) String idCard,
                                Authentication auth,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getRequiredUserByUsername(auth.getName());
            userService.updateProfile(user.getId(), fullName, phone, idCard);
            redirectAttributes.addFlashAttribute("success", "Cập nhật hồ sơ thành công");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/khach-hang/ho-so";
    }

    @PostMapping("/doi-mat-khau")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getRequiredUserByUsername(auth.getName());
            userService.changePassword(user.getId(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + ex.getMessage());
        }
        return "redirect:/khach-hang/ho-so";
    }

    private List<String> buildGallery(Room room) {
        List<String> gallery = new ArrayList<>();
        gallery.add(room.getPrimaryImageUrl());

        List<String> fallbackImages = switch (room.getType()) {
            case "Hạng sang" -> List.of(
                "https://images.unsplash.com/photo-1522798514-97ceb8c4f1c8?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80"
            );
            case "Đôi" -> List.of(
                "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=900&q=80"
            );
            default -> List.of(
                "https://images.unsplash.com/photo-1455587734955-081b22074882?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1445019980597-93fa8acb246c?auto=format&fit=crop&w=900&q=80",
                "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=900&q=80"
            );
        };

        fallbackImages.stream()
            .filter(image -> !gallery.contains(image))
            .forEach(gallery::add);

        while (gallery.size() < 4) {
            gallery.add(room.getPrimaryImageUrl());
        }

        return gallery.subList(0, 4);
    }
}
=======
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
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
