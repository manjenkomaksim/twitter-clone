package com.example.twitterclone.service

import com.example.twitterclone.domain.model.User
import com.example.twitterclone.domain.repository.UserRepository
import com.example.twitterclone.service.impl.AuthServiceImpl
import com.example.twitterclone.service.security.JwtServiceImpl
import spock.lang.Specification

class AuthServiceImplSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def jwtService = Mock(JwtServiceImpl)
    def service = new AuthServiceImpl(userRepository, jwtService)

    def "registers a new user with encoded password"() {
        given:
        userRepository.findByEmail("test@mail.com") >> Optional.empty()
        userRepository.save(_) >> { User u -> u }

        when:
        def user = service.register("test", "test@mail.com", "password123")

        then:
        user.username == "test"
        user.email == "test@mail.com"
        user.password != "password123"
    }

    def "fails to register if email already used"() {
        given:
        userRepository.findByEmail("test@mail.com") >> Optional.of(new User())

        when:
        service.register("x", "test@mail.com", "p")

        then:
        thrown(RuntimeException)
    }

    def "logs in valid user and returns token"() {
        given:
        def encoded = service.passwordEncoder.encode("secret")
        def user = new User(email: "a@b.com", password: encoded, id: "123")

        userRepository.findByEmail("a@b.com") >> Optional.of(user)
        jwtService.generateToken("123") >> "mock.token"

        when:
        def token = service.login("a@b.com", "secret")

        then:
        token == "mock.token"
    }

    def "login fails with wrong password"() {
        given:
        def encoded = service.passwordEncoder.encode("secret")
        def user = new User(email: "a@b.com", password: encoded)
        userRepository.findByEmail("a@b.com") >> Optional.of(user)

        when:
        service.login("a@b.com", "wrong")

        then:
        thrown(RuntimeException)
    }
}