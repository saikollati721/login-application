package com.dbs.assessment.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String errormsg) {
        super(errormsg);
    }
}
