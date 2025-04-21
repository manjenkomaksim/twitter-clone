package com.example.twitterclone.service.api

import com.example.twitterclone.domain.model.Post
import com.example.twitterclone.domain.model.Comment

interface PostService {

    Post create(String content)

    Post update(String postId, String newContent)

    void delete(String postId)

    Post like(String postId)

    Post unlike(String postId)

    Post comment(String postId, String content)

    List<Post> getUserFeed()

    List<Post> getUserPosts(String userId)

    List<Comment> getPostComments(String postId)
}