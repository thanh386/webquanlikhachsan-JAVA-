package com.hotel.management.service;

import com.hotel.management.entity.Booking;
import com.hotel.management.entity.Room;
<<<<<<< HEAD
import com.hotel.management.entity.User;
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.util.DebugRuntimeLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class BookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);
=======
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookingService {
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

<<<<<<< HEAD
    // ── Tạo booking ─────────────────────────────────────────────────────────

    @Transactional
    public Booking createBooking(User user,
                                 Long roomId,
                                 LocalDate checkInDate,
                                 LocalDate checkOutDate,
                                 Integer guestCount) {
        return createBooking(user, roomId, checkInDate, checkOutDate, guestCount, null, null, null);
    }

    @Transactional
    public Booking createBooking(User user,
                                 Long roomId,
                                 LocalDate checkInDate,
                                 LocalDate checkOutDate,
                                 Integer guestCount,
                                 String guestName,
                                 String guestPhone,
                                 String specialRequests) {
        validateCoreBookingInput(user, roomId);
        // Kiểm tra ngày hợp lệ
        validateDates(checkInDate, checkOutDate);

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));

        // Kiểm tra phòng không bị bảo trì
        if ("Bảo trì".equals(room.getStatus()) || "Ngừng khai thác".equals(room.getStatus())) {
            throw new IllegalStateException("Phòng hiện đang bảo trì hoặc ngừng khai thác, không thể đặt.");
        }

        // ✅ Kiểm tra chồng lịch (overlap check) — đây là kiểm tra THỰC TẾ
        boolean hasOverlap = bookingRepository.existsOverlappingBooking(roomId, checkInDate, checkOutDate);
        // #region agent log
        DebugRuntimeLogger.log(
            "baseline",
            "H1",
            "BookingService:createBooking",
            "Create booking pre-check result",
            new LinkedHashMap<>() {{
                put("username", user.getUsername());
                put("roomId", roomId);
                put("roomStatus", room.getStatus());
                put("checkIn", String.valueOf(checkInDate));
                put("checkOut", String.valueOf(checkOutDate));
                put("guestCount", guestCount);
                put("hasOverlap", hasOverlap);
            }}
        );
        // #endregion
        if (hasOverlap) {
            throw new IllegalStateException(
                "Phòng đã có lịch đặt trong khoảng thời gian này. Vui lòng chọn ngày khác hoặc phòng khác."
            );
        }

        // Kiểm tra số khách
        int normalizedGuestCount = (guestCount == null || guestCount < 1) ? 1 : guestCount;
        if (normalizedGuestCount > room.getEffectiveCapacity()) {
            throw new IllegalArgumentException(
                "Số khách (" + normalizedGuestCount + ") vượt quá sức chứa của phòng (" + room.getEffectiveCapacity() + " khách)."
            );
        }

        long nights = Math.max(1, ChronoUnit.DAYS.between(checkInDate, checkOutDate));
        double unitPrice = room.getPriceInVnd();

        Booking booking = new Booking();
        booking.setBookingCode(generateBookingCode(room));
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckInDate(checkInDate);
        booking.setCheckOutDate(checkOutDate);
        booking.setStatus(Booking.STATUS_CHO_XAC_NHAN);
        booking.setGuestCount(normalizedGuestCount);
        booking.setUnitPrice(unitPrice);
        booking.setTotalPrice(nights * unitPrice);
        booking.setCancelReason(null);
        booking.setPaymentStatus(Booking.PAYMENT_CHUA_THANH_TOAN);

        // Thông tin khách lưu trú
        if (guestName != null && !guestName.isBlank()) {
            booking.setGuestName(guestName.trim());
        } else if (user.getFullName() != null && !user.getFullName().isBlank()) {
            booking.setGuestName(user.getFullName().trim());
        }
        if (guestPhone != null && !guestPhone.isBlank()) {
            booking.setGuestPhone(guestPhone.trim());
        } else if (user.getPhone() != null && !user.getPhone().isBlank()) {
            booking.setGuestPhone(user.getPhone().trim());
        }
        if (specialRequests != null && !specialRequests.isBlank()) {
            booking.setSpecialRequests(specialRequests.trim());
        }
        ensureGuestInformation(booking);

        // NOTE: Room.status KHÔNG bị thay đổi khi tạo booking.
        // Phòng chỉ chuyển "Đang sử dụng" khi khách thực sự CHECK-IN.
        Booking saved = bookingRepository.save(booking);
        logAudit("CREATE_BOOKING", saved, "system/customer");
        return saved;
    }

    // ── Truy vấn ────────────────────────────────────────────────────────────

    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserIdOrderByCreatedAtDescIdDesc(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDescIdDesc();
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lượt đặt phòng"));
    }

    public List<Booking> getCurrentlyCheckedIn() {
        return bookingRepository.findByStatusOrderByCheckInDateAsc(Booking.STATUS_DA_CHECK_IN);
    }

    // ── Xác nhận (Admin duyệt) ───────────────────────────────────────────────

    @Transactional
    public void approveBooking(Long id) {
        Booking booking = getBookingById(id);

        if (Booking.STATUS_DA_HUY.equals(booking.getStatus())) {
            throw new IllegalStateException("Không thể duyệt lượt đặt phòng đã bị hủy.");
        }
        if (Booking.STATUS_DA_XAC_NHAN.equals(booking.getStatus())
                || Booking.STATUS_DA_CHECK_IN.equals(booking.getStatus())
                || Booking.STATUS_DA_CHECK_OUT.equals(booking.getStatus())) {
            return; // Đã ở trạng thái tiến xa hơn, không cần làm gì
        }

        // Kiểm tra lại overlap trước khi duyệt
        if (bookingRepository.existsOverlappingBookingExcluding(
                booking.getRoom().getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getId())) {
            throw new IllegalStateException(
                "Không thể duyệt: phòng đã có booking khác trong cùng khoảng thời gian này."
            );
        }

        booking.setStatus(Booking.STATUS_DA_XAC_NHAN);
        // #region agent log
        DebugRuntimeLogger.log(
            "baseline",
            "H2",
            "BookingService:approveBooking",
            "Booking approved",
            new LinkedHashMap<>() {{
                put("bookingId", id);
                put("status", booking.getStatus());
                put("roomId", booking.getRoom().getId());
            }}
        );
        // #endregion
        logAudit("APPROVE_BOOKING", booking, "staff");
        bookingRepository.save(booking);
    }

    // ── Check-in (Nhân viên ghi nhận khách vào phòng) ───────────────────────

    @Transactional
    public void checkIn(Long id) {
        Booking booking = getBookingById(id);

        if (!Booking.STATUS_DA_XAC_NHAN.equals(booking.getStatus())) {
            throw new IllegalStateException(
                "Chỉ có thể check-in khi booking ở trạng thái 'Đã xác nhận'. Trạng thái hiện tại: "
                    + booking.getStatus()
            );
        }

        booking.setStatus(Booking.STATUS_DA_CHECK_IN);
        booking.setActualCheckInTime(LocalDateTime.now());

        // Cập nhật trạng thái phòng → "Đang sử dụng" khi khách THỰC SỰ vào phòng
        Room room = booking.getRoom();
        room.setStatus("Đang sử dụng");
        roomRepository.save(room);

        // #region agent log
        DebugRuntimeLogger.log(
            "baseline",
            "H2",
            "BookingService:checkIn",
            "Booking checked in and room occupied",
            new LinkedHashMap<>() {{
                put("bookingId", id);
                put("bookingStatus", booking.getStatus());
                put("roomId", room.getId());
                put("roomStatus", room.getStatus());
            }}
        );
        // #endregion
        logAudit("CHECK_IN", booking, "staff");
        bookingRepository.save(booking);
    }

    // ── Check-out (Nhân viên ghi nhận khách trả phòng) ──────────────────────

    @Transactional
    public void checkOut(Long id) {
        Booking booking = getBookingById(id);

        if (!Booking.STATUS_DA_CHECK_IN.equals(booking.getStatus())) {
            throw new IllegalStateException(
                "Chỉ có thể check-out khi booking ở trạng thái 'Đã check-in'. Trạng thái hiện tại: "
                    + booking.getStatus()
            );
        }

        booking.setStatus(Booking.STATUS_DA_CHECK_OUT);
        booking.setActualCheckOutTime(LocalDateTime.now());

        // Sau khi khách trả phòng, phòng về "Trống"
        Room room = booking.getRoom();
        room.setStatus("Trống");
        roomRepository.save(room);

        // #region agent log
        DebugRuntimeLogger.log(
            "baseline",
            "H2",
            "BookingService:checkOut",
            "Booking checked out and room released",
            new LinkedHashMap<>() {{
                put("bookingId", id);
                put("bookingStatus", booking.getStatus());
                put("roomId", room.getId());
                put("roomStatus", room.getStatus());
            }}
        );
        // #endregion
        logAudit("CHECK_OUT", booking, "staff");
        bookingRepository.save(booking);
    }

    // ── Cập nhật thanh toán ──────────────────────────────────────────────────

    @Transactional
    public void updatePaymentStatus(Long id, String paymentStatus) {
        Booking booking = getBookingById(id);
        String normalized = normalizePaymentStatus(paymentStatus);
        if (Booking.STATUS_DA_HUY.equals(booking.getStatus())) {
            throw new IllegalStateException("Không thể cập nhật thanh toán cho đơn đã hủy.");
        }
        booking.setPaymentStatus(normalized);
        logAudit("UPDATE_PAYMENT_" + normalized, booking, "staff");
        bookingRepository.save(booking);
    }

    // ── Hủy booking ─────────────────────────────────────────────────────────

    @Transactional
    public void cancelBooking(Long id) {
        cancelBooking(id, "Yêu cầu hủy đặt phòng");
    }

    @Transactional
    public void cancelBooking(Long id, String cancelReason) {
        cancelBooking(id, cancelReason, false);
    }

    @Transactional
    public void cancelBooking(Long id, String cancelReason, boolean noShow) {
        Booking booking = getBookingById(id);

        if (Booking.STATUS_DA_HUY.equals(booking.getStatus())) {
            return; // Đã hủy rồi
        }
        if (Booking.STATUS_DA_CHECK_IN.equals(booking.getStatus())) {
            throw new IllegalStateException(
                "Không thể hủy: khách đang lưu trú. Vui lòng thực hiện check-out trước."
            );
        }
        if (Booking.STATUS_DA_CHECK_OUT.equals(booking.getStatus())) {
            throw new IllegalStateException("Không thể hủy: booking đã hoàn tất.");
        }

        booking.setStatus(Booking.STATUS_DA_HUY);
        booking.setCancelReason(normalizeCancelReason(cancelReason, noShow));

        // Nếu phòng đang "Đang sử dụng" do booking này (hiếm, nhưng an toàn)
        Room room = booking.getRoom();
        if ("Đang sử dụng".equals(room.getStatus())) {
            // Chỉ giải phóng nếu không còn booking check-in nào khác cho phòng này
            boolean hasOtherCheckedIn = bookingRepository.findByStatusOrderByCheckInDateAsc(Booking.STATUS_DA_CHECK_IN)
                .stream()
                .anyMatch(b -> !b.getId().equals(id) && b.getRoom().getId().equals(room.getId()));
            if (!hasOtherCheckedIn) {
                room.setStatus("Trống");
                roomRepository.save(room);
            }
        }

        // #region agent log
        DebugRuntimeLogger.log(
            "baseline",
            "H2",
            "BookingService:cancelBooking",
            "Booking cancelled",
            new LinkedHashMap<>() {{
                put("bookingId", id);
                put("status", booking.getStatus());
                put("reason", booking.getCancelReason());
                put("roomId", room.getId());
                put("roomStatus", room.getStatus());
            }}
        );
        // #endregion
        logAudit(noShow ? "MARK_NO_SHOW" : "CANCEL_BOOKING", booking, "staff/customer");
        bookingRepository.save(booking);
    }

    // ── Thống kê ────────────────────────────────────────────────────────────

    /** Doanh thu thực: chỉ tính booking đã check-out. */
    public double getActualRevenue() {
        Double revenue = bookingRepository.sumRevenueCheckedOut();
        return revenue == null ? 0 : revenue;
    }

    /** Doanh thu dự kiến: booking đã xác nhận + đang check-in. */
    public double getExpectedRevenue() {
        Double revenue = bookingRepository.sumRevenuePending();
        return revenue == null ? 0 : revenue;
    }

    /** Số phòng đang có khách (check-in). */
    public long getOccupiedRoomCount() {
        return bookingRepository.countByStatus(Booking.STATUS_DA_CHECK_IN);
    }

    /** Số đơn đang chờ xác nhận. */
    public long getPendingBookingCount() {
        return bookingRepository.countByStatus(Booking.STATUS_CHO_XAC_NHAN);
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void validateDates(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Ngày nhận phòng và ngày trả phòng là bắt buộc.");
        }
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.equals(checkInDate)) {
            throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng ít nhất 1 đêm.");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nhận phòng không được ở trong quá khứ.");
        }
    }

    private void validateCoreBookingInput(User user, Long roomId) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Không thể tạo booking khi thiếu thông tin tài khoản.");
        }
        if (roomId == null) {
            throw new IllegalArgumentException("Phòng đặt là bắt buộc.");
        }
    }

    private void ensureGuestInformation(Booking booking) {
        if (booking.getGuestName() == null || booking.getGuestName().isBlank()) {
            booking.setGuestName(booking.getUser().getDisplayName());
        }
        if (booking.getGuestPhone() == null || booking.getGuestPhone().isBlank()) {
            String userPhone = booking.getUser().getPhone();
            if (userPhone != null && !userPhone.isBlank()) {
                booking.setGuestPhone(userPhone.trim());
            }
        }
    }

    private String generateBookingCode(Room room) {
        String prefix = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String roomToken = room.getRoomNumber() == null
            ? "ROOM"
            : room.getRoomNumber().trim().toUpperCase(Locale.ROOT);
        String randomToken = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
        return "BK-" + prefix + "-" + roomToken + "-" + randomToken;
    }

    private String normalizeCancelReason(String cancelReason, boolean noShow) {
        String normalizedReason;
        if (cancelReason == null || cancelReason.isBlank()) {
            normalizedReason = "Không có lý do được cung cấp";
        } else {
            normalizedReason = cancelReason.trim();
        }
        return noShow ? Booking.NO_SHOW_MARKER + " " + normalizedReason : normalizedReason;
    }

    private String normalizePaymentStatus(String status) {
        if (status == null || status.isBlank()) return Booking.PAYMENT_CHUA_THANH_TOAN;
        return switch (status.trim()) {
            case Booking.PAYMENT_CHUA_THANH_TOAN,
                 Booking.PAYMENT_DA_COC,
                 Booking.PAYMENT_DA_THANH_TOAN -> status.trim();
            default -> throw new IllegalArgumentException("Trạng thái thanh toán không hợp lệ: " + status);
        };
    }

    private void logAudit(String action, Booking booking, String actor) {
        LOGGER.info(
            "AUDIT action={} actor={} bookingId={} roomId={} status={} paymentStatus={} updatedAt={}",
            action,
            actor,
            booking.getId(),
            booking.getRoom() != null ? booking.getRoom().getId() : null,
            booking.getStatus(),
            booking.getPaymentStatus(),
            booking.getUpdatedAt()
        );
    }
}
=======
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
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
