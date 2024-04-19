package com.example.proiectpao.exceptions;

public class NonExistentException extends RuntimeException {
    public NonExistentException(String message) {
        super(message);
    }
}
