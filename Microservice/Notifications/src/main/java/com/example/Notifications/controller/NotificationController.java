package com.example.Notifications.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.Notifications.model.Notification;
import com.example.Notifications.model.NotificationRequest;
import com.example.Notifications.service.FCMService;
import com.example.Notifications.service.NotificationService;


@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FCMService fcmService;

    @Value("${microservice.principal.url}") // Inyecta la URL desde application.properties
    private String principalServiceUrl;



    

    
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications(@RequestParam Long user) {
        List<Notification> notifications = notificationService.findAllByUser(user);
        return new ResponseEntity<List<Notification>>(notifications, HttpStatus.OK);
        }

        @PostMapping("/updateGrade")
        public ResponseEntity<Void> updateGrade(@RequestBody Map<String, Object> datos) throws Exception {
            try {
                Long userId = Long.parseLong(datos.get("userid").toString());
                if (userId == -1) {
                    return ResponseEntity.badRequest().build();
                }
                    // Llamar al microservicio principal para actualizar la nota
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<Map> response = restTemplate.postForEntity(
                        principalServiceUrl + "/api/v1/moodle/updateGrade",
                            datos,
                            Map.class
                    );
        
                    if (response.getStatusCode().is2xxSuccessful()) {
                        System.out.println("Respuesta exitosa del microservicio principal: " + response.getBody());
                        // Extraer los datos de la respuesta del microservicio principal
                        Map<String, Object> updatedData = response.getBody();
                        String subjectTitle = updatedData.get("subjectTitle").toString();
                        String finalMark = updatedData.get("finalMark").toString();
                        String email = updatedData.get("email").toString();
                        List<String> fcmTokens = (List<String>) updatedData.get("fcmTokens");
        
                        String assignmentName = datos.get("assignmentname").toString();
        
                        // Enviar notificación
                        notificationService.newNote(userId, subjectTitle, assignmentName, finalMark, "Ordinaria");
        
                        for (String token : fcmTokens) {
                            NotificationRequest request = new NotificationRequest(
                                    "Nueva Nota en " + subjectTitle,
                                    "Se ha evaluado: " + assignmentName + " con una nota de " + finalMark,
                                    token
                            );
                            fcmService.sendMessageToToken(request);
                        }
                        
                        return ResponseEntity.ok().build();
                    }
                
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.notFound().build();
        }

    }

