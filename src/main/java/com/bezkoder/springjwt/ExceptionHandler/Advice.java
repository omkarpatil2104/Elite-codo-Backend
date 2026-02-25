package com.bezkoder.springjwt.ExceptionHandler;

import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierror;
import com.bezkoder.springjwt.ExceptionHandler.CustomeException.Apierrorr;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class Advice {

    @ExceptionHandler(Apierrorr.class)
    public ResponseEntity<Map<String, Object>> handleApiError(Apierrorr apierror) {
        Map<String, Object> error = new HashMap<>();
        error.put("message", apierror.getMessage()); // Get error message
        error.put("errorCode", apierror.getErrorCode());
        error.put("timestamp", apierror.getTimestamp());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
