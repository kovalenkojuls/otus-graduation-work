package ru.kovalenkojuls.emailservice.exceptions;

public class EmailException extends RuntimeException {
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
}