package com.example.twitterclone.service

import com.example.twitterclone.domain.model.Post
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

@Service
class FeedWebSocketService {

    private final SimpMessagingTemplate messagingTemplate

    FeedWebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate
    }

    void broadcastToFollowers(Post post, List<String> followerIds) {
        followerIds.each { followerId ->
            messagingTemplate.convertAndSend("/topic/feed/${followerId}", post)
        }
    }
}
