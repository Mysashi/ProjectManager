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
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expires}")
    private Long jwtExpiresMinutes;

    private Claims claims;

    public void generateToken(String username, HttpServletResponse response){
        String jwt = Jwts.builder()
                .subject(username) //username here is indeed the email
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
    }

    public String getJwtFromCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, "JWT");
        if(cookie != null){
            return cookie.getValue();
        }
        return null;

    }
    public void validateToken(String token) throws JwtException {

        try {
            claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();


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

    public String extractUsername() {
        return claims.getSubject();
    }

}
