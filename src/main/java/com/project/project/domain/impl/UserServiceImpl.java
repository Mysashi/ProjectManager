package com.project.project.domain.impl;

import com.project.project.domain.dto.request.auth.LoginRequestDto;
import com.project.project.domain.dto.request.auth.RegisterRequestDto;
import com.project.project.domain.dto.request.update.UpdateUserRequestDto;
import com.project.project.domain.dto.response.RegisterResponseDto;
import com.project.project.domain.dto.response.UserResponseDto;
import com.project.project.domain.entity.RefreshToken;
import com.project.project.domain.entity.Role;
import com.project.project.domain.entity.User;
import com.project.project.domain.impl.security.JwtService;
import com.project.project.domain.mapper.UserMapper;
import com.project.project.domain.repo.RefreshTokenJpaRepository;
import com.project.project.domain.repo.UserJpaRepository;
import com.project.project.domain.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserJpaRepository userRepository;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Autowired
    UserServiceImpl(UserMapper userMapper, UserJpaRepository userRepository, UserMapper mapper, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder,
                    RefreshTokenServiceImpl refreshTokenServiceImpl,
                    RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    @Override
    public RegisterResponseDto register(RegisterRequestDto request) {
        var entity = mapper.registerEntity(request);
        if (userRepository.existsByUsername(entity.getUsername())) throw new EntityExistsException("User is already registered. name " + entity.getUsername());
        entity.addRole(Role.USER);
        entity.setPassword(passwordEncoder.encode(request.password()));
        var saved = userRepository.save(entity);
        log.info("User was registered! userId={}", saved.getId());
        return mapper.toRegisterResponseDto(saved);
    }

    @Override
    public UserResponseDto login(LoginRequestDto request, HttpServletResponse response) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        var accessToken = jwtService.generateToken(request.username(), response);

        var entity = mapper.loginEntity(request);
        User found = findUserByUsername(entity.getUsername());
        var refreshToken = refreshTokenServiceImpl.createRefreshToken(found.getId());
        found.setRefreshToken(refreshToken);
        found.setAccessToken(accessToken);
        return mapper.toResponseDto(found);
    }

    @Override
    public UserResponseDto getCurrentUser(UserDetails userDetails) {
        return userMapper.toResponseDto(findUserByUsername(userDetails.getUsername()));
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User was not found in database"));
    }

    @Override
    public UserResponseDto delete(Long id) {
        var found = findUser(id);
        userRepository.delete(found);
        log.info("User was deleted, userId={}", found.getId());

        return mapper.toResponseDto(found);
    }

    @Override
    public UserResponseDto getOneOrElseThrow(Long id) {

        return mapper.toResponseDto(findUser(id));
    }

    @Override
    public UserResponseDto update(UpdateUserRequestDto request) {
        var found = findUser(request.id());
        var entity = mapper.updateEntityFromDto(request, found);
        var saved = userRepository.save(entity);
        log.info("User was updated!, userId={}", saved.getId());

        return mapper.toResponseDto(saved);
    }

    public User findUser(Long id) {
        var found = userRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        log.info("User was found! id={}", id);

        return found.get();
    }

    public ResponseEntity<String> logoutUser(String requestToken, HttpServletResponse response){
        if (requestToken == null || requestToken.isBlank()) {
            return ResponseEntity.badRequest().body("Refresh token is required.");
        }
        jwtService.removeTokenFromCookie(response);
        return refreshTokenJpaRepository.findByToken(requestToken)
                .map(token -> {
                    refreshTokenJpaRepository.delete(token);
                    return ResponseEntity.ok("Logged out successfully.");
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
    }

    public ResponseEntity<?> refreshToken(Map<String, String> payload, UserDetails userDetails) {
        String requestToken = payload.get("refreshToken");
        return refreshTokenJpaRepository.findByToken(requestToken)
                .map(token -> {
                    if (refreshTokenServiceImpl.isTokenExpired(token)) {
                        refreshTokenJpaRepository.delete(token);
                        return ResponseEntity.badRequest().body("Refresh token expired. Please login again.");
                    }
                    RefreshToken newJwt = refreshTokenServiceImpl.
                            createRefreshToken(getCurrentUser(userDetails).id());
                    return ResponseEntity.ok(Map.of("token", newJwt.getToken()));
                })
                .orElse(ResponseEntity.badRequest().body("Invalid refresh token."));
    }

}
