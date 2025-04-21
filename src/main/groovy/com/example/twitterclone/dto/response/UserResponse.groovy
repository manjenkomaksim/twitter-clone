package com.example.twitterclone.dto.response

class UserResponse {
    String id
    String username
    String email

    UserResponse(String id, String username, String email) {
        this.id = id
        this.username = username
        this.email = email
    }
}
