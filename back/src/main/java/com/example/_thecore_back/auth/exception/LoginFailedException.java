package com.example._thecore_back.auth.exception;

public class LoginFailedException extends RuntimeException{
    public LoginFailedException(String message){
        super(message);
    }
}
