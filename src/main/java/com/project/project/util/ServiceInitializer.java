package com.project.project.util;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.entity.User;
import com.project.project.domain.repo.ProjectJpaRepository;
import com.project.project.domain.repo.UserJpaRepository;
import com.project.project.domain.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Slf4j
@Configuration
public class ServiceInitializer {

    @Bean
    CommandLineRunner initUser(UserJpaRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user"));
            User user2 = new User();
            user2.setUsername("user2");
            user2.setPassword(passwordEncoder.encode("user2"));
            var saved = repository.save(user);
            var saved2 = repository.save(user2);
            log.info("Test user2={} was created", saved2.getId());
            log.info("Test user={} was created", saved.getId());
        };
    }
}
