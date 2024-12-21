package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Attendance;
import com.example.model.User;
import com.example.services.AttendanceService;
import com.example.services.SubjectService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador REST para la gestión de funcionalidades de profesor.
 * Proporciona endpoints para que los usuarios con rol de profesor puedan
 * crear nuevos registros de asistencia y consultar los registros de asistencia existentes.
 */
@Tag(name = "Profesor", description = "API para la gestión de funcionalidades de profesor")
@RestController
@RequestMapping("/api/v1/teacher")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private SubjectService subjectService;

    /**
     * Crea un nuevo registro de asistencia para una asignatura.
     *
     * @param request   La solicitud HTTP actual.
     * @param subjectId El ID de la asignatura para la que se crea el registro de asistencia.
     * @return Una ResponseEntity que contiene el registro de asistencia creado si el usuario es un profesor,
     *         o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Crear un nuevo registro de asistencia", description = "Crea un nuevo registro de asistencia para una asignatura. Solo accesible para profesores.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro de asistencia creado correctamente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @PostMapping("/newAttendance")
    public ResponseEntity<Attendance> newAttendance(
            HttpServletRequest request,
            @Parameter(description = "ID de la asignatura para la que se crea el registro de asistencia", required = true) @RequestParam Long subjectId) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("TEACHER")) {
                Attendance attendance = attendanceService.newAttendance(user, subjectService.getSubject(subjectId));
                URI location = URI.create(request.getRequestURI() + "/" + attendance.getCode());
                return ResponseEntity.created(location).build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Obtiene todos los registros de asistencia de un profesor.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que contiene la lista de registros de asistencia del profesor
     *         si la autenticación es exitosa, o un estado de error 403 si no lo es.
     */
    @Operation(summary = "Obtener todos los registros de asistencia", description = "Devuelve una lista de todos los registros de asistencia creados por el profesor autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de registros de asistencia", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Attendance.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/attendances")
    public ResponseEntity<List<Attendance>> getAllAttendances(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user.getRoles().contains("TEACHER")) {
                return ResponseEntity.ok(attendanceService.getAllAttendances(user));
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}