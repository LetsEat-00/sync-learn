package com.synclearn.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * 간단한 JWT 발급기
 *
 * - 실무: secret은 환경변수나 Secret Manager에서 관리하세요.
 * - 토큰 만료, claim 추가, RS256등도 고려해야 함.
 */
@Component
public class JwtTokenProvider {

    private final Key key;
    private final long accessTokenValidityMs = 1000L * 60 * 60; // 1시간

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        // secret 길이는 충분히 길어야 함 (256-bit 이상 권장)
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String createAccessToken(String userId) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidityMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}