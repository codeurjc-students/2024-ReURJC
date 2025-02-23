package com.example.Notifications.service;

import org.springframework.stereotype.Service;

import com.example.Notifications.model.Notification;
import com.example.Notifications.repository.NotificationRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;



@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void newNote(Long user, String subject, String evaluatedItem, String mark, String convocatory) {
        notificationRepository.save(new Notification(user, "Nueva nota en ".concat(subject),
                "Se ha evaluado " + evaluatedItem + " con una nota de " + mark + ". Convocatoria: " + convocatory));
    }

    public List<Notification> findAllByUser(Long user) {
        return notificationRepository.findFirst10ByStudentOrderByNotificationIdDesc(user);
    }

}
