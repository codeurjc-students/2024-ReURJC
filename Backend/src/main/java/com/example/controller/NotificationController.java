package com.example.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.model.Notification;
import com.example.model.User;
import com.example.services.UserService;
import org.springframework.web.client.RestTemplate;

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
    private UserService userService;


    /**
     * Obtiene todas las notificaciones de un usuario autenticado, realizando una petición a otro microservicio.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que contiene la lista de notificaciones del usuario obtenidas del microservicio de notificaciones,
     * o un estado de error apropiado si la autenticación falla, el usuario no se encuentra, o hay un problema con la petición al microservicio.
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
            if (user != null) {
                try {
                    String urlBase = "http://notifications-service-service:8082/notifications/notifications";  // Aquí defines la URL fija que quieras usar

URI uri = UriComponentsBuilder.fromHttpUrl(urlBase)
                            .queryParam("user", user.getId())
                            .build()
                            .toUri();

RestTemplate restTemplate = new RestTemplate();
ResponseEntity<List<Notification>> response = restTemplate.exchange(
                            uri,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<Notification>>() {}
                    );

                    return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
                } catch (HttpClientErrorException | HttpServerErrorException e) {
                    return ResponseEntity.status(e.getStatusCode()).build();
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
}