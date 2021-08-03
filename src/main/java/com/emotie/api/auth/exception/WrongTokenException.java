package com.emotie.api.auth.exception;

public class WrongTokenException extends RuntimeException{
    public WrongTokenException(String message) {
        super(message);
    }
}
