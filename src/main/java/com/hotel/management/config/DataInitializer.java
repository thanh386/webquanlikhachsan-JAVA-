package com.hotel.management.config;

import com.hotel.management.entity.Booking;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.User;
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.repository.UserRepository;
import com.hotel.management.util.DebugRuntimeLogger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(UserRepository userRepository,
                                      PasswordEncoder passwordEncoder,
                                      RoomRepository roomRepository,
                                      BookingRepository bookingRepository) {
        return args -> {
            // #region agent log
            DebugRuntimeLogger.log(
                "baseline",
                "H6",
                "DataInitializer:seedData",
                "Application startup seed begin",
                Map.of("argsCount", args == null ? 0 : args.length)
            );
            // #endregion
            synchronizeUserRoles(userRepository);
            synchronizeSampleAccounts(userRepository, passwordEncoder);
            ensureSampleRooms(roomRepository);
            synchronizeRoomData(roomRepository);
            synchronizeBookings(bookingRepository);
            // #region agent log
            DebugRuntimeLogger.log(
                "baseline",
                "H6",
                "DataInitializer:seedData",
                "Application startup seed complete",
                Map.of(
                    "userCount", userRepository.count(),
                    "roomCount", roomRepository.count(),
                    "bookingCount", bookingRepository.count()
                )
            );
            // #endregion
        };
    }

    private void ensureSampleRooms(RoomRepository roomRepository) {
        // Luôn kiểm tra danh sách phòng mẫu để đảm bảo máy nào clone về cũng có đủ
        List<Room> sampleRooms = new ArrayList<>(List.of(
            new Room("101", "Đơn", 45.0, "Trống", 2, 
                "Phòng đơn tiêu chuẩn với thiết kế tối giản, hiện đại, tối ưu cho khách đi công tác.", 
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=1200&q=80",
                "Wifi miễn phí\nĐiều hòa\nTV 43 inch\nBàn làm việc\nMinibar"),
            new Room("102", "Đơn", 45.0, "Trống", 2, 
                "Phòng đơn hướng phố với cửa sổ rộng, mang lại cảm giác thoáng đãng và tràn đầy năng lượng.", 
                "https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=1200&q=80",
                "Wifi miễn phí\nĐiều hòa\nTV 43 inch\nBàn làm việc\nMinibar"),
            new Room("201", "Đôi", 75.0, "Trống", 4, 
                "Không gian rộng rãi cho cặp đôi với giường King-size và ban công ngắm bình minh.", 
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&q=80",
                "Giường King-size\nBan công riêng\nBồn tắm đứng\nWifi tốc độ cao\nMáy pha cà phê"),
            new Room("202", "Đôi", 75.0, "Trống", 4, 
                "Phòng đôi Superior với nội thất gỗ ấm cúng, phù hợp cho những kỳ nghỉ gia đình nhỏ.", 
                "https://images.unsplash.com/photo-1566195992011-5f6b21e539aa?auto=format&fit=crop&w=1200&q=80",
                "Giường đôi tiêu chuẩn\nSofa nghỉ\nTV 55 inch\nWifi miễn phí\nMinibar"),
            new Room("301", "Hạng sang", 150.0, "Trống", 4, 
                "Suite cao cấp tầng cao với tầm nhìn toàn cảnh đại dương và dịch vụ quản gia riêng.", 
                "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?auto=format&fit=crop&w=1200&q=80",
                "View biển panorama\nBồn tắm nằm Jacuzzi\nQuản gia riêng\nPhòng khách tách biệt\nĐưa đón sân bay"),
            new Room("302", "Hạng sang", 160.0, "Bảo trì", 4, 
                "Phòng Executive Suite dành cho giới thượng lưu với thiết kế sang trọng bậc nhất.", 
                "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?auto=format&fit=crop&w=1200&q=80",
                "Nội thất cao cấp\nHệ thống âm thanh Bose\nPhòng tắm lát đá marble\nWifi 6G\nBữa sáng đặc quyền"),
            new Room("401", "Đôi", 85.0, "Trống", 4, 
                "Phòng Deluxe Double mang phong cách chuyên nghiệp, kết hợp truyền thống và hiện đại.", 
                "https://images.unsplash.com/photo-1591088398332-8a77d49d76a9?auto=format&fit=crop&w=1200&q=80",
                "Phong cách hiện đại\nGiường cao cấp\nBan công rộng\nKét sắt an toàn\nDịch vụ giặt ủi"),
            new Room("402", "Đôi", 90.0, "Trống", 4, 
                "Phòng Premier Double với diện tích lớn nhất trong phân khúc, view hướng hồ bơi vô cực.", 
                "https://images.unsplash.com/photo-1544161515-4ab6ce6db874?auto=format&fit=crop&w=1200&q=80",
                "View hồ bơi\nSofa bọc da\nTV 65 inch\nWifi miễn phí\nNước uống chào mừng"),
            new Room("501", "Hạng sang", 250.0, "Trống", 6, 
                "Presidential Suite - Đỉnh cao của sự xa hoa với 3 phòng ngủ và hồ bơi riêng trên tầng thượng.", 
                "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?auto=format&fit=crop&w=1200&q=80",
                "Hồ bơi riêng\n3 Phòng ngủ\nPhòng ăn riêng\nBếp đầy đủ tiện nghi\nDịch vụ 24/7"),
            new Room("502", "Hạng sang", 220.0, "Trống", 5, 
                "Royal Penthouse với không gian mở và hệ thống kính trần, mang cả bầu trời sao vào phòng ngủ.", 
                "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=1200&q=80",
                "Kính trần tự động\nSky Garden riêng\nPhòng tắm xông hơi\nBar riêng trong phòng\nTiện nghi cao cấp"),
            new Room("601", "Hạng sang", 500.0, "Trống", 8, 
                "Presidential Diamond Suite - Phòng nguyên thủ quốc gia với tiêu chuẩn an ninh và sang trọng tuyệt đối.", 
                "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=1200&q=80",
                "Phòng họp riêng\nKính chống đạn\nPhục vụ 24/7\nThang máy riêng\nView 360 độ thành phố")
        ));

        // Lưu những phòng chưa tồn tại
        for (Room sample : sampleRooms) {
            if (roomRepository.findByRoomNumber(sample.getRoomNumber()).isEmpty()) {
                roomRepository.save(sample);
            }
        }
    }

    private void synchronizeUserRoles(UserRepository userRepository) {
        List<User> users = userRepository.findAll();
        List<User> changedUsers = new ArrayList<>();

        for (User user : users) {
            Set<String> normalizedRoles = normalizeRoles(user.getRoles());
            if (!normalizedRoles.equals(user.getRoles())) {
                user.setRoles(normalizedRoles);
                changedUsers.add(user);
            }
        }

        if (!changedUsers.isEmpty()) {
            userRepository.saveAll(changedUsers);
        }
    }

    private void synchronizeSampleAccounts(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        createOrUpdateAccount(userRepository, passwordEncoder, "admin", "quantri", "quantri123", "quantri@khachsan.local", Set.of("ROLE_NHAN_VIEN"));
        createOrUpdateAccount(userRepository, passwordEncoder, "customer", "khachhang", "khachhang123", "khachhang@khachsan.local", Set.of("ROLE_KHACH_HANG"));
    }

    private void createOrUpdateAccount(UserRepository userRepository, PasswordEncoder passwordEncoder, String oldUsername, String newUsername, String password, String email, Set<String> roles) {
        Optional<User> currentUser = userRepository.findByUsername(newUsername);
        if (currentUser.isPresent()) {
            User user = currentUser.get();
            user.setEmail(email);
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return;
        }

        Optional<User> legacyUser = userRepository.findByUsername(oldUsername);
        if (legacyUser.isPresent()) {
            User user = legacyUser.get();
            user.setUsername(newUsername);
            user.setEmail(email);
            user.setRoles(roles);
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return;
        }

        User user = new User();
        user.setUsername(newUsername);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setRoles(roles);
        userRepository.save(user);
    }

    private void synchronizeRoomData(RoomRepository roomRepository) {
        List<Room> rooms = roomRepository.findAll();
        List<Room> changedRooms = new ArrayList<>();

        for (Room room : rooms) {
            boolean changed = false;
            String normalizedType = normalizeRoomType(room.getType());
            if (!normalizedType.equals(room.getType())) { room.setType(normalizedType); changed = true; }
            String normalizedStatus = normalizeRoomStatus(room.getStatus());
            if (!normalizedStatus.equals(room.getStatus())) { room.setStatus(normalizedStatus); changed = true; }
            if (room.getCapacity() == null || room.getCapacity() < 1) { room.setCapacity(room.getEffectiveCapacity()); changed = true; }
            if (room.getDescription() == null || room.getDescription().isBlank()) { room.setDescription(room.getResolvedDescription()); changed = true; }
            if (room.getImageUrl() == null || room.getImageUrl().isBlank()) { room.setImageUrl(room.getPrimaryImageUrl()); changed = true; }
            if (room.getAmenities() == null || room.getAmenities().isBlank()) { room.setAmenities(String.join("\n", room.getAmenityList())); changed = true; }
            if (changed) changedRooms.add(room);
        }

        if (!changedRooms.isEmpty()) roomRepository.saveAll(changedRooms);
    }

    private void synchronizeBookings(BookingRepository bookingRepository) {
        List<Booking> bookings = bookingRepository.findAll();
        List<Booking> changedBookings = new ArrayList<>();

        for (Booking booking : bookings) {
            boolean changed = false;
            String normalizedStatus = normalizeBookingStatus(booking.getStatus());
            if (!normalizedStatus.equals(booking.getStatus())) { booking.setStatus(normalizedStatus); changed = true; }
            if (booking.getBookingCode() == null || booking.getBookingCode().isBlank()) { booking.setBookingCode(generateLegacyBookingCode(booking)); changed = true; }
            if (booking.getGuestCount() == null || booking.getGuestCount() < 1) { booking.setGuestCount(booking.getResolvedGuestCount()); changed = true; }
            if (booking.getUnitPrice() == null || booking.getUnitPrice() <= 0) { booking.setUnitPrice(booking.getResolvedUnitPrice()); changed = true; }
            if (booking.getTotalPrice() == null || booking.getTotalPrice() <= 0) { booking.setTotalPrice(booking.getNightCount() * booking.getResolvedUnitPrice()); changed = true; }
            if (booking.getCreatedAt() == null) { booking.setCreatedAt(LocalDateTime.now()); changed = true; }
            if (booking.getUpdatedAt() == null) { booking.setUpdatedAt(booking.getCreatedAt()); changed = true; }
            if (changed) changedBookings.add(booking);
        }

        if (!changedBookings.isEmpty()) bookingRepository.saveAll(changedBookings);
    }

    private Set<String> normalizeRoles(Set<String> currentRoles) {
        if (currentRoles == null || currentRoles.isEmpty()) return Set.of("ROLE_KHACH_HANG");
        Set<String> normalizedRoles = new LinkedHashSet<>();
        for (String role : currentRoles) {
            if (role == null || role.isBlank()) continue;
            String normalizedRole = switch (role.trim().toUpperCase()) {
                case "ROLE_STAFF", "ROLE_NHAN_VIEN" -> "ROLE_NHAN_VIEN";
                case "ROLE_CUSTOMER", "ROLE_KHACH_HANG" -> "ROLE_KHACH_HANG";
                default -> role.trim().toUpperCase();
            };
            normalizedRoles.add(normalizedRole);
        }
        return normalizedRoles.isEmpty() ? Set.of("ROLE_KHACH_HANG") : normalizedRoles;
    }

    private String normalizeRoomStatus(String status) {
        if (status == null) return "Trống";
        return switch (status) {
            case "Available" -> "Trống";
            case "Occupied" -> "Đang sử dụng";
            case "Maintenance" -> "Bảo trì";
            case "Inactive" -> "Ngừng khai thác";
            default -> status;
        };
    }

    private String normalizeBookingStatus(String status) {
        if (status == null) return "Chờ xác nhận";
        return switch (status) {
            case "Pending" -> "Chờ xác nhận";
            case "Confirmed" -> "Đã xác nhận";
            case "Cancelled" -> "Đã hủy";
            case "Completed", "Hoàn tất" -> "Đã check-out";
            default -> status;
        };
    }

    private String normalizeRoomType(String type) {
        if (type == null) return "Đơn";
        return switch (type) {
            case "Single" -> "Đơn";
            case "Double" -> "Đôi";
            case "Suite" -> "Hạng sang";
            default -> type;
        };
    }

    private String generateLegacyBookingCode(Booking booking) {
        return "BK-LEGACY-" + (booking.getId() != null ? booking.getId() : System.currentTimeMillis());
    }
}
