package com.hotel.management;

import com.hotel.management.entity.User;
import com.hotel.management.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PublicRegistrationSecurityTests {

    private static final String USERNAME = "public-signup-staff-attempt";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.findByUsername(USERNAME).ifPresent(userRepository::delete);
    }

    @Test
    void publicRegistrationAlwaysCreatesCustomerRole() throws Exception {
        mockMvc.perform(post("/dang-ky")
                .with(csrf())
                .param("username", USERNAME)
                .param("password", "MatKhau123!")
                .param("email", "public-signup@example.com")
                .param("role", "ROLE_NHAN_VIEN")
                .param("roles", "ROLE_NHAN_VIEN"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/dang-nhap?dangKyThanhCong"));

        User savedUser = userRepository.findByUsername(USERNAME).orElseThrow();
        assertThat(savedUser.getRoles()).isEqualTo(Set.of("ROLE_KHACH_HANG"));
    }
}
