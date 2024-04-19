package com.example.proiectpao.exceptions;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
