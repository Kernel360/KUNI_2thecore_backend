package com.example.common.exception;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException(String message){
        super(message);
    }
}
