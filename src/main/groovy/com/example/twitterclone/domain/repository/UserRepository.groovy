package com.example.twitterclone.domain.repository

import com.example.twitterclone.domain.model.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username)
    Optional<User> findByEmail(String email)
}
