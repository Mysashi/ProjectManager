package com.project.project.domain.repo;

import com.project.project.domain.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(Long userId);

    boolean isTokenExpired(RefreshToken token);
}
