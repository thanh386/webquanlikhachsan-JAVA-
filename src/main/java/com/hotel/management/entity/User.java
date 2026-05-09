package com.hotel.management.entity;

<<<<<<< HEAD
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

=======
import jakarta.persistence.*;
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

<<<<<<< HEAD
    /** Họ và tên đầy đủ — bắt buộc trong quy trình nhận phòng thực tế. */
    @Column(length = 150)
    private String fullName;

    /** Số điện thoại liên lạc. */
    @Column(length = 20)
    private String phone;

    /** Số CMND/CCCD/Hộ chiếu — dùng khi làm thủ tục nhận phòng. */
    @Column(length = 30)
    private String idCard;

=======
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

<<<<<<< HEAD
    // Hàm tạo, getter, setter
=======
    // Constructors, getters, setters
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    public User() {}

    public User(String username, String password, String email, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
    }

<<<<<<< HEAD
    // Getter và setter
=======
    // Getters and setters
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
<<<<<<< HEAD

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }

    /**
     * Trả về tên hiển thị: fullName nếu có, ngược lại dùng username.
     */
    public String getDisplayName() {
        return (fullName != null && !fullName.isBlank()) ? fullName.trim() : username;
    }
}
=======
}
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
