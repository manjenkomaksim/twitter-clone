package com.example.twitterclone.service.api

import com.example.twitterclone.domain.model.User

interface AuthService {

    User register(String username, String email, String password)

    String login(String email, String password)
}