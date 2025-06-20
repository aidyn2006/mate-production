package org.example.mateproduction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ReportAlreadyExistsException extends RuntimeException {
    public ReportAlreadyExistsException(String message) {
        super(message);
    }
}