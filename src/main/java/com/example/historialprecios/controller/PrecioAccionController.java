package com.example.historialprecios.controller;

import com.example.historialprecios.model.PrecioAccion;
import com.example.historialprecios.service.PrecioAccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class PrecioAccionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final PrecioAccionService service;

    public PrecioAccionController(PrecioAccionService service) {
        this.service = service;
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok(Map.of("status", "ok", "db", "connected"));
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of("status", "error", "db", e.getMessage()));
        }
    }

    @GetMapping("/api/precios/{simbolo}")
    public ResponseEntity<List<PrecioAccion>> getPrecios(@PathVariable String simbolo) {
        List<PrecioAccion> precios = service.getPreciosPorSimbolo(simbolo);
        return ResponseEntity.ok(precios);
    }

    @GetMapping("/api/precios/{simbolo}/latest")
    public ResponseEntity<PrecioAccion> getUltimoPrecio(@PathVariable String simbolo) {
        return service.getUltimoPrecio(simbolo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/precios/{simbolo}/range")
    public ResponseEntity<List<PrecioAccion>> getPreciosRango(
            @PathVariable String simbolo,
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fin) {
        List<PrecioAccion> precios = service.getPreciosRango(simbolo, inicio, fin);
        return ResponseEntity.ok(precios);
    }

    @PostMapping("/api/precios")
    public ResponseEntity<PrecioAccion> crearPrecio(@RequestBody PrecioAccion precio) {
        PrecioAccion guardado = service.guardarPrecio(precio);
        return ResponseEntity.ok(guardado);
    }

    @DeleteMapping("/api/precios/{simbolo}")
    public ResponseEntity<Void> eliminarPrecios(@PathVariable String simbolo) {
        service.eliminarPrecios(simbolo);
        return ResponseEntity.noContent().build();
    }
}
