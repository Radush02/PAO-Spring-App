package com.example.proiectpao.exceptions;

/**
 * Exceptie aruncata in cazul in care un obiect exista deja.
 */
public class AlreadyExistsException extends RuntimeException {
    public AlreadyExistsException(String message) {
        super(message);
    }
}
