package com.example.model;

public class NotificationRequest {
    private String title;
    private String body;
    private String topic;
    private String token;

    public NotificationRequest(String t, String b, String to, String tok) {
        title = t;
        body=b;
        topic = to;
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
