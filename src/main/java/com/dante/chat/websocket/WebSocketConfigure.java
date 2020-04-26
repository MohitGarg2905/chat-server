package com.dante.chat.websocket;

import com.dante.chat.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfigure extends AbstractWebSocketHandler implements WebSocketConfigurer {

    @Value("${wondr.kafka.server.urls:localhost:9092}")
    private String bootstrapServers;
    @Value("${wondr.kafka.application.id:wondr}")
    private String applicationId;

    @Autowired
    private UserService userService;

    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocket(applicationId, bootstrapServers, userService), "/chat").setAllowedOrigins("*");
    }
}
