package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para el manejo de la Single Page Application (SPA).
 * Redirige las peticiones a la raíz del sitio web al archivo index.html.
 */
@Tag(name = "SPA", description = "Controlador para la Single Page Application")
@Controller
public class SPAController {
    /**
     * Redirige las peticiones a la raíz del sitio web ("/") al archivo index.html.
     *
     * @return Una cadena que indica la redirección a "forward:/index.html".
     */
    @Operation(summary = "Redirigir a la página principal", description = "Redirige las peticiones a la raíz del sitio web al archivo index.html.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Redirección exitosa a index.html")
    })
    @GetMapping("/")
    public String redirect() {
        return "forward:/index.html";
    }
}