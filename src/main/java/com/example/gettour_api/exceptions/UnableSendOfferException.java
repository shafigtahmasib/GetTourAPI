package com.example.gettour_api.exceptions;

public class UnableSendOfferException extends RuntimeException{
    public UnableSendOfferException(String message){
        super(message);
    }
}
