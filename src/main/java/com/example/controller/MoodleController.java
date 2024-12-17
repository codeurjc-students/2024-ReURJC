package com.example.controller;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.example.model.NotificationRequest;
import com.example.model.Subject;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.services.FCMService;
import com.example.services.NotificationService;
import com.example.services.SubjectMarkService;
import com.example.services.SubjectService;
import com.example.services.UserService;
import com.example.services.securityServices.WebSocket.Message;
import com.example.services.securityServices.WebSocket.ResponseMessage;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/moodle")
public class MoodleController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectMarkService subjectMarkService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FCMService fcmService;

    @Autowired
private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/updateGrade")
    public ResponseEntity<URI> miEndpoint(@RequestBody Map<String, Object> datos) throws Exception {

        try {
            // Obtener los datos de la solicitud
            Long userId = Long.parseLong(datos.get("userid").toString());
            if (userId != -1) {
                Long courseId = Long.parseLong(datos.get("courseid").toString());
                int mark = Integer.parseInt(datos.get("grade").toString());
                String assignmentName = datos.get("assignmentname").toString();

                // Buscar el usuario y la asignatura
                User student = userService.findById(userId);
                Subject subject = subjectService.getSubject(courseId);

                // Crear una nueva nota o actualizarla si existe
                Long idCreated = null;
                if (!subjectMarkService.existsByStudentIdAndSubjectIdAndNameMark(student, subject, assignmentName)) {
                    ;
                    idCreated = subjectMarkService.save(new Subject_Mark(student, subject, mark, "Ordinaria", assignmentName));
                } else {
                    idCreated = updateExistingMark(student, subject, mark, assignmentName);
                }
                notificationService.newNote(student, subject.getTitle(), assignmentName, String.valueOf(mark),
                        "Ordinaria");
                for (String token : student.getFcmToken()) {
                    NotificationRequest request = new NotificationRequest("Nueva Nota en " + subject.getTitle(),
                            ".Se ha evaluado: " + assignmentName + " con una nota de " + mark, token);
                    fcmService.sendMessageToToken(request);
                }
                    URI location = URI.create("/api/events/" + idCreated);
                    messagingTemplate.convertAndSendToUser(student.getEmail(), "/topic/private-messages", 
                    Map.of("content", subjectMarkService.getLastSubjectMarkAdded(student)));
                        return ResponseEntity.created(location).build();
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        
        }
        return ResponseEntity.notFound().build();

    }

    private Long updateExistingMark(User student, Subject subject, int mark, String assignmentName) {
        Subject_Mark existingMark = subjectMarkService
                .findByStudentIdAndSubjectIdAndNameMark(student, subject, assignmentName).get();
        existingMark.setMark(mark);
        return subjectMarkService.save(existingMark);
    }

    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public void getPrivateMessage(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        User user = userService.findByEmail(principal.getName());
        if (user != null) {
            Subject_Mark lastSubjectMark = subjectMarkService.getLastSubjectMarkAdded(user);
            Map<String, Subject_Mark> message = new HashMap<>();
            message.put("content",  lastSubjectMark);
            messagingTemplate.convertAndSendToUser(user.getEmail(), "/topic/private-messages", message);

            
        } 
    }
}


    

    

