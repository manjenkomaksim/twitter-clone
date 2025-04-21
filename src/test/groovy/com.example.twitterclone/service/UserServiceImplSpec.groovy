package com.example.twitterclone.service

import com.example.twitterclone.domain.model.User
import com.example.twitterclone.domain.repository.UserRepository
import com.example.twitterclone.dto.request.UpdateUserRequest
import com.example.twitterclone.service.impl.UserServiceImpl
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UserServiceImplSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def service = new UserServiceImpl(userRepository, passwordEncoder)

    def setup() {
        SecurityContextHolder.context.authentication =
                new UsernamePasswordAuthenticationToken("user123", null, [])
    }

    def "get current user from context"() {
        given:
        def user = new User(id: "user123", username: "john")
        userRepository.findById("user123") >> Optional.of(user)

        expect:
        service.getCurrent().username == "john"
    }

    def "get user by id"() {
        given:
        def user = new User(id: "u5", username: "other")
        userRepository.findById("u5") >> Optional.of(user)

        expect:
        service.getById("u5").username == "other"
    }

    def "update user bio"() {
        given:
        def user = new User(id: "user123", bio: "old")
        userRepository.findById("user123") >> Optional.of(user)
        userRepository.save(_ as User) >> { User u -> u }

        def request = new UpdateUserRequest(bio: "new bio")

        when:
        def result = service.updateUserProfile(request)

        then:
        result.bio == "new bio"
    }

    def "update username and password"() {
        given:
        def user = new User(id: "user123", username: "old", password: "oldPass")
        userRepository.findById("user123") >> Optional.of(user)
        userRepository.save(_ as User) >> { User u -> u }

        def request = new UpdateUserRequest(username: "newName", password: "newPass")

        when:
        passwordEncoder.encode("newPass") >> "encodedPass"
        def result = service.updateUserProfile(request)

        then:
        result.username == "newName"
        result.password == "encodedPass"
    }

    def "delete current user"() {
        given:
        def user = new User(id: "user123")
        userRepository.findById("user123") >> Optional.of(user)

        when:
        service.delete()

        then:
        1 * userRepository.delete(user)
    }

    def "follow another user"() {
        given:
        def follower = new User(id: "user123", following: [] as Set)
        def target = new User(id: "target456", followers: [] as Set)

        userRepository.findById("user123") >> Optional.of(follower)
        userRepository.findById("target456") >> Optional.of(target)

        when:
        service.follow("target456")

        then:
        1 * userRepository.saveAll({ it[0].following.contains("target456") && it[1].followers.contains("user123") })
    }

    def "unfollow a user"() {
        given:
        def follower = new User(id: "user123", following: ["target456"] as Set)
        def target = new User(id: "target456", followers: ["user123"] as Set)

        userRepository.findById("user123") >> Optional.of(follower)
        userRepository.findById("target456") >> Optional.of(target)

        when:
        service.unfollow("target456")

        then:
        1 * userRepository.saveAll({ !it[0].following.contains("target456") && !it[1].followers.contains("user123") })
    }
}