package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.SportReservation;
import com.example.model.User;
import com.example.model.Events.VoteDelegateEvent;
import com.example.services.EventService;
import com.example.services.SportReservationService;
import com.example.services.UserService;
import com.example.services.VotesService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador REST para la gestión de funcionalidades de administrador.
 * Proporciona endpoints para que los usuarios con rol de administrador puedan
 * consultar información sobre reservas, votos, eventos y usuarios.
 */
@Tag(name = "Administrador", description = "API de administrador para la gestión de reservas, votos, eventos y usuarios")
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private SportReservationService sportReservationService;

    @Autowired
    private VotesService votesService;

    @Autowired
    private EventService eventService;

    /**
     * Obtiene todas las reservas activas.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que contiene la lista de reservas activas si el
     *         usuario es administrador, o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Obtener todas las reservas activas", description = "Devuelve una lista de todas las reservas de pistas deportivas que están activas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas activas", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = SportReservation.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/reservations")
    public ResponseEntity<List<SportReservation>> getAllReservations(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("ADMIN")) {
                return ResponseEntity.ok(sportReservationService.getAllActiveReservations());
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Obtiene todos los votos asociados a un evento específico.
     *
     * @param request La solicitud HTTP actual.
     * @param eventId El ID del evento del que se quieren obtener los votos.
     * @return Una ResponseEntity que contiene la lista de votos si el usuario es
     *         administrador, o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Obtener todos los votos de un evento", description = "Devuelve una lista de todos los votos asociados a un evento dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de votos del evento", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Object[].class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/votes")
    public ResponseEntity<List<Object[]>> getAllVotesByEvent(
            HttpServletRequest request,
            @Parameter(description = "ID del evento para el que se obtienen los votos", required = true) @RequestParam("eventId") Long eventId) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("ADMIN")) {
                return ResponseEntity.ok(votesService.getAllVotesByIdAndEvent(eventId));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Obtiene todos los eventos de votación de delegados.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que contiene la lista de eventos si el usuario es
     *         administrador, o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Obtener todos los eventos de votación", description = "Devuelve una lista de todos los eventos de votación de delegados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos de votación", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = VoteDelegateEvent.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/events")
    public ResponseEntity<List<VoteDelegateEvent>> getAllEvents(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("ADMIN")) {
                return ResponseEntity.ok(eventService.getAllEvents());
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param request La solicitud HTTP actual.
     * @param userId  El ID del usuario que se quiere obtener.
     * @return Una ResponseEntity que contiene el usuario si el usuario actual es
     *         administrador, o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Obtener un usuario por ID", description = "Devuelve el usuario dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/user")
    public ResponseEntity<User> getUserById(
            HttpServletRequest request,
            @Parameter(description = "ID del usuario a buscar", required = true) @RequestParam("userId") Long userId) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("ADMIN")) {
                return ResponseEntity.ok(userService.findById(userId));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}