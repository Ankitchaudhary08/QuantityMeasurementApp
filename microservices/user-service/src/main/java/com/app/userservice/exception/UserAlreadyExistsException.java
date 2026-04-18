package com.app.userservice.exception;

/**
 * Thrown when a user tries to register with an already-used email.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
