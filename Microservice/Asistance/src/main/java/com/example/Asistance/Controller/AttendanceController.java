package com.example.Asistance.Controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Asistance.Model.Attendance;

import com.example.Asistance.Model.Subject;
import com.example.Asistance.Model.User;
import com.example.Asistance.services.AttendanceService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceService attendanceService;

    /**
     * Crea un nuevo registro de asistencia.
     *
     * @param request   La solicitud HTTP.
     * @param subject   La asignatura para la que se crea la asistencia.
     * @param creatorId El ID del usuario creador (profesor).
     * @return ResponseEntity con el objeto Attendance creado y la ubicación del
     *         recurso.
     */
    @PostMapping("/new")
    public ResponseEntity<Attendance> newAttendance(
            HttpServletRequest request,
            @RequestBody Subject subject,
            @RequestParam(value = "id") Long creatorId) {
        Attendance attendance = attendanceService.newAttendance(creatorId, subject);
        // Construye la URI del nuevo recurso creado.
        URI location = URI.create(request.getRequestURI() + "/" + attendance.getCode());
        return ResponseEntity.created(location).build();
    }

    /**
     * Añade un usuario a un registro de asistencia existente, dado su código.
     *
     * @param request La solicitud HTTP.
     * @param code    El código del registro de asistencia.
     * @param user    El usuario que se va a añadir.
     * @return ResponseEntity con la ubicación del recurso (usuario añadido) si
     *         tiene éxito,
     *         404 si no se encuentra el evento, o 400 si la solicitud es
     *         incorrecta.
     */
    @PostMapping("/newAttendance")
    public ResponseEntity<URI> newAttendance(
            HttpServletRequest request,
            @RequestParam String code,
            @RequestBody User user) {

        Attendance attendance = attendanceService.getAttendanceEvent(code);

        if (attendance != null) {
            // Obtiene la hora actual.
            LocalDateTime now = LocalDateTime.now();
            // Calcula un tiempo límite (5 minutos antes de ahora + 1 hora de ajuste).
            LocalDateTime fiveMinutesAgo = now.minusMinutes(5).plusHours(1);
            // Verifica si la asistencia está dentro del rango permitido.
            if (attendance.getDateTime().isAfter(fiveMinutesAgo)) {
                attendanceService.adduser(attendance, user);
                // Construye la URI del nuevo recurso creado
                URI location = URI.create(request.getRequestURI() + "/" + user.getStudentId());
                return ResponseEntity.created(location).build();
            }
        } else {
            // Devuelve 404 si el evento no se encuentra.
            return ResponseEntity.notFound().build();
        }
        // Devuelve 400 si la solicitud no es válida (fuera de tiempo).
        return ResponseEntity.badRequest().build();
    }

    /**
     * Obtiene todos los registros de asistencia asociados a un usuario (normalmente
     * un profesor).
     *
     * @param userId El ID del usuario.
     * @return ResponseEntity con la lista de registros de asistencia.
     */
    @GetMapping("/attendances")
    public ResponseEntity<List<Attendance>> getAllAttendances(@RequestParam("id") Long userId) {
        return ResponseEntity.ok(attendanceService.getAllAttendances(userId));
    }

    /**
     * Añade tiempo (15 minutos) a un registro de asistencia existente.
     *
     * @param userId  El ID del usuario que realiza la solicitud (debe ser el
     *                creador).
     * @param id      El ID del registro de asistencia.
     * @param request La solicitud HTTP.
     * @return ResponseEntity con true si se añadió el tiempo correctamente, 403 si
     *         el usuario
     *         no es el creador, o 500 si hay un error interno.
     */
    @PutMapping("/attendances/{id}/add-time")
    public ResponseEntity<Boolean> addTimeToAttendance(@RequestParam("id") Long userId, @PathVariable Long id,
            HttpServletRequest request) {

        try {
            Attendance attendance = attendanceService.getAttendanceById(id);

            // Verifica que el usuario que intenta modificar la asistencia sea el creador.
            if (!attendance.getCreator().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            // Intenta añadir tiempo a la asistencia
            if (attendanceService.addTime(id)) {
                return ResponseEntity.ok().body(true);
            } else {
                // Si addTime devuelve false, podría ser por lógica de negocio o un error.
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            // Captura cualquier otra excepción. Podría ser una excepción de base de datos
            // u otra.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}