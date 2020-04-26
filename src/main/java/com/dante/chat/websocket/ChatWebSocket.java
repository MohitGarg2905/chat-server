package com.dante.chat.websocket;

import com.dante.chat.commons.UriUtils;
import com.dante.chat.commons.exceptions.UnAuthorizedException;
import com.dante.chat.users.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ChatWebSocket implements WebSocketHandler {

    private static final Logger logger = LogManager.getLogger(ChatWebSocket.class);

    private final String bootstrapServers;
    private final String applicationId;
    private final Map<String, ChatMessageConsumer> sessions;
    private final UserService userService;

    public ChatWebSocket(String applicationId, String bootstrapServers, UserService userService){
        this.applicationId = applicationId;
        this.bootstrapServers = bootstrapServers;
        this.userService = userService;
        sessions = new HashMap<>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws ExecutionException, InterruptedException {
        ChatMessageConsumer chatMessageConsumer = new ChatMessageConsumer(bootstrapServers, session, extractUserId(session.getUri()));
        sessions.put(session.getId(), chatMessageConsumer);
        logger.info("Web socket opened for session {}", session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        ChatMessageConsumer chatMessageConsumer = sessions.get(session.getId());
        if(chatMessageConsumer!=null)
            chatMessageConsumer.closeSession();
        logger.info("Web socket closed for session {}", session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    private Long extractUserId(URI uri) {
        Map<String, List<String>> queryMap = UriUtils.splitQuery(uri);
        if(queryMap!=null){
            List<String> authTokenList = queryMap.get("Authorization");
            if(authTokenList==null || authTokenList.size()!=1){
                throw new UnAuthorizedException();
            }
            return userService.validateTokenAndGetUserId(authTokenList.get(0));
        }
        throw new UnAuthorizedException();
    }
}
