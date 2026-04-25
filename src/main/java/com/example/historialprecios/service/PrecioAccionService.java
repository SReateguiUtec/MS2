package com.example.historialprecios.service;

import com.example.historialprecios.model.PrecioAccion;
import com.example.historialprecios.repository.PrecioAccionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PrecioAccionService {

    private final PrecioAccionRepository repository;

    public PrecioAccionService(PrecioAccionRepository repository) {
        this.repository = repository;
    }
    
    public List<PrecioAccion> getPreciosPorSimbolo(String simbolo) {
        return repository.findBySimboloOrderByFechaDesc(simbolo);
    }
    
    public List<PrecioAccion> getPreciosRango(String simbolo, LocalDateTime inicio, LocalDateTime fin) {
        return repository.findBySimboloAndFechaBetween(simbolo, inicio, fin);
    }
    
    public Optional<PrecioAccion> getUltimoPrecio(String simbolo) {
        return repository.findLatestBySimbolo(simbolo);
    }
    
    public PrecioAccion guardarPrecio(PrecioAccion precio) {
        return repository.save(precio);
    }
    
    public void eliminarPrecios(String simbolo) {
        repository.findBySimboloOrderByFechaDesc(simbolo).forEach(repository::delete);
    }
}