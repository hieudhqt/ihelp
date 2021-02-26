package com.swp.ihelp.response;

import java.util.List;

public class ExceptionResponse {
    private int status;
    private String message;
    private List<String> details;
    private String timeStamp;

    public ExceptionResponse() {
    }

    public ExceptionResponse(int status, String message, String timeStamp) {
        this.status = status;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public ExceptionResponse(int status, String message, List<String> details, String timeStamp) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.timeStamp = timeStamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
