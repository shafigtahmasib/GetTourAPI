package com.example.gettour_api.exceptions;

public class EmailIsNotValidException extends RuntimeException{
    public EmailIsNotValidException(String message){
        super(message);
    }
}
