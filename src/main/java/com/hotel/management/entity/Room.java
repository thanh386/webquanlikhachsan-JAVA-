package com.hotel.management.entity;

<<<<<<< HEAD
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Arrays;
import java.util.List;
=======
import jakarta.persistence.*;
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb

@Entity
@Table(name = "rooms")
public class Room {

<<<<<<< HEAD
    private static final double PRICE_MULTIPLIER = 100000d;

=======
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
<<<<<<< HEAD
    private String type;
=======
    private String type; // e.g., Single, Double, Suite
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
<<<<<<< HEAD
    private String status;

    private Integer capacity;

    @Column(length = 2000)
    private String description;

    @Column(length = 1200)
    private String imageUrl;

    @Column(length = 2000)
    private String amenities;

=======
    private String status; // Available, Occupied, Maintenance

    // Constructors, getters, setters
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    public Room() {}

    public Room(String roomNumber, String type, double price, String status) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
    }

<<<<<<< HEAD
    public Room(String roomNumber,
                String type,
                double price,
                String status,
                Integer capacity,
                String description,
                String imageUrl,
                String amenities) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.status = status;
        this.capacity = capacity;
        this.description = description;
        this.imageUrl = imageUrl;
        this.amenities = amenities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public double getPriceInVnd() {
        return price * PRICE_MULTIPLIER;
    }

    public int getEffectiveCapacity() {
        if (capacity != null && capacity > 0) {
            return capacity;
        }

        return switch (type) {
            case "Hạng sang" -> 5;
            case "Đôi" -> 4;
            default -> 2;
        };
    }

    public String getResolvedDescription() {
        if (description != null && !description.isBlank()) {
            return description;
        }

        return switch (type) {
            case "Hạng sang" ->
                "Hạng suite rộng rãi với khu tiếp khách riêng, vật liệu cao cấp và trải nghiệm lưu trú riêng tư cho các kỳ nghỉ đặc biệt.";
            case "Đôi" ->
                "Không gian đôi cân bằng giữa sự thoải mái và công năng, phù hợp cho cặp đôi hoặc gia đình nhỏ cần một kỳ nghỉ gọn gàng, ấm áp.";
            default ->
                "Phòng tiêu chuẩn tinh gọn, sáng sủa và yên tĩnh, phù hợp cho chuyến công tác hoặc kỳ nghỉ ngắn ngày cần sự tập trung.";
        };
    }

    public String getPrimaryImageUrl() {
        if (imageUrl != null && !imageUrl.isBlank()) {
            return imageUrl;
        }

        return switch (type) {
            case "Hạng sang" ->
                "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1400&q=80";
            case "Đôi" ->
                "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1400&q=80";
            default ->
                "https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=1400&q=80";
        };
    }

    public List<String> getAmenityList() {
        if (amenities != null && !amenities.isBlank()) {
            return Arrays.stream(amenities.split("\\r?\\n|\\|"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
        }

        return switch (type) {
            case "Hạng sang" -> List.of(
                "Ban công riêng hướng thành phố",
                "Bồn tắm thư giãn độc lập",
                "Minibar cao cấp trong phòng",
                "Phòng khách tách biệt sang trọng",
                "Bữa sáng phục vụ tận phòng",
                "Dịch vụ đưa đón sân bay"
            );
            case "Đôi" -> List.of(
                "Giường đôi king-size cao cấp",
                "Bàn làm việc rộng rãi",
                "TV thông minh 55 inch",
                "Phòng tắm mưa hiện đại",
                "Wifi tốc độ cao miễn phí",
                "Máy pha cà phê viên nén"
            );
            default -> List.of(
                "Giường tiêu chuẩn êm ái",
                "Két an toàn điện tử",
                "Điều hòa inverter tiết kiệm",
                "TV màn hình phẳng",
                "Wifi miễn phí toàn khu",
                "Nước uống chào mừng"
            );
        };
    }
}
=======
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
