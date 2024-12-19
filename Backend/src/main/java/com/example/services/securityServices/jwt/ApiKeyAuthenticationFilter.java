package com.example.services.securityServices.jwt;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "Authorization";
    private static final String API_KEY = "123"; // Reemplaza con tu API Key

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Aplica el filtro solo a las rutas que comienzan con "/api/moodle/"
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/moodle/")) {
            String apiKey = request.getHeader(API_KEY_HEADER);

            if (apiKey != null && apiKey.equals(API_KEY)) {
                // API Key válida, crea un contexto de seguridad
                Authentication authentication = new UsernamePasswordAuthenticationToken("API_USER", null,
                        Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // API Key inválida, devuelve un error 401
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // Si la ruta no requiere la API Key, continua con el siguiente filtro
        filterChain.doFilter(request, response);
    }
}
