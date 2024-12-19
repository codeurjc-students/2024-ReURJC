package com.example.services.securityServices.WebSocket;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;



@Service
public class WSService {

    private final SimpMessagingTemplate messagingTemplate;
    private final WSMessagesService notificationService;

    public WSService(SimpMessagingTemplate messagingTemplate, WSMessagesService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }


    public void notifyUser(final String id, final String message) {
        ResponseMessage response = new ResponseMessage(message);

        notificationService.sendPrivateNotification(id);
        messagingTemplate.convertAndSendToUser(id, "/topic/private-messages", response);
    }
}
