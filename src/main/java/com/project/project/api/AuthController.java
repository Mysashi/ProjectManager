package com.project.project.api;

import com.project.project.domain.dto.request.auth.LoginRequestDto;
import com.project.project.domain.dto.request.auth.RegisterRequestDto;
import com.project.project.domain.dto.request.create.CreateUserRequestDto;
import com.project.project.domain.dto.request.update.UpdateUserRequestDto;
import com.project.project.domain.dto.response.UserResponseDto;
import com.project.project.domain.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class AuthController {

    private final UserServiceImpl userService;

    @Autowired
    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponseDto registerUser(@RequestBody RegisterRequestDto request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public UserResponseDto loginUser(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        return userService.login(request, response);
    }

    @GetMapping("/{id}")
    public UserResponseDto getOne(@PathVariable Long id) {
        return userService.getOneOrElseThrow(id);
    }

    @GetMapping("/delete/{id}")
    public UserResponseDto deleteUser(@PathVariable Long id) {
        return userService.delete(id);
    }

    @PostMapping("/update")
    public UserResponseDto updateUser(@RequestBody UpdateUserRequestDto request) {
        return userService.update(request);
    }

    @GetMapping("/logout")
    public String logoutUser(HttpServletResponse response) {
        userService.logoutUser(response);
        return "Succesfully left account";
    }


}
