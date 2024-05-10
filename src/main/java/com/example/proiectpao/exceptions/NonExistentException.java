package com.example.proiectpao.exceptions;

/**
 * Exceptie aruncata in cazul in care un obiect nu exista.
 */
public class NonExistentException extends RuntimeException {
    public NonExistentException(String message) {
        super(message);
    }
}
