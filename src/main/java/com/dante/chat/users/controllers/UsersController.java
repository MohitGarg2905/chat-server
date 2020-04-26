package com.dante.chat.users.controllers;

import com.dante.chat.users.model.ChatMessage;
import com.dante.chat.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

@RestController
public class UsersController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/user/add")
    public Long addUser(@RequestParam(name = "email") String emailId, @RequestParam(name = "password") String password) throws NoSuchAlgorithmException, ExecutionException, InterruptedException {
        return userService.addUser(emailId, password);
    }

    @PostMapping(value = "/user/login")
    public String login(@RequestParam(name = "email") String emailId, @RequestParam(name = "password") String password) throws NoSuchAlgorithmException {
        return userService.login(emailId, password);
    }

    @PostMapping(value = "/chat/send")
    public Boolean sendChatMessage(@RequestHeader("Authorization") String token, @RequestBody ChatMessage chatMessage) throws IOException {
        userService.sendMessage(token, chatMessage);
        return true;
    }

}
