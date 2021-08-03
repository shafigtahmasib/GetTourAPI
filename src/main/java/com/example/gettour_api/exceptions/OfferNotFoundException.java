package com.example.gettour_api.exceptions;

public class OfferNotFoundException extends RuntimeException{
    public OfferNotFoundException(String message){
        super(message);
    }
}
