package com.example.twitterclone.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "posts")
class Post {
    @Id
    String id
    String userId
    String content
    Date timestamp = new Date()
    Set<String> likes = []
    List<Comment> comments = []
}
