package com.example.twitterclone.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
class User {
    @Id
    String id
    String username
    String email
    String password
    String bio
    Set<String> followers = []
    Set<String> following = []

    String getId() { return id }
    String getUsername() { return username }
    String getEmail() { return email }
}