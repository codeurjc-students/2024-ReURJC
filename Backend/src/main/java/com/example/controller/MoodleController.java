package com.example.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
import com.example.model.Subject;
import com.example.model.Subject_Mark;
import com.example.model.User;
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
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Actualiza la calificación de un estudiante en una asignatura específica,
     * o la crea si no existe.
     * Envía una notificación al estudiante a través de WebSockets.
     *
     * @param datos Datos de la calificación a actualizar (userid, courseid, grade, assignmentname).
     * @return ResponseEntity con un mapa que contiene información sobre la calificación actualizada y el usuario,
     *         o un error si los datos son incorrectos o el usuario/asignatura no se encuentran.
     * @throws Exception Si ocurre algún error durante el proceso.
     */
    @Operation(summary = "Actualizar calificación", description = "Actualiza la calificación de un estudiante en una asignatura.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Calificación actualizada correctamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario o asignatura no encontrados", content = @Content)
    })
    @PostMapping("/updateGrade")
public ResponseEntity<Map<String, Object>> updateGrade(@RequestBody Map<String, Object> datos) throws Exception {
    try {
        Long userId = Long.parseLong(datos.get("userid").toString());
        if (userId != -1) {
            Long courseId = Long.parseLong(datos.get("courseid").toString());
            int mark = Integer.parseInt(datos.get("grade").toString());
            String assignmentName = datos.get("assignmentname").toString();

            User student = userService.findById(userId);
            Subject subject = subjectService.getSubject(courseId);

            Long idCreated;
            if (!subjectMarkService.existsByStudentIdAndSubjectIdAndNameMark(student, subject, assignmentName)) {
                idCreated = subjectMarkService.save(new Subject_Mark(student, subject, mark, "Ordinaria", assignmentName));
            } else {
                idCreated = updateExistingMark(student, subject, mark, assignmentName);
            }

            // Crear respuesta con datos necesarios para la notificación
            Map<String, Object> response = Map.of(
                    "subjectTitle", subject.getTitle(),
                    "finalMark", mark,
                    "userId", student.getId(),
                    "email", student.getEmail(),
                    "fcmTokens", student.getFcmToken() // Lista de tokens de notificación
            );
            System.out.println("Respuesta preparada para el microservicio de notificaciones: " + response);
            messagingTemplate.convertAndSendToUser(student.getEmail(), "/topic/private-messages",
                        Map.of("content", subjectMarkService.getLastSubjectMarkAdded(student)));

            return ResponseEntity.ok(response);
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
     *  Recibe la solicitud para reenviar el último mensaje, obtiene al usuario actual,
     *  busca su última calificación añadida y la envía a través de WebSockets.
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