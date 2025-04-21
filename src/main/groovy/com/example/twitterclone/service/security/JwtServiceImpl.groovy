package com.example.twitterclone.service.security


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import javax.crypto.SecretKey

@Service
class JwtServiceImpl implements JwtService {

    private SecretKey key

    @Value('${jwt.secret}')
    String secret

    @PostConstruct
    void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes())
    }

    @Override
    String generateToken(String userId) {
        def now = new Date()
        def expiry = new Date(now.time + 1000 * 60 * 60 * 10)

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key)
                .compact()
    }

    @Override
    String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
    }
}
