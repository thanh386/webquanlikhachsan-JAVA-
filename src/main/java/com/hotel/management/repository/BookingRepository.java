package com.hotel.management.repository;

import com.hotel.management.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByCreatedAtDescIdDesc(Long userId);
    List<Booking> findAllByOrderByCreatedAtDescIdDesc();
    List<Booking> findByStatus(String status);
    boolean existsByRoomId(Long roomId);

    /**
     * Kiểm tra xem phòng có booking đang hoạt động (không bị hủy/check-out)
     * chồng lịch với khoảng [checkIn, checkOut) không.
     * Điều kiện overlap: newCheckIn < existingCheckOut AND newCheckOut > existingCheckIn
     */
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.room.id = :roomId
          AND b.status NOT IN ('Đã hủy', 'Đã check-out')
          AND b.checkInDate < :checkOut
          AND b.checkOutDate > :checkIn
    """)
    boolean existsOverlappingBooking(@Param("roomId") Long roomId,
                                     @Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut);

    /**
     * Kiểm tra xem phòng có booking đang hoạt động chồng lịch,
     * ngoại trừ một booking cụ thể (dùng khi chỉnh sửa booking).
     */
    @Query("""
        SELECT COUNT(b) > 0 FROM Booking b
        WHERE b.room.id = :roomId
          AND b.id <> :excludeBookingId
          AND b.status NOT IN ('Đã hủy', 'Đã check-out')
          AND b.checkInDate < :checkOut
          AND b.checkOutDate > :checkIn
    """)
    boolean existsOverlappingBookingExcluding(@Param("roomId") Long roomId,
                                               @Param("checkIn") LocalDate checkIn,
                                               @Param("checkOut") LocalDate checkOut,
                                               @Param("excludeBookingId") Long excludeBookingId);

    /**
     * Tìm phòng có sẵn trong khoảng thời gian (không có booking active chồng lịch).
     */
    @Query("""
        SELECT DISTINCT r.id FROM Room r
        WHERE r.status NOT IN ('Bảo trì', 'Ngừng khai thác')
          AND r.id NOT IN (
              SELECT b.room.id FROM Booking b
              WHERE b.status NOT IN ('Đã hủy', 'Đã check-out')
                AND b.checkInDate < :checkOut
                AND b.checkOutDate > :checkIn
          )
          AND (:guestCount IS NULL OR r.capacity >= :guestCount)
    """)
    List<Long> findAvailableRoomIds(@Param("checkIn") LocalDate checkIn,
                                     @Param("checkOut") LocalDate checkOut,
                                     @Param("guestCount") Integer guestCount);

    /** Đếm số booking theo trạng thái. */
    long countByStatus(String status);

    /** Đếm booking check-out trong tháng (cho báo cáo doanh thu). */
    @Query("""
        SELECT COUNT(b) FROM Booking b
        WHERE b.status = 'Đã check-out'
          AND MONTH(b.checkOutDate) = :month
          AND YEAR(b.checkOutDate) = :year
    """)
    long countCheckedOutByMonth(@Param("month") int month, @Param("year") int year);

    /** Tổng doanh thu các booking đã check-out (doanh thu thực). */
    @Query("""
        SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b
        WHERE b.status = 'Đã check-out'
    """)
    Double sumRevenueCheckedOut();

    /** Tổng doanh thu dự kiến (đã xác nhận + đã check-in). */
    @Query("""
        SELECT COALESCE(SUM(b.totalPrice), 0) FROM Booking b
        WHERE b.status IN ('Đã xác nhận', 'Đã check-in')
    """)
    Double sumRevenuePending();

    /** Booking đang check-in (khách đang lưu trú). */
    List<Booking> findByStatusOrderByCheckInDateAsc(String status);
}
