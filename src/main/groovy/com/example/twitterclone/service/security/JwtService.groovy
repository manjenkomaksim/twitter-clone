package com.example.twitterclone.service.security

interface JwtService {
    String generateToken(String userId)
    String extractUserId(String token)
}