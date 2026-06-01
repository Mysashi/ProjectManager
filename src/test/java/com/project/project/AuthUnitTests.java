package com.project.project;

import com.project.project.domain.dto.request.auth.RegisterRequestDto;
import com.project.project.domain.entity.User;
import com.project.project.domain.impl.UserServiceImpl;
import com.project.project.domain.repo.UserJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AuthUnitTests {

    private final UserServiceImpl userService;
    private final UserJpaRepository userJpaRepository;

    @Autowired
    public AuthUnitTests(UserServiceImpl userService, UserJpaRepository userJpaRepository) {
        this.userService = userService;
        this.userJpaRepository = userJpaRepository;
    }

    @Test
    void shouldRegisterUserSuccess() {
        String username = "testRegister";
        String password = "testRegister";
        RegisterRequestDto request = new RegisterRequestDto(username, password);
        var response = userService.register(request);
        var found = userJpaRepository.findById(response.id());
        Assertions.assertNotNull(found);
        Assertions.assertEquals(username, found.get().getUsername());
        Assertions.assertEquals(username , response.username());
    }
}
