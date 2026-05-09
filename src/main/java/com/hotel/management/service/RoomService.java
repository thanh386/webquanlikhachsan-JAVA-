package com.hotel.management.service;

import com.hotel.management.entity.Room;
<<<<<<< HEAD
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.util.DebugRuntimeLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@Service
public class RoomService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

    private static final String AVAILABLE_STATUS = "Trống";
    private static final String OCCUPIED_STATUS = "Đang sử dụng";
    private static final String MAINTENANCE_STATUS = "Bảo trì";
    private static final String INACTIVE_STATUS = "Ngừng khai thác";

    private static final Set<String> VALID_STATUSES = Set.of(
        AVAILABLE_STATUS, OCCUPIED_STATUS, MAINTENANCE_STATUS, INACTIVE_STATUS
    );
=======
import com.hotel.management.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RoomService {
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb

    @Autowired
    private RoomRepository roomRepository;

<<<<<<< HEAD
    @Autowired
    private BookingRepository bookingRepository;

=======
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

<<<<<<< HEAD
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy phòng"));
    }

    public Room saveRoom(Room room) {
        if (room.getRoomNumber() == null || room.getRoomNumber().isBlank()) {
            throw new IllegalArgumentException("Số phòng là bắt buộc");
        }
        if (room.getType() == null || room.getType().isBlank()) {
            throw new IllegalArgumentException("Loại phòng là bắt buộc");
        }
        if (room.getPrice() <= 0) {
            throw new IllegalArgumentException("Giá phòng phải lớn hơn 0");
        }

        String normalizedRoomNumber = room.getRoomNumber().trim();
        roomRepository.findByRoomNumber(normalizedRoomNumber)
            .filter(existingRoom -> room.getId() == null || !existingRoom.getId().equals(room.getId()))
            .ifPresent(existingRoom -> {
                throw new IllegalArgumentException("Số phòng đã tồn tại trong hệ thống");
            });

        room.setRoomNumber(normalizedRoomNumber);
        if (room.getStatus() == null || room.getStatus().isBlank()) {
            room.setStatus(AVAILABLE_STATUS);
        }
        if (room.getCapacity() == null || room.getCapacity() < 1) {
            room.setCapacity(room.getEffectiveCapacity());
        }
        if (room.getDescription() == null || room.getDescription().isBlank()) {
            room.setDescription(room.getResolvedDescription());
        }
        if (room.getImageUrl() == null || room.getImageUrl().isBlank()) {
            room.setImageUrl(room.getPrimaryImageUrl());
        }
        if (room.getAmenities() == null || room.getAmenities().isBlank()) {
            room.setAmenities(String.join("\n", room.getAmenityList()));
        }
=======
    public Room saveRoom(Room room) {
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
<<<<<<< HEAD
        if (bookingRepository.existsByRoomId(id)) {
            throw new IllegalStateException("Không thể xóa phòng đã có lịch sử đặt phòng");
        }
        roomRepository.deleteById(id);
    }

    /**
     * Phòng "Trống" theo status vật lý — dùng cho trang chủ khi không có ngày tìm kiếm.
     */
    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus(AVAILABLE_STATUS);
    }

    /**
     * Phòng có thể đặt trong khoảng ngày [checkIn, checkOut).
     * Loại trừ phòng đã có booking active chồng lịch.
     * Đây là logic tìm phòng đúng với nghiệp vụ thực tế.
     *
     * @param checkIn ngày nhận phòng (inclusive)
     * @param checkOut ngày trả phòng (exclusive)
     * @param guestCount số khách (null = bỏ qua lọc sức chứa)
     */
    public List<Room> getAvailableRoomsByDate(LocalDate checkIn, LocalDate checkOut, Integer guestCount) {
        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            return getAvailableRooms();
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nhận phòng không được trong quá khứ.");
        }
        List<Long> availableIds = bookingRepository.findAvailableRoomIds(checkIn, checkOut, guestCount);
        // #region agent log
        DebugRuntimeLogger.log(
            "baseline",
            "H1",
            "RoomService:getAvailableRoomsByDate",
            "Available room ids from overlap query",
            new LinkedHashMap<>() {{
                put("checkIn", String.valueOf(checkIn));
                put("checkOut", String.valueOf(checkOut));
                put("guestCount", guestCount);
                put("availableIds", availableIds);
                put("count", availableIds.size());
            }}
        );
        // #endregion
        if (availableIds.isEmpty()) {
            return List.of();
        }
        return roomRepository.findAllById(availableIds);
    }

    @Transactional
    public void updateRoomStatus(Long id, String status) {
        Room room = getRoomById(id);
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Trạng thái phòng là bắt buộc");
        }

        String normalizedStatus = status.trim();
        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new IllegalArgumentException("Trạng thái phòng không hợp lệ: " + normalizedStatus);
        }

        if (OCCUPIED_STATUS.equals(room.getStatus()) && !OCCUPIED_STATUS.equals(normalizedStatus)) {
            // #region agent log
            DebugRuntimeLogger.log(
                "baseline",
                "H4",
                "RoomService:updateRoomStatus",
                "Blocked occupied room status change",
                new LinkedHashMap<>() {{
                    put("roomId", id);
                    put("currentStatus", room.getStatus());
                    put("requestedStatus", normalizedStatus);
                }}
            );
            // #endregion
            throw new IllegalStateException(
                "Không thể đổi trạng thái phòng đang có khách lưu trú. " +
                    "Vui lòng thực hiện check-out trước."
            );
        }

        String previousStatus = room.getStatus();
        room.setStatus(normalizedStatus);
        roomRepository.save(room);
        LOGGER.info(
            "AUDIT action=UPDATE_ROOM_STATUS roomId={} before={} after={}",
            room.getId(),
            previousStatus,
            normalizedStatus
        );
    }

    public long countByStatus(String status) {
        return roomRepository.findByStatus(status).size();
    }
}
=======
        roomRepository.deleteById(id);
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByStatus("Available");
    }
}
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
