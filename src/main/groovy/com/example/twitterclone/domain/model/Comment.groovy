package com.example.twitterclone.domain.model

class Comment {
    String id = UUID.randomUUID().toString()
    String userId
    String content
    Date timestamp = new Date()
}
