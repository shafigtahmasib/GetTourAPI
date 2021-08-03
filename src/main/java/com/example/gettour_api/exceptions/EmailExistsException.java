package com.example.gettour_api.exceptions;

public class EmailExistsException extends RuntimeException{
    public EmailExistsException(String message){
        super(message);
    }
}
