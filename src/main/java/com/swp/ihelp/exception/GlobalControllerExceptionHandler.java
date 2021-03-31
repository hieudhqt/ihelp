package com.swp.ihelp.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.swp.ihelp.response.ExceptionResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(ConstraintViolationException ex) {
        ExceptionResponse response = new ExceptionResponse();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Validation failed.");
        response.setTimeStamp(timeStamp);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }

        ExceptionResponse error = new ExceptionResponse();
        error.setMessage("Validation failed.");
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setDetails(details);
        error.setTimeStamp(timeStamp);

        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    //Handle format exception
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ExceptionResponse> handleFormatException(InvalidFormatException ex) {
        ExceptionResponse response = new ExceptionResponse();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(ex.getMessage());
        response.setTimeStamp(timeStamp);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleDataException(DataIntegrityViolationException ex) {
        ExceptionResponse response = new ExceptionResponse();

        response.setStatus(HttpStatus.CONFLICT.value());
        if (ex.getMessage().contains("same identifier")) {
            response.setMessage("Entity already existed.");
        } else {
            response.setMessage(ex.getMessage());
        }
        response.setTimeStamp(timeStamp);

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    //Handle any exceptions.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exc) {
        ExceptionResponse response = new ExceptionResponse();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setMessage(exc.getMessage());
        response.setTimeStamp(timeStamp);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
