package com.dante.chat.commons.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidCredentialsException extends ResponseStatusException {

    public InvalidCredentialsException() {
        super(HttpStatus.BAD_REQUEST, "Invalid Credentials");
    }
}
