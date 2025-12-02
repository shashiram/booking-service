package com.home.dto;

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private String message;
    private int status;
    private String errors;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponseDTO(String message, int status, String errors) {
        this.message = message;
        this.status = status;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
