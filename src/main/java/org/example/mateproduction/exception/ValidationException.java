package org.example.mateproduction.exception;

public class ValidationException extends Exception {
    public ValidationException(String titleIsRequired) {
        super(titleIsRequired);
    }
}
