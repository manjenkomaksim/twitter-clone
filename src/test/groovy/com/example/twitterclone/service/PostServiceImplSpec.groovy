package com.example.twitterclone.service

import com.example.twitterclone.domain.model.Comment
import com.example.twitterclone.domain.model.Post
import com.example.twitterclone.domain.model.User
import com.example.twitterclone.domain.repository.PostRepository
import com.example.twitterclone.domain.repository.UserRepository
import com.example.twitterclone.service.impl.PostServiceImpl
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class PostServiceImplSpec extends Specification {

    def postRepository = Mock(PostRepository)
    def userRepository = Mock(UserRepository)
    def messagingTemplate = Mock(SimpMessagingTemplate)
    def service = new PostServiceImpl(postRepository, userRepository, messagingTemplate)

    def setup() {
        SecurityContextHolder.context.authentication =
                new UsernamePasswordAuthenticationToken("user123", null, [])
    }

    def "create post"() {
        given:
        def user = new User(id: "user123", followers: ["follower1"] as Set)
        userRepository.findById("user123") >> Optional.of(user)
        postRepository.save(_ as Post) >> { Post p -> p }

        when:
        def post = service.create("hello world")

        then:
        1 * messagingTemplate.convertAndSend("/topic/feed/follower1", _)
        post.content == "hello world"
        post.userId == "user123"
    }

    def "update post"() {
        given:
        def post = new Post(id: "p1", userId: "user123", content: "old")
        postRepository.findById("p1") >> Optional.of(post)
        postRepository.save(_ as Post) >> { Post p -> p }

        when:
        def updated = service.update("p1", "new")

        then:
        updated.content == "new"
    }

    def "delete post"() {
        given:
        def post = new Post(id: "p1", userId: "user123")
        postRepository.findById("p1") >> Optional.of(post)

        when:
        service.delete("p1")

        then:
        1 * postRepository.delete(post)
    }

    def "like post"() {
        given:
        def post = new Post(id: "p1", likes: [] as Set)
        postRepository.findById("p1") >> Optional.of(post)
        postRepository.save(_ as Post) >> { Post p -> p }

        when:
        def liked = service.like("p1")

        then:
        liked.likes.contains("user123")
    }

    def "unlike post"() {
        given:
        def post = new Post(id: "p1", likes: ["user123"] as Set)
        postRepository.findById("p1") >> Optional.of(post)
        postRepository.save(_ as Post) >> { Post p -> p }

        when:
        def result = service.unlike("p1")

        then:
        !result.likes.contains("user123")
    }

    def "comment on post"() {
        given:
        def post = new Post(id: "p1", comments: [])
        postRepository.findById("p1") >> Optional.of(post)
        postRepository.save(_ as Post) >> { Post p -> p }

        when:
        def updated = service.comment("p1", "nice post")

        then:
        updated.comments.size() == 1
        updated.comments[0].content == "nice post"
    }

    def "get user feed"() {
        given:
        def user = new User(id: "user123", following: ["a", "b"] as Set)
        userRepository.findById("user123") >> Optional.of(user)
        postRepository.findAllByUserIdInOrderByTimestampDesc(["a", "b"]) >> []

        expect:
        service.getUserFeed().isEmpty()
    }

    def "get user posts"() {
        given:
        postRepository.findAllByUserIdOrderByTimestampDesc("u1") >> [new Post(userId: "u1")]

        expect:
        service.getUserPosts("u1").size() == 1
    }

    def "get post comments"() {
        given:
        def post = new Post(id: "p1", comments: [new Comment(userId: "u1", content: "hi")])
        postRepository.findById("p1") >> Optional.of(post)

        expect:
        service.getPostComments("p1")[0].content == "hi"
    }
}