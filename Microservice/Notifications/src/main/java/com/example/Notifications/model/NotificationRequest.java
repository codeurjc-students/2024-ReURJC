package com.example.Notifications.model;

public class NotificationRequest {
    private String title;
    private String body;
    private String topic;
    private String token;

    public NotificationRequest(String t, String b, String tok) {
        title = t;
        body = b;
        token = tok;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getTopic() {
        return topic;
    }

    public String getToken() {
        return token;
    }

}
