package com.example.model;

public class NotificationResponse {
    private int status;
    private String message;

    public NotificationResponse(int status, String message) {
        this.status = status;
        this.message = message;

    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
