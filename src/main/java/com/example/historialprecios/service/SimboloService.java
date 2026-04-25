package com.example.historialprecios.service;

import com.example.historialprecios.model.Simbolo;
import com.example.historialprecios.repository.SimboloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SimboloService {

    private final SimboloRepository repository;

    public SimboloService(SimboloRepository repository) {
        this.repository = repository;
    }

    public List<Simbolo> getSimbolosActivos() {
        return repository.findByActivoTrue();
    }

    public List<Simbolo> getTodos() {
        return repository.findAll();
    }

    public Optional<Simbolo> getBySimbolo(String simbolo) {
        return repository.findBySimbolo(simbolo.toUpperCase());
    }

    public List<Simbolo> getBySector(String sector) {
        return repository.findBySector(sector);
    }

    public Simbolo guardar(Simbolo simbolo) {
        simbolo.setSimbolo(simbolo.getSimbolo().toUpperCase());
        return repository.save(simbolo);
    }

    public Optional<Simbolo> desactivar(String simbolo) {
        return repository.findBySimbolo(simbolo.toUpperCase()).map(s -> {
            s.setActivo(false);
            return repository.save(s);
        });
    }
}
