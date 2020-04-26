package com.dante.chat.commons;

public class TopicsUtils {

    public static String getUserChatTopic(Long userId){
        return "chat_topic_"+userId;
    }
}
