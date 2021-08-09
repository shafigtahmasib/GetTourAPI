package com.example.gettour_api.exceptions;

public class RequestNotFoundException extends RuntimeException{
    public RequestNotFoundException(String message){
        super(message);
    }
}

