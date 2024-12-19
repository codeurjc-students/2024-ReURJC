package com.example.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.User;
import com.example.model.Events.Event;
import com.example.services.EventService;
import com.example.services.UserService;
import com.example.services.VotesService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Controlador REST para la gestión de eventos.
 * Proporciona endpoints para consultar eventos, verificar si los eventos de
 * delegados están activados, votar en eventos de delegados y programar el
 * final de un evento de votación.
 */
@Tag(name = "Eventos", description = "API para la gestión de eventos")
@RestController
@RequestMapping("/api/v1")
public class EventsController {

    private static final String EVENTS_PATH = "/events";
    private static final String IS_DELEGATE_ACTIVATED_PATH = EVENTS_PATH + "/isDelegateActivated";
    private static final String IS_VOTE_DELEGATE_ACTIVATED_PATH = EVENTS_PATH + "/isVoteDelegateActivated";
    private static final String VOTE_PATH = EVENTS_PATH + "/vote";

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private VotesService voteService;

    /**
     * Obtiene todos los eventos.
     *
     * @return Una ResponseEntity que contiene la lista de todos los eventos.
     */
    @Operation(summary = "Obtener todos los eventos", description = "Devuelve una lista de todos los eventos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de eventos", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))
            })
    })
    @GetMapping(EVENTS_PATH)
    public ResponseEntity<List<Event>> getEvents() {
        return ResponseEntity.ok(eventService.getEvents());
    }

    /**
     * Verifica si el evento para postularse como delegado está activado.
     *
     * @return Una ResponseEntity que contiene true si el evento está activado,
     *         false en caso contrario.
     */
    @Operation(summary = "Verificar si el evento para postularse como delegado está activado", description = "Devuelve true si el evento para postularse como delegado está activado, false en caso contrario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del evento para postularse como delegado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))
            })
    })
    @GetMapping(IS_DELEGATE_ACTIVATED_PATH)
    public ResponseEntity<Boolean> isCandidateEvent() {
        return ResponseEntity.ok(eventService.isBecomeCandidateEvent());
    }

    /**
     * Verifica si el evento de votación de delegados está activado.
     *
     * @return Una ResponseEntity que contiene true si el evento está activado,
     *         false en caso contrario.
     */
    @Operation(summary = "Verificar si el evento de votación de delegados está activado", description = "Devuelve true si el evento de votación de delegados está activado, false en caso contrario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del evento de votación de delegados", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))
            })
    })
    @GetMapping(IS_VOTE_DELEGATE_ACTIVATED_PATH)
    public ResponseEntity<Boolean> isVoteDelegateEvent() {
        return ResponseEntity.ok(eventService.isVoteDelegatesEvent());
    }

    /**
     * Vota por un candidato en el evento de votación de delegados.
     *
     * @param request     La solicitud HTTP actual.
     * @param candidateId El ID del candidato por el que se vota.
     * @return Una ResponseEntity que indica el resultado de la operación.
     */
    @Operation(summary = "Votar por un candidato", description = "Permite a un usuario votar por un candidato en el evento de votación de delegados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Voto registrado correctamente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado / Ya se ha votado / El evento no está activo", content = @Content)
    })
    @PostMapping(VOTE_PATH)
    public ResponseEntity<String> vote(
            HttpServletRequest request,
            @Parameter(description = "ID del candidato por el que se vota", required = true) @RequestBody long candidateId) {
        Principal principal = request.getUserPrincipal();
        if (eventService.isVoteDelegatesEvent()) {
            if (principal != null) {
                if (userService.findById(candidateId).isCandidate()) {
                    User user = userService.findByEmail(principal.getName());
                    if (!voteService.hasUserAlreadyVoted(user, eventService.getVoteDelegatesEvent().getEventId())) {
                        Long voteId = voteService.createVote(user, candidateId,
                                eventService.getVoteDelegatesEvent().getEventId());
                        // Se crea la URI con la ruta actual, para ajustarse al estandar REST
                        URI location = URI.create(request.getRequestURI() + "/" + voteId);
                        return ResponseEntity.created(location).build();
                    }
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Programa el final del evento de votación.
     * Se ejecuta diariamente a las 00:00:00.
     */
    @Operation(summary = "Programar el final del evento de votación", description = "Este método se ejecuta automáticamente para finalizar el evento de votación si no está activado.")
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleVotingEnd() {
        if (!eventService.isVoteDelegatesEvent())
            eventService.endVotingEvent();
    }
}