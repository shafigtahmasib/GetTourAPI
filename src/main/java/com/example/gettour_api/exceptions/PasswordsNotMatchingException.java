package com.example.gettour_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class PasswordsNotMatchingException extends RuntimeException{
    public PasswordsNotMatchingException(String message){
        super(message);
    }
}