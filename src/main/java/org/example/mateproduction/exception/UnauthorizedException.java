package org.example.mateproduction.exception;

public class UnauthorizedException extends Throwable {
    public UnauthorizedException(String userIsNotAuthenticated) {
        super(userIsNotAuthenticated);
    }
}
