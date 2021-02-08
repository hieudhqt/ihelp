package com.swp.ihelp.exception;

import com.swp.ihelp.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Calendar;
import java.util.TimeZone;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private static String timeStamp = Calendar.getInstance(TimeZone.getTimeZone("Asia/Saigon")).getTime().toString();

    //Entity not found exception handler
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleException(EntityNotFoundException exc) {
        ExceptionResponse response = new ExceptionResponse();

        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage(exc.getMessage());
        response.setTimeStamp(timeStamp);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    //Entity existed exception handler
    @ExceptionHandler(EntityExistedException.class)
    public ResponseEntity<ExceptionResponse> handleException(EntityExistedException exc) {
        ExceptionResponse response = new ExceptionResponse();

        response.setStatus(HttpStatus.CONFLICT.value());
        response.setMessage(exc.getMessage());
        response.setTimeStamp(timeStamp);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    //Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exc) {
        ExceptionResponse response = new ExceptionResponse();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(exc.getMessage());
        response.setTimeStamp(timeStamp);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
