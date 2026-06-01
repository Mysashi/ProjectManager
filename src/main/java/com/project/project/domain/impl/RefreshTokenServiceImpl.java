package com.project.project.domain.impl;

import com.project.project.domain.entity.RefreshToken;
import com.project.project.domain.entity.User;
import com.project.project.domain.repo.RefreshTokenJpaRepository;
import com.project.project.domain.repo.RefreshTokenService;
import com.project.project.domain.repo.UserJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenServiceImpl  implements RefreshTokenService {

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    private final UserJpaRepository userRepository;;

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;


    public RefreshTokenServiceImpl(UserJpaRepository userRepo,
                                   RefreshTokenJpaRepository refreshTokenJpaRepository) {
        this.userRepository = userRepo;
        this.refreshTokenJpaRepository = refreshTokenJpaRepository;
    }

    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<RefreshToken> existingTokenOpt = refreshTokenJpaRepository.findByUserId(userId);

        RefreshToken refreshToken;

        if (existingTokenOpt.isPresent()) {
            refreshToken = existingTokenOpt.get();
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
            user.setRefreshToken(refreshToken);
        }

        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(30, java.time.temporal.ChronoUnit.DAYS));

        return refreshTokenJpaRepository.save(refreshToken);
    }

    public boolean isTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
