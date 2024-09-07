package com.example.demo;

public class BookIdMismatchException extends RuntimeException {

    public BookIdMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookIdMismatchException(String message) {
        super(message);
    }

    public BookIdMismatchException() {
        super("Book ID mismatch");
    }
}