package com.example.Notifications.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    private final RestTemplate restTemplate;

    // URL base del microservicio principal, inyectada desde application.properties.
    @Value("${microservice.principal.url}")
    private String principalServiceUrl;

    public NotificationController() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Obtiene todas las notificaciones asociadas a un usuario específico.
     *
     * @param user El ID del usuario.
     * @return ResponseEntity con la lista de notificaciones y el estado HTTP OK.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications(@RequestParam Long user) {
        List<Notification> notifications = notificationService.findAllByUser(user);
        return new ResponseEntity<List<Notification>>(notifications, HttpStatus.OK);
    }

    /**
     * Actualiza la calificación de un estudiante en una asignatura. Este método
     * actúa como intermediario: primero, llama al microservicio principal para
     * actualizar la nota; luego, si la actualización es exitosa, crea una
     * notificación local y envía notificaciones push a través de FCM.
     *
     * @param datos Mapa que contiene los datos de la calificación (userid,
     *              subjectTitle, finalMark, etc.).
     * @return ResponseEntity con estado OK si todo va bien, Bad Request si el
     *         formato de los datos es incorrecto, o Not Found si no se puede actualizar la nota.
     * @throws Exception Si ocurre algún error durante el proceso.
     */
    @PostMapping("/updateGrade")
    public ResponseEntity<Void> updateGrade(@RequestBody Map<String, Object> datos) throws Exception {
        try {
            Long userId = Long.parseLong(datos.get("userid").toString());
            // Validación básica: Si el userId es -1, se considera una solicitud incorrecta.
            if (userId == -1) {
                return ResponseEntity.badRequest().build();
            }
            // 1. Llamada al microservicio principal para actualizar la nota.
            ResponseEntity<Map> response = restTemplate.postForEntity(
                principalServiceUrl + "/api/v1/moodle/updateGrade",
                datos,
                Map.class);

            // 2. Si la actualización en el microservicio principal fue exitosa...
            if (response.getStatusCode().is2xxSuccessful()) {
                // 3. Extraer los datos de la respuesta del microservicio principal.
                Map<String, Object> updatedData = response.getBody();
                String subjectTitle = updatedData.get("subjectTitle").toString();
                String finalMark = updatedData.get("finalMark").toString();
                //String email = updatedData.get("email").toString(); // No se usa, pero podría ser útil.
                List<String> fcmTokens = (List<String>) updatedData.get("fcmTokens");

                String assignmentName = datos.get("assignmentname").toString();

                // 4. Crear la notificación local (en la base de datos de este microservicio).
                notificationService.newNote(userId, subjectTitle, assignmentName, finalMark, "Ordinaria");

                // 5. Enviar notificaciones push a través de FCM a todos los tokens asociados al usuario.
                for (String token : fcmTokens) {
                    NotificationRequest request = new NotificationRequest(
                            "Nueva Nota en " + subjectTitle,
                            "Se ha evaluado: " + assignmentName + " con una nota de " + finalMark,
                            token);
                    fcmService.sendMessageToToken(request);
                }

                return ResponseEntity.ok().build();
            }

        } catch (NumberFormatException e) {
            // Captura la excepción si hay problemas al convertir el userid a Long.
            return ResponseEntity.badRequest().build();
        }
        // Si el microservicio principal no devuelve un 2xx, se llega aquí.
        return ResponseEntity.notFound().build();
    }
}