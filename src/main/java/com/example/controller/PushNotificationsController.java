package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.NotificationRequest;
import com.example.model.NotificationResponse;
import com.example.services.FCMService;

import java.util.concurrent.ExecutionException;

@RestController
public class PushNotificationsController {
    @Autowired
    private FCMService fcmService;

    @PostMapping("/api/notification")
    public ResponseEntity sendNotification(@RequestBody NotificationRequest request)
            throws ExecutionException, InterruptedException {
        fcmService.sendMessageToToken(request);
        return new ResponseEntity<>(new NotificationResponse(HttpStatus.OK.value(), "Notification has been sent."),
                HttpStatus.OK);
    }

}
