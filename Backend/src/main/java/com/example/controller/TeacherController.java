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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.model.Attendance;
import com.example.model.AttendanceDto;
import com.example.model.Subject;
import com.example.model.SubjectDto;
import com.example.model.User;
import com.example.services.SubjectService;
import com.example.services.UserService;

import jakarta.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Controlador REST para la gestión de funcionalidades de profesor.
 * Proporciona endpoints para que los usuarios con rol de profesor puedan
 * crear nuevos registros de asistencia y consultar los registros de asistencia
 * existentes.
 */
@Tag(name = "Profesor", description = "API para la gestión de funcionalidades de profesor")
@RestController
@RequestMapping("/api/v1/teacher")
public class TeacherController {

    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${attendance.service.url}")
    private String attendanceServiceUrl;

    public SubjectDto convertToDto(Subject subject) {
        return new SubjectDto(subject.getId(), subject.getTitle());
    }

    /**
     * Crea un nuevo registro de asistencia para una asignatura, delegando la
     * operación a un microservicio de asistencia.
     *
     * @param request   La solicitud HTTP actual.
     * @param subjectId El ID de la asignatura para la que se crea el registro de
     *                  asistencia.
     * @return Una ResponseEntity con la respuesta del microservicio de asistencia
     *         (normalmente, el registro de asistencia creado),
     *         o un estado 403 Forbidden si el usuario no es un profesor.
     *         Si la llamada al microservicio falla, devuelve el código de estado
     *         correspondiente.
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
                String url = attendanceServiceUrl + "/attendance/new?id=" + user.getStudentId();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<SubjectDto> requestEntity = new HttpEntity<>(
                        convertToDto(subjectService.getSubject(subjectId)), headers);
                ResponseEntity<Attendance> response = restTemplate.exchange(
                        url, // URL del microservicio de asistencia
                        HttpMethod.POST, // Método HTTP
                        requestEntity, // La entidad de la petición
                        Attendance.class // El tipo de dato de la respuesta esperada
                );
                if (response.getStatusCode().is2xxSuccessful()) {
                    return response; // Devuelve la respuesta del microservicio de asistencia
                } else {
                    // Maneja errores (4xx, 5xx)
                    return ResponseEntity.status(response.getStatusCode()).build();
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Obtiene todos los registros de asistencia creados por un profesor, delegando
     * la operación a un microservicio de asistencia.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con la lista de registros de asistencia del
     *         profesor, o un estado 403 Forbidden
     *         si el usuario no está autenticado o no es un profesor. Si la llamada
     *         al microservicio falla, devuelve
     *         el código de estado correspondiente o 500 Internal Server Error si es
     *         un error de red.
     */
    @Operation(summary = "Obtener todos los registros de asistencia", description = "Devuelve una lista de todos los registros de asistencia creados por el profesor autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de registros de asistencia", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Attendance.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/attendances")
    public ResponseEntity<List<AttendanceDto>> getAllAttendances(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.findByEmail(principal.getName());
        if (!user.getRoles().contains("TEACHER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 1. Construye la URL del microservicio de asistencia.
        String url = attendanceServiceUrl + "/attendance/attendances?id=" + user.getStudentId();

        // 2. Realiza la petición GET usando restTemplate.exchange (o getForEntity).

        try {

            ResponseEntity<List<AttendanceDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null, // No hay cuerpo en la petición GET
                    new ParameterizedTypeReference<List<AttendanceDto>>() {
                    } // Usa ParameterizedTypeReference para listas
            );

            // 3. Procesa la respuesta.
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody()); // Devuelve la lista de asistencias
            } else {
                // Maneja errores (4xx, 5xx).
                return ResponseEntity.status(response.getStatusCode()).build();
            }
        } catch (RestClientException e) {
            // Captura excepciones de RestTemplate (errores de red, timeouts, etc.).
            System.err.println("Error al llamar al microservicio de asistencia: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Añade tiempo a un registro de asistencia existente, delegando la operación a
     * un microservicio de asistencia.
     *
     * @param id      El ID del registro de asistencia al que se le añadirá tiempo.
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que indica si la operación fue exitosa:
     *         - 200 OK si se añadió el tiempo correctamente (respuesta del
     *         microservicio).
     *         - 403 Forbidden si el usuario no es un profesor o no es el creador de
     *         la asistencia.
     *         - 500 Internal Server Error si hay un error de comunicación con el
     *         microservicio.
     *         El manejo de errores 404 (Not Found) se delega al microservicio de
     *         asistencia.
     */
    @Operation(summary = "Añadir tiempo a un registro de asistencia", description = "Añade 15 minutos a la hora de finalización de un registro de asistencia, si este existe y el usuario autenticado es su creador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tiempo añadido correctamente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Registro de asistencia no encontrado", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    // En TeacherController (Microservicio Principal)

    @PutMapping("/attendances/{id}/add-time")
    public ResponseEntity<Boolean> addTimeToAttendance(@PathVariable Long id, HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.findByEmail(principal.getName());
        if (!user.getRoles().contains("TEACHER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // 1. Construye la URL  --  ¡AÑADE EL ID DEL CREADOR!
        String url = attendanceServiceUrl + "/attendance/attendances/" + id + "/add-time?id=" + user.getStudentId();

        // 2. Crea la entidad de la petición (encabezados si son necesarios)
        HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.APPLICATION_JSON); // No es necesario en este caso
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        // 3. Realiza la petición PUT
        try {
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    Boolean.class);

            return response; // Devuelve la respuesta

        } catch (RestClientException e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}