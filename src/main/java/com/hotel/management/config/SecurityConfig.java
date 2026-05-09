package com.hotel.management.config;

<<<<<<< HEAD
import com.hotel.management.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
=======
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
<<<<<<< HEAD
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
=======
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
<<<<<<< HEAD
    public DaoAuthenticationProvider authenticationProvider(UserService userService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
            .authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/", "/dang-nhap", "/dang-ky", "/quen-mat-khau", "/h2-console/**", "/loi/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/khach-hang/phong/**", "/khach-hang/dat-phong/**").permitAll()
                .requestMatchers("/quan-tri/**").hasRole("NHAN_VIEN")
                .requestMatchers("/khach-hang/**").hasAnyRole("KHACH_HANG", "NHAN_VIEN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/dang-nhap")
                .loginProcessingUrl("/dang-nhap")
                .failureUrl("/dang-nhap?dangNhapSai")
                .defaultSuccessUrl("/bang-dieu-khien", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/dang-xuat", "POST"))
                .logoutSuccessUrl("/dang-nhap?daDangXuat")
                .permitAll()
            )
            .exceptionHandling(exception -> exception.accessDeniedPage("/loi/403"))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
=======
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/admin/**").hasRole("STAFF")
                .requestMatchers("/customer/**").hasAnyRole("CUSTOMER", "STAFF")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> da444c50eedca965c53767edb4158b0605b15cfb
