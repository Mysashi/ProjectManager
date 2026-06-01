package com.project.project.domain.impl.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expires}")
    private Long jwtExpiresMinutes;

    private Claims claims;

    public String generateToken(String username, HttpServletResponse response) {
        String jwt = Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiresMinutes * 60 * 1000))
                .signWith(getSignInKey())
                .compact();

        Cookie cookie = new Cookie("JWT", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
        return jwt;
    }

    public String getJwtFromCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, "JWT");
        if(cookie != null){
            return cookie.getValue();
        }
        return null;

    }
    public Claims validateToken(String token) throws JwtException {

        try {
            claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims;

        } catch(JwtException e){
            throw new JwtException(e.getMessage());
        }
    }
    public void removeTokenFromCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("JWT", null);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    private SecretKey getSignInKey() {
//        SignatureAlgorithm.HS256, this.secret
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = validateToken(token); // Используем твой метод валидации
        String username = extractUsername(claims);
        return new UsernamePasswordAuthenticationToken(username, claims, new ArrayList<>());
    }

    public String extractUsername(Claims claims) {
        return claims.getSubject();
    }

}
