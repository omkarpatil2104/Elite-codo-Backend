package com.bezkoder.springjwt.ExceptionHandler.CustomeException;

import java.util.Date;

public class Apierrorr extends RuntimeException {

    private final String errorCode;
    private final Date timestamp;


    public Apierrorr(String message, String errorCode) {
        super(message); // Pass error message to RuntimeException
        this.errorCode = errorCode;
        this.timestamp = new Date(); // Automatically set the current timestamp
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
