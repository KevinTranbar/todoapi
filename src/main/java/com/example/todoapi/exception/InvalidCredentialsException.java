package com.example.todoapi.exception;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() { //Same exception for both username and password to prevent probing (brute force) api
        super("Invalid username or password");
    }
}
