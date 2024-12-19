package com.example.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.News;
import com.example.services.NewService;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controlador REST para la gestión de noticias.
 * Proporciona un endpoint para obtener una página de noticias ordenadas por fecha de publicación.
 */
@Tag(name = "Noticias", description = "API para la consulta de noticias")
@RestController
@RequestMapping("/api/v1/news")
public class NewsController {

    @Autowired
    private NewService service;

    /**
     * Obtiene una página de noticias ordenadas por fecha de publicación, de las más
     * recientes a las más antiguas.
     *
     * @param pageNumber Número de página a obtener (comenzando desde 0).
     * @return Una ResponseEntity que contiene la lista de noticias de la página
     *         solicitada.
     */
    @Operation(summary = "Obtener noticias paginadas", description = "Devuelve una lista paginada de noticias ordenadas por fecha de publicación, de las más recientes a las más antiguas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de noticias", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = News.class))
            })
    })
    @GetMapping
    public ResponseEntity<List<News>> getNewsPageableNewer(
            @Parameter(description = "Número de página a obtener (comenzando desde 0)", required = true) @RequestParam int pageNumber) {
        return ResponseEntity.ok(service.getAllNewer(pageNumber));
    }
}