package com.example.twitterclone.service.api

import com.example.twitterclone.domain.model.User
import com.example.twitterclone.dto.request.UpdateUserRequest

interface UserService {

    User getCurrent()

    User updateUserProfile(UpdateUserRequest request)

    void delete()

    void follow(String targetId)

    void unfollow(String targetId)

    User getById(String id)
}