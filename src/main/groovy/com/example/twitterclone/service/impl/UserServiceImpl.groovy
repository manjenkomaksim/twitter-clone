package com.example.twitterclone.service.impl

import com.example.twitterclone.domain.model.User
import com.example.twitterclone.domain.repository.UserRepository
import com.example.twitterclone.dto.request.UpdateUserRequest
import com.example.twitterclone.service.api.UserService
import groovy.util.logging.Slf4j
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.webjars.NotFoundException

@Slf4j
@Service
class UserServiceImpl implements UserService {

    final UserRepository userRepository
    final PasswordEncoder passwordEncoder

    UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository
        this.passwordEncoder = passwordEncoder
    }

    @Override
    User getCurrent() {
        def userId = getCurrentUserId()
        log.info("Fetching current user with ID: {}", userId)
        userRepository.findById(userId).orElseThrow { new NotFoundException("User not found") }
    }

    @Override
    User getById(String id) {
        log.info("Fetching user by ID: {}", id)
        userRepository.findById(id).orElseThrow { new NotFoundException("User not found") }
    }

    @Override
    User updateUserProfile(UpdateUserRequest request) {
        def userId = getCurrentUserId()
        def user = userRepository.findById(userId).orElseThrow { new NotFoundException("User not found") }

        if (request.username) {
            user.username = request.username
            log.info("Updating username for user {} to {}", userId, request.username)
        }

        if (request.password) {
            user.password = passwordEncoder.encode(request.password)
            log.info("Updating password for user {}", userId)
        }

        if (request.bio) {
            user.bio = request.bio
            log.info("Updating bio for user {} to {}", userId, request.bio)
        }

        userRepository.save(user)
    }

    @Override
    void delete() {
        def userId = getCurrentUserId()
        def user = userRepository.findById(userId).orElseThrow { new NotFoundException("User not found") }
        log.info("Deleting user account: {}", userId)
        userRepository.delete(user)
    }

    @Override
    void follow(String id) {
        def currentUserId = getCurrentUserId()
        def currentUser = userRepository.findById(currentUserId).orElseThrow { new NotFoundException("User not found") }
        def target = userRepository.findById(id).orElseThrow { new NotFoundException("Target user not found") }

        currentUser.following.add(target.id)
        target.followers.add(currentUser.id)

        log.info("User {} followed user {}", currentUserId, id)
        userRepository.saveAll([currentUser, target])
    }

    @Override
    void unfollow(String id) {
        def currentUserId = getCurrentUserId()
        def currentUser = userRepository.findById(currentUserId).orElseThrow { new NotFoundException("User not found") }
        def target = userRepository.findById(id).orElseThrow { new NotFoundException("Target user not found") }

        currentUser.following.remove(target.id)
        target.followers.remove(currentUser.id)

        log.info("User {} unfollowed user {}", currentUserId, id)
        userRepository.saveAll([currentUser, target])
    }

    private String getCurrentUserId() {
        return SecurityContextHolder.context.authentication.principal as String
    }
}