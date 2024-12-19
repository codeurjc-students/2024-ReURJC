package com.example.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Festive;
import com.example.services.FestiveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para la gestión de días festivos.
 * Proporciona un endpoint para consultar todos los días festivos.
 */
@Tag(name = "Días Festivos", description = "API para la consulta de días festivos")
@RestController
@RequestMapping("/api/v1/festives")
public class FestivesController {

    @Autowired
    private FestiveService service;

    /**
     * Obtiene todos los días festivos.
     *
     * @return Una ResponseEntity que contiene la lista de todos los días festivos.
     */
    @Operation(summary = "Obtener todos los días festivos", description = "Devuelve una lista de todos los días festivos registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de días festivos", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Festive.class))
            })
    })
    @GetMapping
    public ResponseEntity<List<Festive>> getMethodName() {
        return ResponseEntity.ok(service.getAll());
    }
}