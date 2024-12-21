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
import org.springframework.web.bind.annotation.RestController;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.services.SubjectMarkService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para la gestión de calificaciones.
 * Proporciona un endpoint para obtener las calificaciones de un usuario autenticado.
 */
@Tag(name = "Calificaciones", description = "API para la consulta de calificaciones")
@RestController
@RequestMapping("/api/v1/marks")
public class SubjectMarkController {

    @Autowired
    private SubjectMarkService subjectMarkService;

    @Autowired
    private UserService userService;

    /**
     * Obtiene las calificaciones del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que contiene la lista de calificaciones del usuario
     *         si la autenticación es exitosa, o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Obtener calificaciones del usuario", description = "Devuelve una lista de las calificaciones asociadas al usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de calificaciones del usuario", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Subject_Mark.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Subject_Mark>> getMethodName(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            List<Subject_Mark> record = subjectMarkService.findSubjectsByStudent(user);
            return ResponseEntity.ok(record);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}