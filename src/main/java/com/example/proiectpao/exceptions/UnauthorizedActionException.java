package com.example.proiectpao.exceptions;

/**
 * Exceptie aruncata in cazul in care un utilizator nu are dreptul de a efectua o actiune.
 */
public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
