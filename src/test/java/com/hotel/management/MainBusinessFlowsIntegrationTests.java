package com.hotel.management;

import com.hotel.management.entity.Booking;
import com.hotel.management.entity.Room;
import com.hotel.management.entity.User;
import com.hotel.management.repository.BookingRepository;
import com.hotel.management.repository.RoomRepository;
import com.hotel.management.repository.UserRepository;
import com.hotel.management.service.BookingService;
import com.hotel.management.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MainBusinessFlowsIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomService roomService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void forgotPasswordPageIsReachableFromLoginFlow() throws Exception {
        mockMvc.perform(get("/quen-mat-khau"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Khôi phục quyền truy cập")));
    }

    @Test
    void anonymousUsersAreRedirectedToLoginWhenSubmittingBookingRequest() throws Exception {
        long bookingCountBefore = bookingRepository.count();

        mockMvc.perform(post("/khach-hang/dat-phong")
                .with(csrf())
                .param("roomId", findRoomByNumber("101").getId().toString())
                .param("checkInDate", LocalDate.now().plusDays(5).toString())
                .param("checkOutDate", LocalDate.now().plusDays(7).toString())
                .param("guestCount", "1")
                .param("guestName", "Khach Le")
                .param("guestPhone", "0900000000"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlPattern("**/dang-nhap"));

        assertThat(bookingRepository.count()).isEqualTo(bookingCountBefore);
    }

    @Test
    void customerCanCreateAndCancelOwnBooking() throws Exception {
        Room room = findRoomByNumber("102");
        long bookingCountBefore = bookingRepository.count();

        mockMvc.perform(post("/khach-hang/dat-phong")
                .with(customerUser())
                .with(csrf())
                .param("roomId", room.getId().toString())
                .param("checkInDate", LocalDate.now().plusDays(6).toString())
                .param("checkOutDate", LocalDate.now().plusDays(8).toString())
                .param("guestCount", "2")
                .param("guestName", "Khach Dat Phong")
                .param("guestPhone", "0911222333")
                .param("specialRequests", "Tang cao"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/khach-hang/lich-su-dat-phong"));

        assertThat(bookingRepository.count()).isEqualTo(bookingCountBefore + 1);

        User customer = findUser("khachhang");
        Booking savedBooking = bookingRepository.findByUserIdOrderByCreatedAtDescIdDesc(customer.getId()).stream()
            .filter(booking -> room.getId().equals(booking.getRoom().getId()))
            .filter(booking -> "Khach Dat Phong".equals(booking.getGuestName()))
            .findFirst()
            .orElseThrow();

        assertThat(savedBooking.getStatus()).isEqualTo(Booking.STATUS_CHO_XAC_NHAN);

        mockMvc.perform(post("/khach-hang/luot-dat/{id}/huy", savedBooking.getId())
                .with(customerUser())
                .with(csrf())
                .param("cancelReason", "Thay doi lich"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/khach-hang/lich-su-dat-phong"));

        Booking cancelledBooking = bookingRepository.findById(savedBooking.getId()).orElseThrow();
        assertThat(cancelledBooking.getStatus()).isEqualTo(Booking.STATUS_DA_HUY);
        assertThat(cancelledBooking.getCancelReason()).contains("Thay doi lich");
    }

    @Test
    void customerCanUpdateProfileAndPassword() throws Exception {
        User customer = findUser("khachhang");

        mockMvc.perform(post("/khach-hang/ho-so")
                .with(customerUser())
                .with(csrf())
                .param("fullName", "Khach Hang Moi")
                .param("phone", "0988777666")
                .param("idCard", "079123456789"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/khach-hang/ho-so"));

        User updatedCustomer = findUser("khachhang");
        assertThat(updatedCustomer.getFullName()).isEqualTo("Khach Hang Moi");
        assertThat(updatedCustomer.getPhone()).isEqualTo("0988777666");
        assertThat(updatedCustomer.getIdCard()).isEqualTo("079123456789");

        mockMvc.perform(post("/khach-hang/doi-mat-khau")
                .with(customerUser())
                .with(csrf())
                .param("oldPassword", "khachhang123")
                .param("newPassword", "MatKhauMoi123"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/khach-hang/ho-so"));

        assertThat(passwordEncoder.matches("MatKhauMoi123", findUser("khachhang").getPassword())).isTrue();
    }

    @Test
    void roomManagementRejectsDuplicateRoomNumbers() {
        Room duplicateRoom = new Room();
        duplicateRoom.setRoomNumber("101");
        duplicateRoom.setType("Đôi");
        duplicateRoom.setPrice(88.0);
        duplicateRoom.setStatus("Trống");
        duplicateRoom.setCapacity(3);

        assertThatThrownBy(() -> roomService.saveRoom(duplicateRoom))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Số phòng");
    }

    @Test
    void staffManagementCannotDeleteOwnAccount() throws Exception {
        User admin = findUser("quantri");

        mockMvc.perform(post("/quan-tri/nhan-vien/{id}/xoa", admin.getId())
                .with(staffUser())
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/quan-tri/nhan-vien"));

        assertThat(userRepository.findById(admin.getId())).isPresent();
    }

    @Test
    void staffCanRunBookingLifecycleEndToEnd() throws Exception {
        User customer = findUser("khachhang");
        Room room = findRoomByNumber("201");

        Booking booking = bookingService.createBooking(
            customer,
            room.getId(),
            LocalDate.now().plusDays(9),
            LocalDate.now().plusDays(11),
            2,
            "Khach Checkin",
            "0933333333",
            "Nhan phong som"
        );

        mockMvc.perform(post("/quan-tri/luot-dat/duyet/{id}", booking.getId())
                .with(staffUser())
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/quan-tri/luot-dat"));
        assertThat(bookingRepository.findById(booking.getId()).orElseThrow().getStatus())
            .isEqualTo(Booking.STATUS_DA_XAC_NHAN);

        mockMvc.perform(post("/quan-tri/luot-dat/check-in/{id}", booking.getId())
                .with(staffUser())
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/quan-tri/luot-dat"));
        assertThat(bookingRepository.findById(booking.getId()).orElseThrow().getStatus())
            .isEqualTo(Booking.STATUS_DA_CHECK_IN);
        assertThat(roomRepository.findById(room.getId()).orElseThrow().getStatus()).isEqualTo("Đang sử dụng");

        mockMvc.perform(post("/quan-tri/luot-dat/thanh-toan/{id}", booking.getId())
                .with(staffUser())
                .with(csrf())
                .param("paymentStatus", Booking.PAYMENT_DA_THANH_TOAN))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/quan-tri/luot-dat"));
        assertThat(bookingRepository.findById(booking.getId()).orElseThrow().getPaymentStatus())
            .isEqualTo(Booking.PAYMENT_DA_THANH_TOAN);

        mockMvc.perform(post("/quan-tri/luot-dat/check-out/{id}", booking.getId())
                .with(staffUser())
                .with(csrf()))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/quan-tri/luot-dat"));
        assertThat(bookingRepository.findById(booking.getId()).orElseThrow().getStatus())
            .isEqualTo(Booking.STATUS_DA_CHECK_OUT);
        assertThat(roomRepository.findById(room.getId()).orElseThrow().getStatus()).isEqualTo("Trống");
    }

    @Test
    void paymentStatusRejectsInvalidValues() {
        User customer = findUser("khachhang");
        Room room = findRoomByNumber("202");

        Booking booking = bookingService.createBooking(
            customer,
            room.getId(),
            LocalDate.now().plusDays(12),
            LocalDate.now().plusDays(14),
            2,
            "Khach Payment",
            "0909888777",
            null
        );

        assertThatThrownBy(() -> bookingService.updatePaymentStatus(booking.getId(), "INVALID"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Trạng thái thanh toán không hợp lệ");
    }

    @Test
    void noShowCancellationAddsMarker() {
        User customer = findUser("khachhang");
        Room room = findRoomByNumber("401");

        Booking booking = bookingService.createBooking(
            customer,
            room.getId(),
            LocalDate.now().plusDays(15),
            LocalDate.now().plusDays(16),
            2,
            "Khach NoShow",
            "0911999888",
            null
        );
        bookingService.approveBooking(booking.getId());
        bookingService.cancelBooking(booking.getId(), "Khách không đến", true);

        Booking cancelled = bookingRepository.findById(booking.getId()).orElseThrow();
        assertThat(cancelled.getStatus()).isEqualTo(Booking.STATUS_DA_HUY);
        assertThat(cancelled.isNoShow()).isTrue();
    }

    private Room findRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber).orElseThrow();
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    private SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor customerUser() {
        return user("khachhang").roles("KHACH_HANG");
    }

    private SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor staffUser() {
        return user("quantri").roles("NHAN_VIEN");
    }
}
