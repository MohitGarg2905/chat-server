package com.dante.chat.users.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ChatMessage {

    private Long toUserId;
    private String message;
}
