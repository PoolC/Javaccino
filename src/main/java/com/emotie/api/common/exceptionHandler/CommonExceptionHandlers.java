package com.emotie.api.common.exceptionHandler;

import com.emotie.api.auth.exception.*;
import com.emotie.api.common.exception.NotSameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class CommonExceptionHandlers {
    @ExceptionHandler({IllegalArgumentException.class, NotSameException.class})
    public ResponseEntity<Map<String, String>> BadRequestHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ExceptionHandler({UnauthenticatedException.class, WrongPasswordException.class})
    public ResponseEntity<Map<String, String>> unauthorizedHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<Map<String, String>> forbiddenHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Map<String, String>> notFoundHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ExceptionHandler({ExpiredTokenException.class, WrongTokenException.class})
    public ResponseEntity<Map<String, String>> conflictHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("message", e.getMessage()));
    }
}
