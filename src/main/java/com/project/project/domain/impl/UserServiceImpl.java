package com.project.project.domain.impl;

import com.project.project.domain.dto.request.auth.LoginRequestDto;
import com.project.project.domain.dto.request.auth.RegisterRequestDto;
import com.project.project.domain.dto.request.update.UpdateUserRequestDto;
import com.project.project.domain.dto.response.UserResponseDto;
import com.project.project.domain.entity.Role;
import com.project.project.domain.entity.User;
import com.project.project.domain.impl.security.JwtService;
import com.project.project.domain.mapper.UserMapper;
import com.project.project.domain.repo.UserJpaRepository;
import com.project.project.domain.service.UserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserJpaRepository userRepository;
    private final UserMapper mapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    UserServiceImpl(UserMapper userMapper, UserJpaRepository userRepository, UserMapper mapper, AuthenticationManager authenticationManager, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDto register(RegisterRequestDto request) {
        var entity = mapper.registerEntity(request);
        if (userRepository.existsByUsername(entity.getUsername())) throw new EntityExistsException("User is already registered. name " + entity.getUsername());
        entity.addRole(Role.USER);
        entity.setPassword(passwordEncoder.encode(request.password()));
        var saved = userRepository.save(entity);
        log.info("User was registered! userId={}", saved.getId());
        return mapper.toResponseDto(saved);
    }

    @Override
    public UserResponseDto login(LoginRequestDto request, HttpServletResponse response) {
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);
        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        jwtService.generateToken(request.username(), response);
        var entity = mapper.loginEntity(request);
        User found = userRepository.findByUsername(entity.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User was not found in database"));

        return mapper.toResponseDto(found);
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

    public void logoutUser(HttpServletResponse response){
        jwtService.removeTokenFromCookie(response);
    }

}
