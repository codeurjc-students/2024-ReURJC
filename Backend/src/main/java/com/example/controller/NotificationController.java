package com.example.controller;

import java.security.Principal;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Notification;
import com.example.model.User;
import com.example.services.NotificationService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST para la gestión de notificaciones.
 * Proporciona un endpoint para obtener todas las notificaciones de un usuario.
 */
@Tag(name = "Notificaciones", description = "API para la consulta de notificaciones")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    /**
     * Obtiene todas las notificaciones de un usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que contiene la lista de notificaciones del usuario
     *         si la autenticación es exitosa, o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Obtener todas las notificaciones de un usuario", description = "Devuelve una lista de todas las notificaciones asociadas al usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificaciones del usuario", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Notification.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<List<Notification>> getAllNotifications(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            List<Notification> notifications = notificationService.findAllByUser(user);
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}