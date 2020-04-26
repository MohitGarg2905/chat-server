package com.dante.chat.users.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class KafkaChatMessageObject {

    private Long fromUserId;
    private Long toUserId;
    private String message;
    private Date timestamp;
    private MessageType messageType;
}
