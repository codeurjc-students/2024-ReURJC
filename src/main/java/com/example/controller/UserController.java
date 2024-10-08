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
    



    


    

    
    

    
}
