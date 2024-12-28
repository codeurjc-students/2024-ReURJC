package com.example.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;

import com.example.controller.Responses.SubjectScheduleResponse;
import com.example.model.Subject;
import com.example.model.User;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para la gestión de asignaturas.
 * Proporciona endpoints para obtener el horario y las asignaturas de un usuario autenticado.
 */
@Tag(name = "Asignaturas", description = "API para la consulta de asignaturas y horarios")
@RestController
@RequestMapping("/api/v1/subjects")
public class SubjectsController {

    @Autowired
    private UserService userService;

    /**
     * Obtiene el horario de las asignaturas del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que contiene la lista de horarios de las asignaturas del usuario
     *         si la autenticación es exitosa, o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Obtener el horario de las asignaturas", description = "Devuelve el horario de las asignaturas del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario de las asignaturas del usuario", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = SubjectScheduleResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/schedule")
    public ResponseEntity<List<SubjectScheduleResponse>> getSchedule(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());

            Collection<Subject> subject = user.getSubjects();
            List<SubjectScheduleResponse> response = new ArrayList<SubjectScheduleResponse>();
            for (Subject subjectElem : subject) {
                response.add(new SubjectScheduleResponse(subjectElem.getTitle(), subjectElem.getSchedule()));
            }
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Obtiene las asignaturas del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que contiene la lista de asignaturas del usuario
     *         si la autenticación es exitosa, o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Obtener las asignaturas del usuario", description = "Devuelve la lista de asignaturas del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asignaturas del usuario", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Subject.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<List<Subject>> getSubjects(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            List<Subject> response = new ArrayList<Subject>();
            for (Subject subjectElem : user.getSubjects()) {
                response.add(subjectElem);
            }
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}