package com.example.twitterclone.controller

import com.example.twitterclone.config.JwtAuthFilter
import com.example.twitterclone.config.SecurityBypassConfig
import com.example.twitterclone.config.SecurityConfig
import com.example.twitterclone.domain.model.User
import com.example.twitterclone.dto.request.LoginRequest
import com.example.twitterclone.dto.request.RegisterUserRequest
import com.example.twitterclone.service.api.AuthService
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import com.fasterxml.jackson.databind.ObjectMapper

import static org.mockito.Mockito.*
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

@WebMvcTest(
        controllers = AuthController,
        excludeAutoConfiguration = [SecurityConfig],
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = [JwtAuthFilter]
        )
)
@Import(SecurityBypassConfig)
class AuthControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @MockBean
    AuthService authService

    ObjectMapper objectMapper = new ObjectMapper()

    def "should register user and return JSON"() {
        given:
        def request = new RegisterUserRequest(username: "maksim", email: "maksim@mail.com", password: "qwerty")

        def savedUser = new User()
        savedUser.id = "u1"
        savedUser.username = "maksim"
        savedUser.email = "maksim@mail.com"

        when(authService.register(anyString(), anyString(), anyString())).thenReturn(savedUser)

        expect:
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.id').value("u1"))
                .andExpect(jsonPath('$.username').value("maksim"))
                .andExpect(jsonPath('$.email').value("maksim@mail.com"))
    }

    def "should return JWT token on login"() {
        given:
        def request = new LoginRequest(email: "maksim@mail.com", password: "qwerty")

        when(authService.login(anyString(), anyString())).thenReturn("mock.jwt.token")

        expect:
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.token').value("mock.jwt.token"))
    }
}