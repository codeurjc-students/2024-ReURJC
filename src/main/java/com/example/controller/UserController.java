package com.example.controller;

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
import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.example.model.SportReservation;
import com.example.model.Subject_Mark;
import com.example.model.User;
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

@RestController
@RequestMapping("/api/users")
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

    @GetMapping("/me/subjects")
    public ResponseEntity<?> getMethodName(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity.ok(user.getSubjects());
        } else {
            ResponseEntity.notFound().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/setDeviceToken")
    public ResponseEntity<URI> postMethodName(HttpServletRequest request,
            @RequestParam(name = "fcmToken", required = false) String fcmToken) {
        Principal principal = request.getUserPrincipal();
        System.out.println("El token es: " + fcmToken);
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            userService.setToken(user, fcmToken);
            URI location = URI.create(request.getRequestURI() + "/" + user.getId());
            return ResponseEntity.ok(location);
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para realizar esta acción.");
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletRequest request,
            @RequestBody LoginRequest loginRequest) {
        if (request.getUserPrincipal() != null) {
            return ResponseEntity
                    .notFound().build();
        } else {
            if (userService.existsByEmail(loginRequest.getUsername())) {
                return userLoginService.login(loginRequest, accessToken, refreshToken);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @GetMapping("/me")
    public ResponseEntity<User> get_me(HttpServletRequest request) throws IOException {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

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
            int targetHeight = (targetWidth * profilePic.getHeight()) / profilePic.getWidth(); // Calcular altura
                                                                                               // proporcional

            // Dibujar la primera imagen reescalada en el lienzo
            g2d.drawImage(profilePic, 10, 10, targetWidth, targetHeight, null);

            // Cargar la segunda imagen
            BufferedImage image = ImageIO.read(new File("src/main/java/com/example/model/logo.png"));

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
            g2d.drawString("DNI: " + user.getDni().toString(), textX, textY + 30); // Espaciado de 30 píxeles hacia
                                                                                   // abajo

            g2d.drawString("ROL: " + ((user.getRoles().get(0).equals("USER")) ? "Estudiante" : "Empleado"), textX,
                    textY + 60); // Espaciado de 30 píxeles hacia abajo

            g2d.drawString("ID: " + user.getStudentId().toString(), textX, textY + 90); // Espaciado de 30 píxeles hacia
                                                                                        // abajo

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

    @GetMapping("/me/grades")
    public ResponseEntity<List<Subject_Mark>> getGrades(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity.ok(subjectMarkService.findSubjectsByStudent(user));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/newReservation")
    public ResponseEntity<URI> newSportReservation(HttpServletRequest request,
            @RequestBody Map<String, Object> SportRervationInfo) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());

            try {
                // Construct the date-time string, adding a leading zero to the day
                String año = SportRervationInfo.get("año").toString();
                String mes = SportRervationInfo.get("mes").toString();
                String dia = SportRervationInfo.get("fecha").toString();
                String hora = SportRervationInfo.get("hora").toString();

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
                // Parse the date-time string using the correct format
                LocalDateTime fechaHora = LocalDateTime.parse(
                        dateTimeString,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));

                Long reserveId = sportReservationService.newReserve(user, fechaHora,
                        (int) SportRervationInfo.get("pista"));
                URI location = URI.create(request.getRequestURI() + "/" + reserveId);
                return ResponseEntity.created(location).build();

            } catch (DateTimeParseException e) {
                // Handle the exception, e.g., log the error and return an error response
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/getReservations")
    public ResponseEntity<List<SportReservation>> getReservations(
            @RequestParam("pista") int pista,
            @RequestParam("año") int año,
            @RequestParam("mes") int mes,
            @RequestParam("dia") int dia) {

        LocalDate date = LocalDate.of(año, mes, dia);

        return ResponseEntity.ok(sportReservationService.getActivereservations(pista, date));
    }

    @GetMapping("/hasReservation")
    public ResponseEntity<Boolean> hasReservation(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity.ok(sportReservationService.isreserveActive(user));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

    }

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

}
