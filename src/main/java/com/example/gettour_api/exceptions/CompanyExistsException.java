package com.example.gettour_api.exceptions;

public class CompanyExistsException extends IllegalStateException{
    public CompanyExistsException(String message){
        super(message);
    }
}
