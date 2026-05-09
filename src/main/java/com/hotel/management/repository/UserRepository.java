package com.hotel.management.repository;

import com.hotel.management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    /** Đếm khách hàng trực tiếp trong DB (tối ưu hơn lấy tất cả về Java filter). */
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r = :role")
    long countByRole(@Param("role") String role);

    /** Tìm tất cả user có role cụ thể. */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r = :role ORDER BY u.id DESC")
    List<User> findAllByRole(@Param("role") String role);
}