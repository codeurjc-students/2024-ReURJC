package com.example.controller;

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.io.IOException;



import com.example.model.User;

import com.example.services.UserService;
import com.example.services.securityServices.jwt.AuthResponse;
import com.example.services.securityServices.jwt.LoginRequest;
import com.example.services.securityServices.jwt.UserLoginService;
import com.example.services.securityServices.jwt.AuthResponse.Status;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/users")
public class UserController {
   
    @Autowired
    private UserService userService;

    @Autowired
	private UserLoginService userLoginService;

    @GetMapping("/me/subjects")
    public ResponseEntity<?> getMethodName(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            return ResponseEntity.ok(user.getSubjects());
        } else {
            ResponseEntity.ok(new AuthResponse(Status.FAILURE, "You must login!", true));
        }

        return ResponseEntity.notFound().build();
    } 

    @PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@CookieValue(name = "accessToken", required = false) String accessToken,
			@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletRequest request,
			@RequestBody LoginRequest loginRequest) {
		if (request.getUserPrincipal() != null) {
			return ResponseEntity
					.ok(new AuthResponse(Status.FAILURE, "Cannot login when you are not logged out", true));
		} else {
			if (userService.existsByEmail(loginRequest.getUsername())) {
				return userLoginService.login(loginRequest, accessToken, refreshToken);
			} else {
				return ResponseEntity.ok(new AuthResponse(Status.FAILURE, "Invalid credentials", true));
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
			return ResponseEntity.notFound().build();
		}
	}
<<<<<<< Updated upstream
=======

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
BufferedImage image = ImageIO.read(new File("src/main/java/com/example/model/image copy.png"));

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

g2d.drawString("ROL: " + ((user.getRoles().get(0).equals("USER")) ? "Estudiante" : "Empleado"), textX, textY + 60); // Espaciado de 30 píxeles hacia abajo

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
		return null;
	}

	
	
>>>>>>> Stashed changes
    



    


    

    
    

    
}
