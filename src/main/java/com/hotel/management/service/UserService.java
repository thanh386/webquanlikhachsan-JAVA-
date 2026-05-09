package com.hotel.management.service;

import com.hotel.management.entity.User;
import com.hotel.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    public static final String CUSTOMER_ROLE = "ROLE_KHACH_HANG";
    public static final String STAFF_ROLE = "ROLE_NHAN_VIEN";

    private static final Set<String> ALLOWED_ROLES = Set.of(CUSTOMER_ROLE, STAFF_ROLE);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User saveUser(User user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập là bắt buộc");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Mật khẩu là bắt buộc");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email là bắt buộc");
        }

        user.setUsername(user.getUsername().trim());
        user.setEmail(user.getEmail().trim());
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        }

        if (user.getFullName() != null) {
            user.setFullName(user.getFullName().trim());
        }
        if (user.getPhone() != null) {
            user.setPhone(user.getPhone().trim());
        }
        if (user.getIdCard() != null) {
            user.setIdCard(user.getIdCard().trim());
        }

        user.setRoles(normalizeRoles(user.getRoles()));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User registerCustomer(User user) {
        user.setRoles(Collections.singleton(CUSTOMER_ROLE));
        return saveUser(user);
    }

    @Transactional
    public User createStaffAccount(String username, String password, String email, String fullName, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRoles(Collections.singleton(STAFF_ROLE));
        return saveUser(user);
    }

    @Transactional
    public User updateProfile(Long userId, String fullName, String phone, String idCard) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName.trim());
        }
        if (phone != null && !phone.isBlank()) {
            user.setPhone(phone.trim());
        }
        if (idCard != null && !idCard.isBlank()) {
            user.setIdCard(idCard.trim());
        }
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getRequiredUserByUsername(String username) {
        return findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));
    }

    public long countCustomers() {
        return userRepository.countByRole(CUSTOMER_ROLE);
    }

    public List<User> getAllCustomers() {
        return userRepository.findAllByRole(CUSTOMER_ROLE);
    }

    public List<User> getAllStaff() {
        return userRepository.findAllByRole(STAFF_ROLE);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getRequiredUserByUsername(username);
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword())
            .authorities(user.getRoles().toArray(String[]::new))
            .build();
    }

    private Set<String> normalizeRoles(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.singleton(CUSTOMER_ROLE);
        }

        Set<String> normalizedRoles = new LinkedHashSet<>();
        for (String role : roles) {
            if (role == null) {
                continue;
            }
            String normalizedRole = role.trim().toUpperCase();
            if (ALLOWED_ROLES.contains(normalizedRole)) {
                normalizedRoles.add(normalizedRole);
            }
        }

        if (normalizedRoles.isEmpty()) {
            return Collections.singleton(CUSTOMER_ROLE);
        }
        return normalizedRoles;
    }

    @Transactional
    public void deleteStaffAccount(Long id, String actorUsername) {
        User targetUser = userRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

        if (!targetUser.getRoles().contains(STAFF_ROLE)) {
            throw new IllegalArgumentException("Chỉ có thể xóa tài khoản nhân viên từ màn hình này");
        }
        if (actorUsername != null && actorUsername.equals(targetUser.getUsername())) {
            throw new IllegalStateException("Không thể tự xóa tài khoản đang đăng nhập");
        }
        if (userRepository.countByRole(STAFF_ROLE) <= 1) {
            throw new IllegalStateException("Hệ thống phải luôn còn ít nhất một tài khoản nhân viên");
        }

        userRepository.delete(targetUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
