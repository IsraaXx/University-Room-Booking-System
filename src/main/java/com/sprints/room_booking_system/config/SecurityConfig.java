package com.sprints.room_booking_system.config;

import com.sprints.room_booking_system.security.JwtAuthenticationFilter;
import com.sprints.room_booking_system.security.RestAccessDeniedHandler;
import com.sprints.room_booking_system.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint authEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(authEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/v1/auth/**").permitAll()
                    // Room endpoints: POST/PUT/DELETE admin-only, GET authenticated
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/rooms/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/rooms/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/rooms/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/rooms/**").authenticated()
                    // Booking approvals admin-only
                    .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/bookings/*/approve").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.PATCH, "/api/v1/bookings/*/reject").hasRole("ADMIN")
                    // Building endpoints: POST/PUT/DELETE admin-only, GET authenticated
                    .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/v1/buildings/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/buildings/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/buildings/**").hasRole("ADMIN")
                    .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/buildings/**").authenticated()

                    // Everything else requires authentication
                    .anyRequest().authenticated()
            )
            .authenticationProvider(daoAuthenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    

    @Bean
    public AuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
