package com.example.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List; // Asegúrate de tener este import si usas List.of()
import java.util.Map;
// import java.util.Optional; // Necesario si usas Optional en findBy...

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema; // Si defines schema en @ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

// Import SLF4J Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Importar HttpStatus
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

    // Añadir instancia de Logger
    private static final Logger log = LoggerFactory.getLogger(MoodleController.class);

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
     * o la crea si no existe. Parsea la nota recibida (potencialmente con decimales)
     * y la convierte a entero antes de guardarla.
     * Envía una notificación al estudiante a través de WebSockets y devuelve
     * los datos necesarios al servicio de notificaciones.
     *
     * @param datos Datos de la calificación recibidos (userid, courseid, grade, assignmentname).
     * @return ResponseEntity con un mapa que contiene información sobre la calificación actualizada y el usuario,
     * o un error si los datos son incorrectos o el usuario/asignatura no se encuentran.
     */
    @Operation(summary = "Actualizar calificación", description = "Actualiza o crea la calificación de un estudiante en una asignatura, esperando la nota como entero internamente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calificación actualizada o creada correctamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta (datos faltantes o formato inválido)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario o Asignatura no encontrados", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/updateGrade")
    public ResponseEntity<Map<String, Object>> updateGrade(@RequestBody Map<String, Object> datos) {
        log.info("Received request to /updateGrade with data: {}", datos);

        Long userId;
        Long courseId;
        int mark; // La nota final que usaremos será un entero
        String assignmentName;
        String gradeString; // Para loguear/errores

        try {
            // --- 1. Validar y Parsear Datos de Entrada ---
            Object userIdObj = datos.get("userid");
            Object courseIdObj = datos.get("courseid");
            Object gradeObj = datos.get("grade");
            Object assignmentNameObj = datos.get("assignmentname");

            if (userIdObj == null || courseIdObj == null || gradeObj == null || assignmentNameObj == null) {
                log.warn("UpdateGrade failed: Missing required fields in payload: {}", datos);
                return ResponseEntity.badRequest().body(Map.of("error", "Faltan campos requeridos (userid, courseid, grade, assignmentname)"));
            }

            userId = Long.parseLong(userIdObj.toString());
            courseId = Long.parseLong(courseIdObj.toString());
            assignmentName = assignmentNameObj.toString();
            gradeString = gradeObj.toString(); // Guardar la cadena original de la nota

            double tempMark;
            try {
                tempMark = Double.parseDouble(gradeString);
            } catch (NumberFormatException e) {
                log.warn("UpdateGrade failed for userId {}: Invalid grade format '{}'", userId, gradeString);
                return ResponseEntity.badRequest().body(Map.of("error", "Formato de nota inválido: " + gradeString));
            }
            // Redondear el double al entero más cercano y convertir a int
            mark = (int) Math.round(tempMark);
            log.info("Parsed grade string '{}' to int value: {}", gradeString, mark);
            // -----------------------------------------------------------------------

            // --- 2. Obtener Entidades de Dominio ---
            User student = userService.findById(userId);
            if (student == null) {
                log.warn("UpdateGrade failed: User not found with ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Usuario no encontrado con id: " + userId));
            }

            Subject subject = subjectService.getSubject(courseId);
            if (subject == null) {
                log.warn("UpdateGrade failed for userId {}: Subject (course) not found with course ID: {}", userId, courseId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Asignatura (curso) no encontrada con id: " + courseId));
            }

            // --- 3. Guardar o Actualizar la Nota ---
            // ASUNCIÓN: El servicio y la entidad Subject_Mark esperan/guardan un 'int' para la nota.
            Long subjectMarkId;
            if (!subjectMarkService.existsByStudentIdAndSubjectIdAndNameMark(student, subject, assignmentName)) {
                log.info("Creating new Subject_Mark for userId {}, subjectId {}, assignment '{}', mark {}", userId, subject.getId(), assignmentName, mark);
                Subject_Mark newMark = new Subject_Mark(student, subject, mark, "Ordinaria", assignmentName); // Constructor debe aceptar int
                subjectMarkId = subjectMarkService.save(newMark); // save debe aceptar int
            } else {
                log.info("Updating existing Subject_Mark for userId {}, subjectId {}, assignment '{}', new mark {}", userId, subject.getId(), assignmentName, mark);
                subjectMarkId = updateExistingMark(student, subject, mark, assignmentName); // Debe aceptar int
            }
            log.info("Subject Mark processed successfully with ID: {}", subjectMarkId);


            // --- 4. Preparar Respuesta para el Servicio de Notificaciones ---
            Map<String, Object> responseBody = Map.of(
                    "subjectTitle", subject.getTitle(),
                    "finalMark", mark, // Devolver la nota ya como entero procesado
                    "userId", student.getId(),
                    "email", student.getEmail(),
                    // Asegurarse de manejar lista de tokens nula o vacía
                    "fcmTokens", student.getFcmToken() != null ? student.getFcmToken() : List.of()
            );
            log.info("Successfully processed grade update for userId {}. Response body for secondary service: {}", userId, responseBody);

            // --- 5. Enviar Notificación WebSocket ---
            try {
                 // Verifica si este método realmente obtiene la nota que acabas de guardar/actualizar
                 Subject_Mark lastMark = subjectMarkService.getLastSubjectMarkAdded(student);
                 if (lastMark != null) {
                     messagingTemplate.convertAndSendToUser(student.getEmail(), "/topic/private-messages", Map.of("content", lastMark));
                     log.info("Sent WebSocket notification to user {}", student.getEmail());
                 } else {
                     log.warn("Could not retrieve last added subject mark for userId {} to send via WebSocket.", userId);
                 }
            } catch (Exception e) {
                 // No fallar la petición HTTP si falla el WebSocket, solo loguear
                 log.error("Failed to send WebSocket notification to user {}: {}", student.getEmail(), e.getMessage(), e);
            }

            return ResponseEntity.ok(responseBody); // 200 OK indica éxito

        } catch (NumberFormatException e) {
            // Captura errores al parsear userId o courseId (el parseo de 'grade' tiene su propio catch)
            log.error("UpdateGrade failed: Error parsing userId or courseId from data {}: {}", datos, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Formato inválido para userId o courseId"));
        } catch (Exception e) {
            // Captura genérica para otros errores inesperados (DB, servicios, etc.)
            log.error("UpdateGrade failed: Unexpected internal error for data {}: {}", datos, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno del servidor al procesar la calificación"));
        }
    }


    /**
     * Actualiza una nota existente.
     * **IMPORTANTE**: Este método y la entidad/servicio subyacente deben usar 'int' para la nota.
     *
     * @param student        Usuario al que pertenece la nota.
     * @param subject        Asignatura a la que pertenece la nota.
     * @param mark           Nota (int) a actualizar.
     * @param assignmentName Nombre de la tarea a actualizar.
     * @return El ID de la nota actualizada.
     * @throws IllegalStateException si la nota no se encuentra a pesar de la verificación previa.
     */
    private Long updateExistingMark(User student, Subject subject, int mark, String assignmentName) {
        // Usar orElseThrow para manejar el caso (improbable) de que no se encuentre
        Subject_Mark existingMark = subjectMarkService
                .findByStudentIdAndSubjectIdAndNameMark(student, subject, assignmentName)
                .orElseThrow(() -> {
                    String errorMsg = String.format("CRITICAL: updateExistingMark called but mark not found for userId %d, subjectId %d, assignment '%s'",
                                                    student.getId(), subject.getId(), assignmentName);
                    log.error(errorMsg);
                    return new IllegalStateException("Subject mark not found for update despite existence check.");
                });

        existingMark.setMark(mark); // Asumiendo que setMark(int) existe
        return subjectMarkService.save(existingMark); // Asumiendo que save(Subject_Mark con int) existe
    }

    /**
     * Maneja mensajes WebSocket entrantes para reenviar la última notificación.
     * @param request La solicitud HTTP actual (usada para obtener el Principal).
     */
    @Operation(summary = "Reenviar última nota vía WebSocket", description = "Maneja mensajes en '/private-message' y envía la última nota registrada al usuario autenticado.")
    @MessageMapping("/private-message")
    @SendToUser("/topic/private-messages")
    public void getPrivateMessage(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null && principal.getName() != null) {
            User user = userService.findByEmail(principal.getName());
            if (user != null) {
                log.debug("Processing WebSocket private-message request for user {}", user.getEmail());
                Subject_Mark lastSubjectMark = subjectMarkService.getLastSubjectMarkAdded(user);
                 if(lastSubjectMark != null) {
                    Map<String, Subject_Mark> message = new HashMap<>();
                    message.put("content", lastSubjectMark);
                    messagingTemplate.convertAndSendToUser(user.getEmail(), "/topic/private-messages", message);
                    log.debug("Sent last subject mark via WebSocket to user {}", user.getEmail());
                 } else {
                    log.warn("No last subject mark found for user {} to send via WebSocket.", user.getEmail());
                    // Opcionalmente enviar un mensaje vacío o de error al usuario por WebSocket
                    // messagingTemplate.convertAndSendToUser(user.getEmail(), "/topic/private-messages", Map.of("error", "No hay notas recientes"));
                 }
            } else {
                 log.warn("WebSocket private-message: User not found for principal name {}", principal.getName());
            }
        } else {
             log.warn("WebSocket private-message: Principal or principal name is null.");
        }
    }
}