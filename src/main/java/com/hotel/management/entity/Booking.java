package com.hotel.management.entity;

<<<<<<< HEAD
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
=======
import jakarta.persistence.*;
import java.time.LocalDate;
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb

@Entity
@Table(name = "bookings")
public class Booking {

<<<<<<< HEAD
    // ── Trạng thái booking (lifecycle) ──────────────────────────────────────
    /** Khách vừa đặt, chờ nhân viên xem xét. */
    public static final String STATUS_CHO_XAC_NHAN  = "Chờ xác nhận";
    /** Nhân viên đã duyệt, chờ khách đến nhận phòng. */
    public static final String STATUS_DA_XAC_NHAN   = "Đã xác nhận";
    /** Khách đang ở trong phòng. */
    public static final String STATUS_DA_CHECK_IN   = "Đã check-in";
    /** Khách đã trả phòng — doanh thu được ghi nhận. */
    public static final String STATUS_DA_CHECK_OUT  = "Đã check-out";
    /** Đơn bị hủy (khách tự hủy hoặc nhân viên từ chối). */
    public static final String STATUS_DA_HUY        = "Đã hủy";
    /** Marker no-show: dùng trong giai đoạn MVP để phân loại lý do hủy do khách không đến. */
    public static final String NO_SHOW_MARKER = "[NO_SHOW]";

    // ── Trạng thái thanh toán ───────────────────────────────────────────────
    public static final String PAYMENT_CHUA_THANH_TOAN = "Chưa thanh toán";
    public static final String PAYMENT_DA_COC          = "Đã cọc";
    public static final String PAYMENT_DA_THANH_TOAN   = "Đã thanh toán";

=======
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(length = 64)
    private String bookingCode;

=======
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

<<<<<<< HEAD
    /** Thời điểm khách thực sự vào phòng (do nhân viên ghi nhận). */
    private LocalDateTime actualCheckInTime;

    /** Thời điểm khách thực sự trả phòng (do nhân viên ghi nhận). */
    private LocalDateTime actualCheckOutTime;

    @Column(nullable = false)
    private String status;

    private Integer guestCount;

    /**
     * Tên khách chính lưu trú (có thể khác với tài khoản đặt phòng).
     * Bắt buộc trong quy trình nhận phòng thực tế.
     */
    @Column(length = 150)
    private String guestName;

    /**
     * Số điện thoại liên lạc của khách chính.
     */
    @Column(length = 20)
    private String guestPhone;

    /**
     * Yêu cầu đặc biệt của khách (ăn kiêng, phòng không hút thuốc, tầng cao, v.v.).
     */
    @Column(length = 500)
    private String specialRequests;

    private Double unitPrice;

    private Double totalPrice;

    /**
     * Trạng thái thanh toán: Chưa thanh toán / Đã cọc / Đã thanh toán.
     */
    @Column(length = 50)
    private String paymentStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(length = 500)
    private String cancelReason;

=======
    @Column(nullable = false)
    private String status; // Confirmed, Cancelled, Completed

    // Constructors, getters, setters
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    public Booking() {}

    public Booking(User user, Room room, LocalDate checkInDate, LocalDate checkOutDate, String status) {
        this.user = user;
        this.room = room;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
    }

<<<<<<< HEAD
    // ── Getters & Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingCode() { return bookingCode; }
    public void setBookingCode(String bookingCode) { this.bookingCode = bookingCode; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }

    public LocalDateTime getActualCheckInTime() { return actualCheckInTime; }
    public void setActualCheckInTime(LocalDateTime actualCheckInTime) { this.actualCheckInTime = actualCheckInTime; }

    public LocalDateTime getActualCheckOutTime() { return actualCheckOutTime; }
    public void setActualCheckOutTime(LocalDateTime actualCheckOutTime) { this.actualCheckOutTime = actualCheckOutTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getGuestCount() { return guestCount; }
    public void setGuestCount(Integer guestCount) { this.guestCount = guestCount; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    // ── Computed helpers ───────────────────────────────────────────────────

    public long getNightCount() {
        if (checkInDate == null || checkOutDate == null) {
            return 0;
        }
        return Math.max(1, ChronoUnit.DAYS.between(checkInDate, checkOutDate));
    }

    public String getResolvedBookingCode() {
        return bookingCode != null && !bookingCode.isBlank()
            ? bookingCode
            : "BK-" + (id == null ? "PENDING" : id);
    }

    public int getResolvedGuestCount() {
        if (guestCount != null && guestCount > 0) {
            return guestCount;
        }
        return room == null ? 1 : Math.min(2, room.getEffectiveCapacity());
    }

    public double getResolvedUnitPrice() {
        if (unitPrice != null && unitPrice > 0) {
            return unitPrice;
        }
        return room == null ? 0 : room.getPriceInVnd();
    }

    public double getResolvedTotalPrice() {
        if (totalPrice != null && totalPrice > 0) {
            return totalPrice;
        }
        return getNightCount() * getResolvedUnitPrice();
    }

    /** Tên khách lưu trú hiển thị: guestName > user.fullName > user.username. */
    public String getResolvedGuestName() {
        if (guestName != null && !guestName.isBlank()) return guestName.trim();
        if (user != null) return user.getDisplayName();
        return "Khách";
    }

    /** Trạng thái thanh toán hiển thị. */
    public String getResolvedPaymentStatus() {
        return (paymentStatus != null && !paymentStatus.isBlank())
            ? paymentStatus
            : PAYMENT_CHUA_THANH_TOAN;
    }

    /** True nếu booking đang hoạt động (chưa hủy, chưa check-out). */
    public boolean isActive() {
        return !STATUS_DA_HUY.equals(status) && !STATUS_DA_CHECK_OUT.equals(status);
    }

    /** True nếu có thể check-in (đã xác nhận và chưa check-in). */
    public boolean isCheckInEligible() {
        return STATUS_DA_XAC_NHAN.equals(status);
    }

    /** True nếu có thể check-out (đang check-in). */
    public boolean isCheckOutEligible() {
        return STATUS_DA_CHECK_IN.equals(status);
    }

    /** True nếu khách có thể hủy (chưa check-in). */
    public boolean isCancellableByCustomer() {
        return STATUS_CHO_XAC_NHAN.equals(status) || STATUS_DA_XAC_NHAN.equals(status);
    }

    public boolean isNoShow() {
        return cancelReason != null && cancelReason.startsWith(NO_SHOW_MARKER);
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (paymentStatus == null || paymentStatus.isBlank()) {
            paymentStatus = PAYMENT_CHUA_THANH_TOAN;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
=======
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate = checkOutDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
