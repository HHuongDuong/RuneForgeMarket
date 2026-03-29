package com.example.runeforgemarket.auth.service;

import com.example.runeforgemarket.auth.config.CustomUserDetailsService;
import com.example.runeforgemarket.auth.config.JwtService;
import com.example.runeforgemarket.auth.dto.AuthResponse;
import com.example.runeforgemarket.auth.dto.LoginRequest;
import com.example.runeforgemarket.auth.dto.RefreshRequest;
import com.example.runeforgemarket.auth.dto.RegisterRequest;
import com.example.runeforgemarket.user.model.Role;
import com.example.runeforgemarket.user.model.RoleName;
import com.example.runeforgemarket.user.model.User;
import com.example.runeforgemarket.user.repository.RoleRepository;
import com.example.runeforgemarket.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class authService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public authService(
        AuthenticationManager authenticationManager,
        CustomUserDetailsService userDetailsService,
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        Role role = roleRepository.findByName(RoleName.USER)
            .orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName(RoleName.USER);
                return roleRepository.save(newRole);
            });

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRoles(new HashSet<>());
        user.getRoles().add(role);

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return new AuthResponse(
            jwtService.generateAccessToken(userDetails),
            jwtService.generateRefreshToken(userDetails),
            "Bearer"
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = findUserByUsernameOrEmail(request.usernameOrEmail());

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        return new AuthResponse(
            jwtService.generateAccessToken(userDetails),
            jwtService.generateRefreshToken(userDetails),
            "Bearer"
        );
    }

    public AuthResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(token, userDetails) || !jwtService.isRefreshToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        return new AuthResponse(
            jwtService.generateAccessToken(userDetails),
            jwtService.generateRefreshToken(userDetails),
            "Bearer"
        );
    }

    private User findUserByUsernameOrEmail(String usernameOrEmail) {
        if (usernameOrEmail.contains("@")) {
            return userRepository.findByEmail(usernameOrEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        }

        return userRepository.findByUsername(usernameOrEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }
}
