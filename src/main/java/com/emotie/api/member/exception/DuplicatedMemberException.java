package com.emotie.api.member.exception;

public class DuplicatedMemberException extends RuntimeException{

    public DuplicatedMemberException(String message) {
        super(message);
    }
}
