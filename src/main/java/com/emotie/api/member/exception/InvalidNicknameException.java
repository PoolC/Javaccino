package com.emotie.api.member.exception;

public class InvalidNicknameException extends RuntimeException{

    public InvalidNicknameException(String message) {
        super(message);
    }
}
