package com.dbs.assessment.exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String errormsg) {
        super(errormsg);
    }
}
