package com.example.historialprecios.repository;

import com.example.historialprecios.model.Simbolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SimboloRepository extends JpaRepository<Simbolo, Long> {

    Optional<Simbolo> findBySimbolo(String simbolo);

    List<Simbolo> findByActivoTrue();

    List<Simbolo> findBySector(String sector);

    boolean existsBySimbolo(String simbolo);
}
