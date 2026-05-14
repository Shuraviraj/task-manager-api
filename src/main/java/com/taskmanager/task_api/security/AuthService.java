package com.taskmanager.task_api.security;

import com.taskmanager.task_api.entity.AppUser;
import com.taskmanager.task_api.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // inside the class, before constructor
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Register new user
    // Checks duplicate username, hashes password, saves to DB
    public String register(String username, String password, String role) {
        log.info("Registering new user: {}", username);
        Optional<AppUser> appUserEntity = userRepository.findByUsername(username);
        if (appUserEntity.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username already exists");
        }
        AppUser user = AppUser.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", username);
        return "user registered successfully with username: " + username;
    }

    // Login — verify credentials, return JWT token
    public String login(String username, String password) {
        log.info("Login attempt for user: {}", username);
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        log.info("Login successful for user: {}", username);
        return jwtUtil.generateToken(username, user.getRole());
    }
}
