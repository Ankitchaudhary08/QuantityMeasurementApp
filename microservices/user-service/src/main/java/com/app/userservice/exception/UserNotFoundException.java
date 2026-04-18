package com.app.userservice.exception;

/**
 * Thrown when a user is not found by email or ID.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
