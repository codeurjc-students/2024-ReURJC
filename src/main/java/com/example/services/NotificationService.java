package com.example.services;

import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.model.Notification;
import com.example.model.User;
import com.example.repository.NotificationRepository;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userservice;

    public void newNote(long userId, String subject, String evaluatedItem, String mark, String convocatory) {
        notificationRepository.save(new Notification(userservice.findById(userId), "Nueva nota en ".concat(subject), "Se ha evaluado "  + evaluatedItem + " con una nota de " + mark+". Convocatoria: "+ convocatory)); 
    }

    public List<Notification> findAllByUserId(User user) {
        return notificationRepository.findFirst10ByStudentOrderByNotificationIdDesc(user);
    }
    
}
