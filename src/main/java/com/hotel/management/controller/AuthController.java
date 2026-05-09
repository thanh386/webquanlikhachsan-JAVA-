package com.hotel.management.controller;

<<<<<<< HEAD
import com.hotel.management.entity.Room;
import com.hotel.management.entity.User;
import com.hotel.management.service.RoomService;
import com.hotel.management.service.UserService;
import com.hotel.management.util.DebugRuntimeLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
=======
import com.hotel.management.entity.User;
import com.hotel.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
=======
import java.util.Set;
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

<<<<<<< HEAD
    @Autowired
    private RoomService roomService;

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false, defaultValue = "1") Integer guests,
            Model model) {

        LocalDate today = LocalDate.now();
        LocalDate defaultCheckIn = (checkInDate != null && !checkInDate.isBefore(today))
            ? checkInDate
            : today.plusDays(1);
        LocalDate defaultCheckOut = (checkOutDate != null && checkOutDate.isAfter(defaultCheckIn))
            ? checkOutDate
            : defaultCheckIn.plusDays(1);

        int normalizedGuests = (guests == null || guests < 1) ? 1 : guests;

        // ✅ Tìm phòng theo ngày thực sự (loại trừ phòng đã có booking chồng lịch)
        List<Room> rooms;
        boolean isSearching = (checkInDate != null || checkOutDate != null);
        if (isSearching) {
            rooms = roomService.getAvailableRoomsByDate(defaultCheckIn, defaultCheckOut, normalizedGuests);
        } else {
            // Trang chủ không có tham số tìm kiếm — hiển thị phòng trống vật lý
            rooms = roomService.getAvailableRooms();
        }

        // #region agent log
        DebugRuntimeLogger.log(
            "baseline",
            "H1",
            "AuthController:home",
            "Home room search result",
            new LinkedHashMap<>() {{
                put("isSearching", isSearching);
                put("checkIn", String.valueOf(defaultCheckIn));
                put("checkOut", String.valueOf(defaultCheckOut));
                put("guests", normalizedGuests);
                put("roomCount", rooms.size());
                put("roomIds", rooms.stream().map(Room::getId).collect(Collectors.toList()));
            }}
        );
        // #endregion

        model.addAttribute("rooms", rooms);
        model.addAttribute("today", today);
        model.addAttribute("searchCheckInDate", defaultCheckIn);
        model.addAttribute("searchCheckOutDate", defaultCheckOut);
        model.addAttribute("guests", normalizedGuests);
        model.addAttribute("isSearching", isSearching);
        model.addAttribute("activePage", "home");
        return "customer/index";
    }

    @GetMapping("/dang-nhap")
=======
    @GetMapping("/login")
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    public String login() {
        return "login";
    }

<<<<<<< HEAD
    @GetMapping("/dang-ky")
    public String registerForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new User());
        }
        return "register";
    }

    @GetMapping("/quen-mat-khau")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/dang-ky")
    public String register(User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerCustomer(user);
            return "redirect:/dang-nhap?dangKyThanhCong";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/dang-ky";
        }
    }

    @GetMapping("/bang-dieu-khien")
    public String dashboard(Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_NHAN_VIEN"));
        String redirectTarget = isStaff ? "/quan-tri/bang-dieu-khien" : "/khach-hang/lich-su-dat-phong";

        // #region agent log
        DebugRuntimeLogger.log(
            "baseline",
            "H3",
            "AuthController:dashboard",
            "Dashboard role redirect decision",
            new LinkedHashMap<>() {{
                put("username", auth.getName());
                put("authorities", auth.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList()));
                put("redirectTarget", redirectTarget);
            }}
        );
        // #endregion

        if (isStaff) {
            return "redirect:/quan-tri/bang-dieu-khien";
        }
        return "redirect:/khach-hang/lich-su-dat-phong";
    }
}
=======
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(User user, String role) {
        Set<String> roles = Set.of(role);
        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/customer/dashboard";
        }
    }
}
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
