package com.dante.chat.users.service;

import com.dante.chat.users.model.ChatMessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public interface UserService {
    Long addUser(String email, String password) throws NoSuchAlgorithmException, ExecutionException, InterruptedException;

    String login(String emailId, String password) throws NoSuchAlgorithmException;

    Long validateTokenAndGetUserId(String token);

    void sendMessage(String token, ChatMessage chatMessage) throws IOException;
}
