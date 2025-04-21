package com.example.twitterclone.controller

import com.example.twitterclone.domain.model.User
import com.example.twitterclone.domain.repository.UserRepository
import com.example.twitterclone.service.security.JwtService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    UserRepository userRepository

    @Autowired
    JwtService jwtService

    def setup() {
        userRepository.deleteAll()
    }

    def "should get user by id"() {
        given:
        def user = userRepository.save(new User(id: "u1", username: "maksim", email: "maksim@mail.com", password: "123"))
        def token = jwtService.generateToken(user.id)

        expect:
        mockMvc.perform(get("/users/${user.id}")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id').value(user.id))
    }

    def "should update username, password and bio"() {
        given:
        def user = userRepository.save(new User(id: "u7", username: "original", email: "x@x.com", password: "oldpass", bio: "old bio"))
        def token = jwtService.generateToken(user.id)

        def body = '''
    {
        "username": "updatedName",
        "password": "newSecret",
        "bio": "updated bio"
    }
    '''

        expect:
        mockMvc.perform(put("/users/me")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.username').value("updatedName"))
                .andExpect(jsonPath('$.bio').value("updated bio"))
    }

    def "should delete user"() {
        given:
        def user = userRepository.save(new User(id: "u3", username: "bob", email: "b@mail.com", password: "p"))
        def token = jwtService.generateToken(user.id)

        expect:
        mockMvc.perform(delete("/users/me")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())
    }

    def "should follow and unfollow user"() {
        given:
        def follower = userRepository.save(new User(id: "u4", username: "tom", email: "t@mail.com", password: "p"))
        def target = userRepository.save(new User(id: "target123", username: "target", email: "target@mail.com", password: "p"))
        def token = jwtService.generateToken(follower.id)

        expect:
        mockMvc.perform(post("/users/${target.id}/subscribe")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())

        mockMvc.perform(post("/users/${target.id}/unsubscribe")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())
    }
}

