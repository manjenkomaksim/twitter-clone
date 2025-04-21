package com.example.twitterclone.service.impl

import com.example.twitterclone.domain.model.User
import com.example.twitterclone.domain.repository.UserRepository
import com.example.twitterclone.service.api.AuthService
import com.example.twitterclone.service.security.JwtServiceImpl
import groovy.util.logging.Slf4j
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Slf4j
@Service
class AuthServiceImpl implements AuthService {

    final UserRepository userRepository
    final JwtServiceImpl jwtService
    final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder()

    AuthServiceImpl(UserRepository userRepository, JwtServiceImpl jwtService) {
        this.userRepository = userRepository
        this.jwtService = jwtService
    }

    @Override
    User register(String username, String email, String password) {
        def encodedPassword = passwordEncoder.encode(password)
        def user = new User(username: username, email: email, password: encodedPassword)
        def savedUser = userRepository.save(user)
        log.info("Registered new user with ID: {}, username: {}", savedUser.id, savedUser.username)
        return savedUser
    }

    @Override
    String login(String email, String password) {
        def user = userRepository.findByEmail(email)
                .orElseThrow { new RuntimeException("Invalid credentials") }

        if (!passwordEncoder.matches(password, user.password)) {
            throw new RuntimeException("Invalid credentials")
        }

        def token = jwtService.generateToken(user.id)
        log.info("Generated token for user ID: {}", user.id)
        return token
    }
}
