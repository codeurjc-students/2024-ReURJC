package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.example.model.Attendance;
import com.example.model.SportReservation;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.services.AttendanceService;
import com.example.services.EventService;
import com.example.services.SportReservationService;
import com.example.services.SubjectMarkService;
import com.example.services.UserService;
import com.example.services.VotesService;
import com.example.services.securityServices.jwt.AuthResponse;
import com.example.services.securityServices.jwt.LoginRequest;
import com.example.services.securityServices.jwt.UserLoginService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador REST para la gestión de usuarios y sus funcionalidades asociadas.
 * Proporciona endpoints para la autenticación, consulta de información del usuario,
 * gestión de candidaturas a delegado, votaciones, reservas deportivas, asistencias y más.
 */
@Tag(name = "Usuarios", description = "API para la gestión de usuarios y sus funcionalidades asociadas")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private VotesService voteService;

    @Autowired
    private EventService eventService;

    @Autowired
    private SubjectMarkService subjectMarkService;

    @Autowired
    private SportReservationService sportReservationService;

    @Autowired
    private AttendanceService attendanceService;

    /**
     * Obtiene las asignaturas del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que contiene la lista de asignaturas del usuario o un estado de error si no está autenticado.
     */
    @Operation(summary = "Obtener asignaturas del usuario", description = "Devuelve las asignaturas del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignaturas del usuario", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Object.class)))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/me/subjects")
    public ResponseEntity<?> subjects(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity.ok(user.getSubjects());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Establece el token de dispositivo FCM para un usuario.
     *
     * @param request  La solicitud HTTP actual.
     * @param fcmToken El token de dispositivo FCM.
     * @return Una ResponseEntity con la ubicación del recurso actualizado.
     */
    @Operation(summary = "Establecer token de dispositivo FCM", description = "Establece el token de dispositivo FCM para un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Token de dispositivo FCM establecido correctamente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @PostMapping("/setDeviceToken")
    public ResponseEntity<URI> setDeviceToken(
            HttpServletRequest request,
            @Parameter(description = "Token de dispositivo FCM", required = false) @RequestParam(name = "fcmToken", required = false) String fcmToken) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            userService.setToken(user, fcmToken);
            URI location = URI.create(request.getRequestURI() + "/" + user.getId());
            return ResponseEntity.created(location).build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Autentica a un usuario y devuelve un token de acceso.
     *
     * @param accessToken  Token de acceso (opcional, desde cookie).
     * @param refreshToken Token de refresco (opcional, desde cookie).
     * @param request      La solicitud HTTP actual.
     * @param loginRequest La solicitud de inicio de sesión con las credenciales del usuario.
     * @return Una ResponseEntity con la respuesta de autenticación o un estado de error si las credenciales son inválidas.
     */
    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y devuelve un token de acceso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o credenciales inválidas", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletRequest request,
            @RequestBody LoginRequest loginRequest) {
        if (request.getUserPrincipal() != null) {
            return ResponseEntity.notFound().build();
        } else {
            if (userService.existsByEmail(loginRequest.getUsername())) {
                return userLoginService.login(loginRequest, accessToken, refreshToken);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    /**
     * Genera y devuelve el carnet de estudiante del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con la imagen del carnet de estudiante o un estado de error si el usuario no está autenticado.
     * @throws IOException Si hay un error de entrada/salida.
     * @throws SQLException Si hay un error de SQL.
     */
    @Operation(summary = "Obtener carnet de estudiante", description = "Genera y devuelve el carnet de estudiante del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carnet de estudiante generado", content = {
                    @Content(mediaType = "image/png")
            }),
            @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/me/carnet")
    public ResponseEntity<?> getCarnet(HttpServletRequest request) throws IOException, SQLException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            int width = 400;
            int height = 250;

            BufferedImage card = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = card.createGraphics();

            // Dibujar el fondo blanco con bordes redondeados
            g2d.setColor(Color.WHITE);
            int arcWidth = 20; // Ancho del arco para los bordes redondeados
            int arcHeight = 20; // Alto del arco para los bordes redondeados
            g2d.fillRoundRect(0, 0, width, height, arcWidth, arcHeight);

            // Obtener los bytes de la imagen del usuario
            byte[] photoBytes = user.getPhoto().getBytes(1, (int) user.getPhoto().length());
            ByteArrayInputStream bis = new ByteArrayInputStream(photoBytes);
            BufferedImage profilePic = ImageIO.read(bis);

            // Definir el nuevo tamaño objetivo para la imagen del usuario
            int targetWidth = 150; // Nuevo ancho deseado
            int targetHeight = (targetWidth * profilePic.getHeight()) / profilePic.getWidth(); // Calcular altura proporcional

            // Dibujar la primera imagen reescalada en el lienzo
            g2d.drawImage(profilePic, 10, 10, targetWidth, targetHeight, null);

            // Cargar la segunda imagen
            InputStream logoStream = getClass().getClassLoader().getResourceAsStream("logo.png");
            if (logoStream == null) {
                throw new FileNotFoundException("El archivo logo.png no se encontró en el classpath.");
            }
            BufferedImage image = ImageIO.read(logoStream);

            // Obtener dimensiones originales de la segunda imagen
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            // Definir el nuevo tamaño objetivo para la segunda imagen
            int newImageWidth = 150; // Nuevo ancho deseado
            int newImageHeight = (newImageWidth * imageHeight) / imageWidth; // Calcular altura proporcional

            // Dibujar la segunda imagen reescalada justo debajo de la primera
            g2d.drawImage(image, 10, 10 + targetHeight + 10, newImageWidth, newImageHeight, null);

            // Calcular la posición del texto
            int textX = 10 + targetWidth + 10; // X: posición a la derecha de la imagen con separación de 10 píxeles
            int textY = 30; // Y: posición inicial para el texto

            // Dibujar el texto a la derecha de la imagen
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 18));
            g2d.drawString(user.getName() + " " + user.getSurname1() + " " + user.getSurname2(), textX, textY);

            // Fecha de nacimiento
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            g2d.drawString("DNI: " + user.getDni().toString(), textX, textY + 30); // Espaciado de 30 píxeles hacia abajo

            g2d.drawString("ROL: " + ((user.getRoles().get(0).equals("USER")) ? "Estudiante" : "Empleado"), textX,
                    textY + 60); // Espaciado de 30 píxeles hacia abajo

            g2d.drawString("ID: " + user.getStudentId().toString(), textX, textY + 90); // Espaciado de 30 píxeles hacia abajo

            // Añadir el lema de la universidad a la derecha de la segunda imagen
            g2d.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 16)); // Cursiva y negrita
            String motto = "Non nova, sed nove";
            int mottoX = 40 + newImageWidth + 10; // X: a la derecha de la segunda imagen
            int mottoY = -20 + targetHeight + newImageHeight + 30; // Y: debajo de la segunda imagen

            g2d.drawString(motto, mottoX, mottoY); // Dibujar lema

            g2d.dispose();

            // Convertir a byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(card, "png", baos);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Verifica si el usuario autenticado es candidato a delegado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con el estado de la candidatura del usuario o un estado de error si no está autenticado.
     * @throws IOException Si hay un error de entrada/salida.
     */
    @Operation(summary = "Verificar si el usuario es candidato a delegado", description = "Verifica si el usuario autenticado es candidato a delegado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de la candidatura del usuario", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))
            }),
            @ApiResponse(responseCode = "404", description = "No encontrado", content = @Content)
    })
    @GetMapping("/me/isDelegate")
    public ResponseEntity<Boolean> getMeIsDelegate(HttpServletRequest request) throws IOException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity.ok(user.isCandidate());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Permite al usuario autenticado postularse como candidato a delegado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con la ubicación del recurso actualizado o un estado de error si no está autenticado.
     * @throws IOException Si hay un error de entrada/salida.
     */
    @Operation(summary = "Postularse como candidato a delegado", description = "Permite al usuario autenticado postularse como candidato a delegado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidatura registrada correctamente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @PutMapping("/me/becomeDelegate")
    public ResponseEntity<URI> becomeDelegate(HttpServletRequest request) throws IOException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            user.setCandidate(true);
            userService.save(user);
            URI location = URI.create(request.getRequestURI() + "/" + user.getId());
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Cancela la candidatura a delegado del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con la ubicación del recurso actualizado o un estado de error si no está autenticado.
     * @throws IOException Si hay un error de entrada/salida.
     */
    @Operation(summary = "Cancelar candidatura a delegado", description = "Cancela la candidatura a delegado del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Candidatura cancelada correctamente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @PutMapping("/me/cancelCandidacy")
    public ResponseEntity<URI> cancelCandidacy(HttpServletRequest request) throws IOException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            user.setCandidate(false);
            userService.save(user);
            URI location = URI.create(request.getRequestURI() + "/" + user.getId());
            return ResponseEntity.ok(location);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Obtiene la lista de candidatos a delegado, excluyendo al usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con la lista de candidatos o un estado de error si no está autenticado.
     * @throws IOException Si hay un error de entrada/salida.
     */
    @Operation(summary = "Obtener candidatos a delegado", description = "Obtiene la lista de candidatos a delegado, excluyendo al usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de candidatos", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/candidates")
    public ResponseEntity<List<User>> getCandidates(HttpServletRequest request) throws IOException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            String principalEmail = principal.getName();
            List<User> candidates = userService.getCandidatesExcludingUser(principalEmail);
            return ResponseEntity.ok(candidates);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Verifica si el usuario autenticado ha votado en el evento actual de votación de delegados.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con el resultado de la verificación o un estado de error si no está autenticado.
     * @throws IOException Si hay un error de entrada/salida.
     */
    @Operation(summary = "Verificar si el usuario ha votado", description = "Verifica si el usuario autenticado ha votado en el evento actual de votación de delegados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de la verificación", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/me/hasVoted")
    public ResponseEntity<Boolean> hasVoted(HttpServletRequest request) throws IOException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity
                    .ok(voteService.hasUserAlreadyVoted(user, eventService.getVoteDelegatesEvent().getEventId()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Obtiene las calificaciones del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con la lista de calificaciones del usuario o un estado de error si no está autenticado.
     */
    @Operation(summary = "Obtener calificaciones del usuario", description = "Obtiene las calificaciones del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de calificaciones del usuario", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Subject_Mark.class)))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/me/grades")
    public ResponseEntity<List<Subject_Mark>> getGrades(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity.ok(subjectMarkService.findSubjectsByStudent(user));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Crea una nueva reserva de pista deportiva para el usuario autenticado.
     *
     * @param request             La solicitud HTTP actual.
     * @param sportReservationInfo Mapa con la información de la reserva.
     * @return Una ResponseEntity con la ubicación del recurso creado o un estado de error si no está autenticado o si los datos son inválidos.
     */
    @Operation(summary = "Crear una nueva reserva de pista deportiva", description = "Crea una nueva reserva de pista deportiva para el usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada correctamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de reserva inválidos", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @PostMapping("/newReservation")
    public ResponseEntity<URI> newSportReservation(
            HttpServletRequest request,
            @Parameter(description = "Información de la reserva", required = true) @RequestBody Map<String, Object> sportReservationInfo) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            try {
                String año = sportReservationInfo.get("año").toString();
                String mes = sportReservationInfo.get("mes").toString();
                String dia = sportReservationInfo.get("fecha").toString();
                String hora = sportReservationInfo.get("hora").toString();

                // Añadir ceros a la izquierda si es necesario
                if (mes.length() < 2) {
                    mes = "0" + mes;
                }
                if (dia.length() < 2) {
                    dia = "0" + dia;
                }
                if (hora.length() < 2) {
                    hora = "0" + hora;
                }

                String dateTimeString = año + "-" + mes + "-" + dia + " " + hora + ":00:00.000000";
                // Parseamos la fecha en el formato correcto
                LocalDateTime fechaHora = LocalDateTime.parse(
                        dateTimeString,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

                Long reserveId = sportReservationService.newReserve(user, fechaHora,
                        (int) sportReservationInfo.get("pista"));
                URI location = URI.create(request.getRequestURI() + "/" + reserveId);
                return ResponseEntity.created(location).build();
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Obtiene las reservas de pistas deportivas para una fecha y pista específicas.
     *
     * @param pista ID de la pista.
     * @param año   Año de la reserva.
     * @param mes   Mes de la reserva.
     * @param dia   Día de la reserva.
     * @return Una ResponseEntity con la lista de reservas o un estado de error si los parámetros son inválidos.
     */
    @Operation(summary = "Obtener reservas de pistas deportivas", description = "Obtiene las reservas de pistas deportivas para una fecha y pista específicas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de reservas", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SportReservation.class)))
            }),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content)
    })
    @GetMapping("/getReservations")
    public ResponseEntity<List<SportReservation>> getReservations(
            @Parameter(description = "ID de la pista", required = true) @RequestParam("pista") int pista,
            @Parameter(description = "Año de la reserva", required = true) @RequestParam("año") int año,
            @Parameter(description = "Mes de la reserva", required = true) @RequestParam("mes") int mes,
            @Parameter(description = "Día de la reserva", required = true) @RequestParam("dia") int dia) {
        LocalDate date = LocalDate.of(año, mes, dia);
        return ResponseEntity.ok(sportReservationService.getActivereservations(pista, date));
    }

    /**
     * Verifica si el usuario autenticado tiene una reserva activa.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con el resultado de la verificación o un estado de error si no está autenticado.
     */
    @Operation(summary = "Verificar si el usuario tiene una reserva activa", description = "Verifica si el usuario autenticado tiene una reserva activa.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de la verificación", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/hasReservation")
    public ResponseEntity<Boolean> hasReservation(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity.ok(sportReservationService.isreserveActive(user));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Elimina la reserva activa del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity que indica si la operación se realizó correctamente o un estado de error si no está autenticado.
     */
    @Operation(summary = "Eliminar reserva activa", description = "Elimina la reserva activa del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @DeleteMapping("/me/deleteReservation")
    public ResponseEntity deletereservation(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            sportReservationService.deleteReservation(user);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Obtiene la reserva activa del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con la reserva del usuario o un estado de error si no está autenticado o no tiene reserva.
     */
    @Operation(summary = "Obtener reserva activa del usuario", description = "Obtiene la reserva activa del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva del usuario", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = SportReservation.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/me/getReservation")
    public ResponseEntity<SportReservation> getUserReservation(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            SportReservation reservation = sportReservationService.getUserReserve(user);
            return ResponseEntity.ok(reservation);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    /**
     * Registra la asistencia del usuario autenticado a un evento de asistencia.
     *
     * @param request La solicitud HTTP actual.
     * @param code    El código del evento de asistencia.
     * @return Una ResponseEntity con la ubicación del recurso creado o un estado de error si no está autenticado o si el código es inválido.
     */
    @Operation(summary = "Registrar asistencia a un evento", description = "Registra la asistencia del usuario autenticado a un evento de asistencia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asistencia registrada correctamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Solicitud incorrecta", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Evento de asistencia no encontrado", content = @Content)
    })
    @PostMapping("/newAttendance")
    public ResponseEntity<URI> newAttendance(
            HttpServletRequest request,
            @Parameter(description = "Código del evento de asistencia", required = true) @RequestParam String code) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            Attendance attendance = attendanceService.getAttendanceEvent(code);
            if (attendance != null) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime fiveMinutesAgo = now.minusMinutes(5).plusHours(1);
                if (attendance.getDateTime().isAfter(fiveMinutesAgo)) {
                    attendanceService.adduser(attendance, user);
                    URI location = URI.create(request.getRequestURI() + "/" + user.getId());
                    return ResponseEntity.created(location).build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Obtiene la información del usuario autenticado.
     *
     * @param request La solicitud HTTP actual.
     * @return Una ResponseEntity con la información del usuario autenticado o un estado de error si no está autenticado.
     * @throws IOException Si hay un error de entrada/salida.
     */
    @Operation(summary = "Obtener información del usuario autenticado", description = "Obtiene la información del usuario autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Información del usuario", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            }),
            @ApiResponse(responseCode = "403", description = "Acceso denegado", content = @Content)
    })
    @GetMapping("/me")
    public ResponseEntity<User> get_me(HttpServletRequest request) throws IOException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            user.setPassword(null); // No se devuelve la contraseña por seguridad
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

}
