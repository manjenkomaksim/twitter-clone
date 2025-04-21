package com.example.twitterclone.service.impl

import com.example.twitterclone.domain.model.Comment
import com.example.twitterclone.domain.model.Post
import com.example.twitterclone.domain.repository.PostRepository
import com.example.twitterclone.domain.repository.UserRepository
import com.example.twitterclone.service.api.PostService
import groovy.util.logging.Slf4j
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Slf4j
@Service
class PostServiceImpl implements PostService {

    final PostRepository postRepository
    final UserRepository userRepository
    final SimpMessagingTemplate messagingTemplate

    PostServiceImpl(PostRepository postRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.postRepository = postRepository
        this.userRepository = userRepository
        this.messagingTemplate = messagingTemplate
    }

    @Override
    Post create(String content) {
        def userId = getCurrentUserId()
        log.info("Creating post for user: {}", userId)

        def post = new Post(userId: userId, content: content)
        def saved = postRepository.save(post)

        def user = userRepository.findById(userId).orElseThrow { new NotFoundException("User not found") }
        user.followers.each { followerId ->
            log.info("Sending post to follower over WebSocket: {}", followerId)
            messagingTemplate.convertAndSend("/topic/feed/" + followerId, saved) // 🛠️ Cast GString -> String
        }

        return saved
    }

    @Override
    Post update(String postId, String newContent) {
        def userId = getCurrentUserId()
        def post = postRepository.findById(postId).orElseThrow { new NotFoundException("Post not found") }
        if (post.userId != userId) throw new RuntimeException("You can update only your own posts")

        log.info("Updating post {} by user {}", postId, userId)
        post.content = newContent
        postRepository.save(post)
    }

    @Override
    void delete(String postId) {
        def userId = getCurrentUserId()
        def post = postRepository.findById(postId).orElseThrow { new NotFoundException("Post not found") }
        if (post.userId != userId) throw new RuntimeException("You can delete only your own posts")

        log.info("Deleting post {} by user {}", postId, userId)
        postRepository.delete(post)
    }

    @Override
    Post like(String postId) {
        def userId = getCurrentUserId()
        def post = postRepository.findById(postId).orElseThrow { new NotFoundException("Post not found") }

        post.likes.add(userId)
        log.info("User {} liked post {}", userId, postId)
        postRepository.save(post)
    }

    @Override
    Post unlike(String postId) {
        def userId = getCurrentUserId()
        def post = postRepository.findById(postId).orElseThrow { new NotFoundException("Post not found") }

        post.likes.remove(userId)
        log.info("User {} unliked post {}", userId, postId)
        postRepository.save(post)
    }

    @Override
    Post comment(String postId, String content) {
        def userId = getCurrentUserId()
        def post = postRepository.findById(postId).orElseThrow { new NotFoundException("Post not found") }

        def comment = new Comment(userId: userId, content: content)
        post.comments.add(comment)
        log.info("User {} commented on post {}: {}", userId, postId, content)
        postRepository.save(post)
    }

    @Override
    List<Post> getUserFeed() {
        def userId = getCurrentUserId()
        def user = userRepository.findById(userId).orElseThrow { new NotFoundException("User not found") }

        log.info("Retrieving feed for user {}", userId)
        postRepository.findAllByUserIdInOrderByTimestampDesc(user.following.toList())
    }

    @Override
    List<Post> getUserPosts(String userId) {
        log.info("Retrieving posts for user {}", userId)
        postRepository.findAllByUserIdOrderByTimestampDesc(userId)
    }

    @Override
    List<Comment> getPostComments(String postId) {
        def post = postRepository.findById(postId).orElseThrow { new NotFoundException("Post not found") }
        log.info("Retrieving comments for post {}", postId)
        post.comments
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.context.authentication.principal as String
    }
}
