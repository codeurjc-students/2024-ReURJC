package com.example.controller;

import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.model.NotificationRequest;
import com.example.model.Subject;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.services.FCMService;
import com.example.services.NotificationService;
import com.example.services.SubjectMarkService;
import com.example.services.SubjectService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST para la integración con Moodle.
 * Proporciona endpoints para actualizar calificaciones y enviar notificaciones
 * a través de WebSockets.
 */
@Tag(name = "Moodle", description = "API para la integración con Moodle")
@RestController
@RequestMapping("/api/v1/moodle")
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

    /**
     * Actualiza la calificación de un estudiante en una asignatura específica.
     *
     * @param datos Datos de la calificación a actualizar.
     * @return Una ResponseEntity con la URI del recurso actualizado.
     * @throws Exception Si ocurre algún error durante el proceso.
     */
    @Operation(summary = "Actualizar calificación", description = "Actualiza la calificación de un estudiante en una asignatura.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Calificación actualizada correctamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario o asignatura no encontrados", content = @Content)
    })
    @PostMapping("/updateGrade")
    public ResponseEntity<URI> updateGrade(
            @Parameter(description = "Datos de la calificación a actualizar", required = true) @RequestBody Map<String, Object> datos)
            throws Exception {

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
                    idCreated = subjectMarkService
                            .save(new Subject_Mark(student, subject, mark, "Ordinaria", assignmentName));
                } else {
                    idCreated = updateExistingMark(student, subject, mark, assignmentName);
                }
                // Notificar al usuario
                notificationService.newNote(student, subject.getTitle(), assignmentName, String.valueOf(mark),
                        "Ordinaria");
                for (String token : student.getFcmToken()) {
                    NotificationRequest request = new NotificationRequest("Nueva Nota en " + subject.getTitle(),
                            "Se ha evaluado: " + assignmentName + " con una nota de " + mark, token);
                    fcmService.sendMessageToToken(request);
                }
                URI location = URI.create("/api/v1/events/" + idCreated);
                messagingTemplate.convertAndSendToUser(student.getEmail(), "/topic/private-messages",
                        Map.of("content", subjectMarkService.getLastSubjectMarkAdded(student)));
                return ResponseEntity.created(location).build();
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Actualiza una nota existente.
     *
     * @param student        Usuario al que pertenece la nota.
     * @param subject        Asignatura a la que pertenece la nota.
     * @param mark           Nota a actualizar.
     * @param assignmentName Nombre de la tarea a actualizar.
     * @return El ID de la nota actualizada.
     */
    private Long updateExistingMark(User student, Subject subject, int mark, String assignmentName) {
        Subject_Mark existingMark = subjectMarkService
                .findByStudentIdAndSubjectIdAndNameMark(student, subject, assignmentName).get();
        existingMark.setMark(mark);
        return subjectMarkService.save(existingMark);
    }

    /**
     * Recibe un mensaje privado a través de WebSockets y lo reenvía al usuario
     * destinatario.
     *
     * @param request La solicitud HTTP actual.
     */
    @Operation(summary = "Recibir mensaje privado", description = "Recibe un mensaje privado a través de WebSockets y lo reenvía al usuario destinatario.")
    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public void getPrivateMessage(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        User user = userService.findByEmail(principal.getName());
        if (user != null) {
            Subject_Mark lastSubjectMark = subjectMarkService.getLastSubjectMarkAdded(user);
            Map<String, Subject_Mark> message = new HashMap<>();
            message.put("content", lastSubjectMark);
            messagingTemplate.convertAndSendToUser(user.getEmail(), "/topic/private-messages", message);
        }
    }
}