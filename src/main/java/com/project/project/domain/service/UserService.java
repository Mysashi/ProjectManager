package com.project.project.domain.service;

import com.project.project.domain.dto.request.auth.LoginRequestDto;
import com.project.project.domain.dto.request.auth.RegisterRequestDto;
import com.project.project.domain.dto.request.update.UpdateUserRequestDto;
import com.project.project.domain.dto.response.RegisterResponseDto;
import com.project.project.domain.dto.response.UserResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {

    RegisterResponseDto register(RegisterRequestDto request);

    UserResponseDto getCurrentUser(UserDetails userDetails);

    UserResponseDto delete(Long id);

    UserResponseDto getOneOrElseThrow(Long id);

    UserResponseDto update(UpdateUserRequestDto request);

    UserResponseDto login(LoginRequestDto request, HttpServletResponse response);

}
