package com.emotie.api.common.exceptionHandler;

import com.emotie.api.auth.exception.*;
import com.emotie.api.common.exception.DuplicatedException;
import com.emotie.api.common.exception.NotSameException;
import com.emotie.api.diary.exception.PeekingPrivatePostException;
import com.emotie.api.emotion.exception.EmotionDeleteConflictException;
import com.emotie.api.guestbook.exception.MyselfException;
import com.emotie.api.member.exception.CannotFollowException;
import com.emotie.api.member.exception.EmotionScoreNotInitializedException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class CommonExceptionHandlers {
    @ExceptionHandler({
            IllegalArgumentException.class, NotSameException.class, MethodArgumentNotValidException.class,
            MismatchedInputException.class, RequestRejectedException.class, ConstraintViolationException.class
    })
    public ResponseEntity<Map<String, String>> BadRequestHandler(Exception e) {
        Map<String, String> errors = new HashMap<>();
        errors.put("message", e.getMessage());

        checkMethodArgumentNotValidException(e, errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler({UnauthenticatedException.class})
    public ResponseEntity<Map<String, String>> unauthorizedHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ExceptionHandler({UnauthorizedException.class, PeekingPrivatePostException.class, WrongPasswordException.class})
    public ResponseEntity<Map<String, String>> forbiddenHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Map<String, String>> notFoundHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    @ExceptionHandler({
            ExpiredTokenException.class, WrongTokenException.class, DuplicatedException.class,
            CannotFollowException.class, EmotionDeleteConflictException.class,
            IndexOutOfBoundsException.class, EmotionScoreNotInitializedException.class,
            MyselfException.class
    })
    public ResponseEntity<Map<String, String>> conflictHandler(Exception e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Collections.singletonMap("message", e.getMessage()));
    }

    private void checkMethodArgumentNotValidException(Exception e, Map<String, String> errors) {
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = (MethodArgumentNotValidException) e;
            errors.clear();
            StringBuilder sb = new StringBuilder();
            ex.getBindingResult().getAllErrors()
                    .forEach(c -> sb.append(c.getDefaultMessage()));
            errors.put("message", sb.toString());
        }
    }
}
