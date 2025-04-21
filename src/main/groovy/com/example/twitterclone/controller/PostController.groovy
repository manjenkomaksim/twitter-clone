package com.example.twitterclone.controller

import com.example.twitterclone.domain.model.Comment
import com.example.twitterclone.domain.model.Post
import com.example.twitterclone.dto.request.CommentRequest
import com.example.twitterclone.dto.request.CreatePostRequest
import com.example.twitterclone.service.api.PostService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/posts")
class PostController {

    final PostService postService

    PostController(PostService postService) {
        this.postService = postService
    }

    @PostMapping
    Post create(@RequestBody CreatePostRequest request) {
        return postService.create(request.content)
    }

    @PutMapping("/{id}")
    Post update(@PathVariable String id, @RequestBody CreatePostRequest request) {
        return postService.update(id, request.content)
    }

    @DeleteMapping("/{id}")
    void delete(@PathVariable String id) {
        postService.delete(id)
    }

    @PostMapping("/{id}/like")
    Post like(@PathVariable String id) {
        return postService.like(id)
    }

    @PostMapping("/{id}/unlike")
    Post unlike(@PathVariable String id) {
        return postService.unlike(id)
    }

    @PostMapping("/{id}/comment")
    Post comment(@PathVariable String id, @RequestBody CommentRequest request) {
        return postService.comment(id, request.content)
    }

    @GetMapping("/feed")
    List<Post> feed() {
        return postService.getUserFeed()
    }

    @GetMapping("/{id}/feed")
    List<Post> userPosts(@PathVariable String id) {
        return postService.getUserPosts(id)
    }

    @GetMapping("/{id}/comments")
    List<Comment> comments(@PathVariable String id) {
        return postService.getPostComments(id)
    }
}
