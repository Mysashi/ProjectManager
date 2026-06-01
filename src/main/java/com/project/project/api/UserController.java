package com.project.project.api;

import com.project.project.domain.dto.request.update.UpdateUserRequestDto;
import com.project.project.domain.dto.response.UserResponseDto;
import com.project.project.domain.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    UserController(UserServiceImpl userService) {

        this.userService = userService;
    }
    @GetMapping("/{id}")
    public UserResponseDto getOne(@PathVariable Long id) {
        return userService.getOneOrElseThrow(id);
    }

    @DeleteMapping("/delete/{id}")
    public UserResponseDto deleteUser(@PathVariable Long id) {
        return userService.delete(id);
    }

    @PostMapping("/update")
    public UserResponseDto updateUser(@RequestBody UpdateUserRequestDto request) {
        return userService.update(request);
    }

    @PostMapping("/me")
    public UserResponseDto getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getCurrentUser(userDetails);
    }
}
