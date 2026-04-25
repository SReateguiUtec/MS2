package com.example.historialprecios.controller;

import com.example.historialprecios.model.Simbolo;
import com.example.historialprecios.service.SimboloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/simbolos")
public class SimboloController {

    private final SimboloService service;

    public SimboloController(SimboloService service) {
        this.service = service;
    }

    // GET /api/simbolos  →  lista todos los símbolos activos
    @GetMapping
    public ResponseEntity<List<Simbolo>> getSimbolosActivos() {
        return ResponseEntity.ok(service.getSimbolosActivos());
    }

    // GET /api/simbolos/all  →  lista todos incluyendo inactivos
    @GetMapping("/all")
    public ResponseEntity<List<Simbolo>> getTodos() {
        return ResponseEntity.ok(service.getTodos());
    }

    // GET /api/simbolos/{simbolo}  →  detalle de un símbolo (ej: AAPL)
    @GetMapping("/{simbolo}")
    public ResponseEntity<Simbolo> getBySimbolo(@PathVariable String simbolo) {
        return service.getBySimbolo(simbolo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/simbolos/sector/{sector}  →  todos los símbolos de un sector
    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<Simbolo>> getBySector(@PathVariable String sector) {
        return ResponseEntity.ok(service.getBySector(sector));
    }

    // POST /api/simbolos  →  registrar un nuevo símbolo
    @PostMapping
    public ResponseEntity<Simbolo> crear(@RequestBody Simbolo simbolo) {
        Simbolo guardado = service.guardar(simbolo);
        return ResponseEntity.status(201).body(guardado);
    }

    // PUT /api/simbolos/{simbolo}  →  actualizar datos de un símbolo
    @PutMapping("/{simbolo}")
    public ResponseEntity<Simbolo> actualizar(
            @PathVariable String simbolo,
            @RequestBody Simbolo datos) {
        return service.getBySimbolo(simbolo).map(s -> {
            if (datos.getNombre()    != null) s.setNombre(datos.getNombre());
            if (datos.getSector()    != null) s.setSector(datos.getSector());
            if (datos.getIndustria() != null) s.setIndustria(datos.getIndustria());
            if (datos.getBolsa()     != null) s.setBolsa(datos.getBolsa());
            if (datos.getPais()      != null) s.setPais(datos.getPais());
            if (datos.getActivo()    != null) s.setActivo(datos.getActivo());
            return ResponseEntity.ok(service.guardar(s));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/simbolos/{simbolo}  →  desactivar símbolo (soft delete)
    @DeleteMapping("/{simbolo}")
    public ResponseEntity<Simbolo> desactivar(@PathVariable String simbolo) {
        return service.desactivar(simbolo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
