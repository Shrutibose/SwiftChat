package com.swiftchat.controller;

import com.swiftchat.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // REGISTER API
    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password) {
        return service.register(username, password);
    }

    // LOGIN API
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        return service.login(username, password);
    }

    //ONLINE USERS API
    @GetMapping("/online-users")
    public List<String> onlineUsers() {
        return service.getOnlineUsers();
    }

    //USER LOGOUT API
    @PostMapping("/logout")
    public String logout(@RequestParam String username) {
        return service.logout(username);
    }
}