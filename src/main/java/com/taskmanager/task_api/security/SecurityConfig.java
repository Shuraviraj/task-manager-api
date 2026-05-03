package com.taskmanager.task_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Replaces Spring Security's default configuration with our custom rules
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // JwtAuthFilter injected by Spring — Dependency Injection
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Defines all security rules for the application
    // @Bean = Spring manages this object (can't use @Component on library classes)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF — not needed for JWT (CSRF only affects cookie auth)
                .csrf(csrf -> csrf.disable())

                // Don't store sessions — JWT carries identity, server stays stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules:
                // /auth/** = public (register, login — no token needed)
                // Everything else = must have valid JWT token
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Register our JWT filter to run before Spring's default auth filter
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Password encoder bean — BCrypt hashes passwords securely
    // @Bean used because BCryptPasswordEncoder is a library class
    // Used when registering users to hash their password before saving
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//  ADDING THIS SO THAT SWAGGER CAN ACCESS CONTROLLERS BUT USER CANT CALL IT WITHOUT TOKEN
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        // 1. Allow everyone to view the Swagger UI and API docs 📄
//                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
//                        // 2. Require authentication for every other request
//                        .anyRequest().authenticated()
//                )
//                // 3. Disable CSRF temporarily so we can easily test POST requests from Swagger
//                .csrf(csrf -> csrf.disable());
//
//        return http.build();
//    }

//    DISABLE AUTH FROM ALL API
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(auth -> auth
//                        // 🔓 Temporarily allow EVERY request through without logging in
//                        .anyRequest().permitAll()
//                )
//                .csrf(csrf -> csrf.disable());
//
//        return http.build();
//    }
}
