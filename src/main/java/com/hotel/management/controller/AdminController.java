package com.hotel.management.controller;

import com.hotel.management.entity.Booking;
import com.hotel.management.entity.Room;
import com.hotel.management.service.BookingService;
import com.hotel.management.service.RoomService;
import com.hotel.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/quan-tri")
public class AdminController {

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("sidebarPendingCount", bookingService.getPendingBookingCount());
    }

    @GetMapping("/bang-dieu-khien")
    public String dashboard(Model model) {
        List<Room> allRooms = roomService.getAllRooms();

        Map<String, Long> statusCounts = new HashMap<>();
        List<String> statuses = Arrays.asList("Trống", "Đang sử dụng", "Bảo trì", "Ngừng khai thác");
        for (String status : statuses) {
            long count = allRooms.stream().filter(room -> status.equals(room.getStatus())).count();
            statusCounts.put(status, count);
        }

        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("roomStatuses", statuses);
        model.addAttribute("totalRooms", (long) allRooms.size());

        model.addAttribute("totalRevenue", bookingService.getActualRevenue());
        model.addAttribute("expectedRevenue", bookingService.getExpectedRevenue());
        model.addAttribute("pendingBookings", bookingService.getPendingBookingCount());
        model.addAttribute("occupiedRooms", bookingService.getOccupiedRoomCount());
        model.addAttribute("customerCount", userService.getAllCustomers().size());

        List<Booking> recentBookings = bookingService.getAllBookings().stream().limit(5).toList();
        model.addAttribute("recentBookings", recentBookings);

        model.addAttribute("activePage", "dashboard");
        return "admin/dashboard";
    }

    @GetMapping("/phong")
    public String listRooms(@RequestParam(required = false) Long editId, Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        Room room = editId != null ? roomService.getRoomById(editId) : new Room();
        model.addAttribute("room", room);
        model.addAttribute("activePage", "rooms");
        return "admin/rooms";
    }

    @PostMapping("/phong/luu")
    public String saveRoom(@ModelAttribute("room") Room room,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/phong";
        }
        try {
            roomService.saveRoom(room);
            redirectAttributes.addFlashAttribute("success", "Lưu thông tin phòng thành công!");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + exception.getMessage());
        }
        return "redirect:/quan-tri/phong";
    }

    @PostMapping("/phong/xoa/{id}")
    public String deleteRoom(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/phong";
        }
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa phòng thành công.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa: " + exception.getMessage());
        }
        return "redirect:/quan-tri/phong";
    }

    @PostMapping("/phong/trang-thai")
    public String updateRoomStatus(@RequestParam Long roomId,
                                   @RequestParam String status,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/phong";
        }
        try {
            roomService.updateRoomStatus(roomId, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái phòng thành công.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quan-tri/phong";
    }

    @GetMapping("/luot-dat")
    public String listBookings(Model model) {
        List<Booking> bookings = bookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        model.addAttribute("activePage", "bookings");
        return "admin/bookings";
    }

    @PostMapping("/luot-dat/duyet/{id}")
    public String approveBooking(@PathVariable Long id,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/luot-dat";
        }
        try {
            bookingService.approveBooking(id);
            redirectAttributes.addFlashAttribute("success", "Đã xác nhận đơn đặt phòng.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quan-tri/luot-dat";
    }

    @PostMapping("/luot-dat/check-in/{id}")
    public String checkIn(@PathVariable Long id,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/luot-dat";
        }
        try {
            bookingService.checkIn(id);
            redirectAttributes.addFlashAttribute("success", "Khách đã check-in thành công.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quan-tri/luot-dat";
    }

    @PostMapping("/luot-dat/check-out/{id}")
    public String checkOut(@PathVariable Long id,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/luot-dat";
        }
        try {
            bookingService.checkOut(id);
            redirectAttributes.addFlashAttribute("success", "Khách đã trả phòng thành công.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quan-tri/luot-dat";
    }

    @PostMapping("/luot-dat/huy/{id}")
    public String cancelBooking(@PathVariable Long id,
                                @RequestParam(required = false) String reason,
                                @RequestParam(required = false, defaultValue = "false") boolean noShow,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/luot-dat";
        }
        try {
            bookingService.cancelBooking(id, reason, noShow);
            redirectAttributes.addFlashAttribute("success", noShow
                ? "Đã đánh dấu no-show và hủy đơn đặt phòng."
                : "Đã hủy đơn đặt phòng.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quan-tri/luot-dat";
    }

    @PostMapping("/luot-dat/thanh-toan/{id}")
    public String updatePaymentStatus(@PathVariable Long id,
                                      @RequestParam String paymentStatus,
                                      Authentication authentication,
                                      RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/luot-dat";
        }
        try {
            bookingService.updatePaymentStatus(id, paymentStatus);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái thanh toán thành công.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/quan-tri/luot-dat";
    }

    @GetMapping("/nhan-vien")
    public String listStaff(Model model) {
        model.addAttribute("staffs", userService.getAllStaff());
        model.addAttribute("activePage", "staff");
        return "admin/staff";
    }

    @PostMapping("/nhan-vien")
    public String saveStaff(@RequestParam("fullName") String fullName,
                            @RequestParam("username") String username,
                            @RequestParam("email") String email,
                            @RequestParam("password") String password,
                            @RequestParam("phone") String phone,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/nhan-vien";
        }
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new IllegalArgumentException("Tên đăng nhập không được để trống");
            }
            if (password == null || password.length() < 6) {
                throw new IllegalArgumentException("Mật khẩu phải từ 6 ký tự");
            }

            userService.createStaffAccount(username.trim(), password, email.trim(), fullName.trim(), phone.trim());
            redirectAttributes.addFlashAttribute("success", "Thêm nhân viên [" + username + "] thành công!");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "Không thể thêm nhân viên: " + exception.getMessage());
        }
        return "redirect:/quan-tri/nhan-vien";
    }

    @PostMapping("/nhan-vien/{id}/xoa")
    public String deleteStaff(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (!ensureStaff(authentication, redirectAttributes)) {
            return "redirect:/quan-tri/nhan-vien";
        }
        try {
            userService.deleteStaffAccount(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Đã xóa tài khoản nhân viên.");
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + exception.getMessage());
        }
        return "redirect:/quan-tri/nhan-vien";
    }

    private boolean ensureStaff(Authentication authentication, RedirectAttributes redirectAttributes) {
        boolean isStaff = authentication != null
            && authentication.getAuthorities().stream().anyMatch(a -> "ROLE_NHAN_VIEN".equals(a.getAuthority()));
        if (!isStaff) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền thực hiện thao tác này.");
        }
        return isStaff;
    }
}
