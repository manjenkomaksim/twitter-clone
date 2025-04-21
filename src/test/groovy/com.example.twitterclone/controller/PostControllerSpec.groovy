package com.example.twitterclone.controller

import com.example.twitterclone.domain.model.Post
import com.example.twitterclone.domain.model.User
import com.example.twitterclone.domain.repository.PostRepository
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
class PostControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @Autowired
    UserRepository userRepository

    @Autowired
    PostRepository postRepository

    @Autowired
    JwtService jwtService

    def setup() {
        userRepository.deleteAll()
        postRepository.deleteAll()
    }

    def "should like and unlike post"() {
        given:
        def user = userRepository.save(new User(id: "u1", username: "maks", email: "m@x.com", password: "p"))
        def token = jwtService.generateToken(user.id)

        def postEntity = postRepository.save(new Post(id: "p1", content: "hello", userId: user.id))

        expect:
        mockMvc.perform(post("/posts/${postEntity.id}/like")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())

        mockMvc.perform(post("/posts/${postEntity.id}/unlike")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())
    }

    def "should update and delete post"() {
        given:
        def user = userRepository.save(new User(id: "u2", username: "kate", email: "k@x.com", password: "p"))
        def token = jwtService.generateToken(user.id)
        def postEntity = postRepository.save(new Post(id: "p2", content: "initial", userId: user.id))

        def updateBody = '{"content": "newContent"}'

        expect:
        mockMvc.perform(put("/posts/${postEntity.id}")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.content').value("newContent"))

        mockMvc.perform(delete("/posts/${postEntity.id}")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())
    }

    def "should comment and get comments"() {
        given:
        def user = userRepository.save(new User(id: "u3", username: "alex", email: "a@x.com", password: "p"))
        def token = jwtService.generateToken(user.id)
        def postEntity = postRepository.save(new Post(id: "p3", content: "post", userId: user.id))

        def commentBody = '{"content": "Hello"}'

        expect:
        mockMvc.perform(post("/posts/${postEntity.id}/comment")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentBody))
                .andExpect(status().isOk())

        mockMvc.perform(get("/posts/${postEntity.id}/comments")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$[0].content').value("Hello"))
    }

    def "should get user feed and user posts"() {
        given:
        def user = userRepository.save(new User(id: "u4", username: "bob", email: "b@x.com", password: "p"))
        def token = jwtService.generateToken(user.id)
        postRepository.save(new Post(userId: user.id, content: "a"))
        postRepository.save(new Post(userId: user.id, content: "b"))

        expect:
        mockMvc.perform(get("/posts/feed")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())

        mockMvc.perform(get("/posts/${user.id}/feed")
                .header("Authorization", "Bearer $token"))
                .andExpect(status().isOk())
    }
}
