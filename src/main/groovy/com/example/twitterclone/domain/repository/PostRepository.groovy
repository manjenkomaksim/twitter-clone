package com.example.twitterclone.domain.repository

import com.example.twitterclone.domain.model.Post
import org.springframework.data.mongodb.repository.MongoRepository

interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findAllByUserIdInOrderByTimestampDesc(List<String> userIds)
    List<Post> findAllByUserIdOrderByTimestampDesc(String userId)
}
