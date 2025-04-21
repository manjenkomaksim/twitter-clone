package com.example.twitterclone.controller


import com.example.twitterclone.domain.model.User
import com.example.twitterclone.dto.request.UpdateUserRequest
import com.example.twitterclone.service.api.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController {

    final UserService userService

    UserController(UserService userService) {
        this.userService = userService
    }

    @GetMapping("/me")
    User getCurrent() {
        return userService.getCurrent()
    }

    @GetMapping("/{id}")
    User getUser(@PathVariable String id) {
        return userService.getById(id)
    }

    @PutMapping("/me")
    User updateUser(@RequestBody UpdateUserRequest request) {
        return userService.updateUserProfile(request)
    }

    @DeleteMapping("/me")
    void delete() {
        userService.delete()
    }

    @PostMapping("/{id}/subscribe")
    void follow(@PathVariable String id) {
        userService.follow(id)
    }

    @PostMapping("/{id}/unsubscribe")
    void unfollow(@PathVariable String id) {
        userService.unfollow(id)
    }
}
