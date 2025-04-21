package com.example.twitterclone.controller

import com.example.twitterclone.dto.response.AuthResponse
import com.example.twitterclone.dto.request.LoginRequest
import com.example.twitterclone.dto.request.RegisterUserRequest
import com.example.twitterclone.dto.response.UserResponse
import com.example.twitterclone.service.api.AuthService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController {

    final AuthService authService

    AuthController(AuthService authService) {
        this.authService = authService
    }

    @PostMapping("/register")
    UserResponse register(@RequestBody RegisterUserRequest request) {
        def user = authService.register(request.username, request.email, request.password)
        return new UserResponse(user.id, user.username, user.email)
    }

    @PostMapping("/login")
    AuthResponse login(@RequestBody LoginRequest request) {
        def token = authService.login(request.email, request.password)
        return new AuthResponse(token)
    }
}