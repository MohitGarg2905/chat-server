package com.dante.chat.commons.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnAuthorizedException extends ResponseStatusException {

    public UnAuthorizedException(){
        super(HttpStatus.UNAUTHORIZED, "UnAuthorized Exception");
    }
}
