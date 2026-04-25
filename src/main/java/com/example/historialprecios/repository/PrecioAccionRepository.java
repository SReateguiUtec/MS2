package com.example.historialprecios.repository;

import com.example.historialprecios.model.PrecioAccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PrecioAccionRepository extends JpaRepository<PrecioAccion, Long> {
    
    List<PrecioAccion> findBySimboloOrderByFechaDesc(String simbolo);
    
    @Query("SELECT p FROM PrecioAccion p WHERE p.simbolo = :simbolo AND p.fecha <= :fecha ORDER BY p.fecha DESC")
    List<PrecioAccion> findBySimboloAndFechaBefore(@Param("simbolo") String simbolo, @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT p FROM PrecioAccion p WHERE p.simbolo = :simbolo AND p.fecha BETWEEN :inicio AND :fin ORDER BY p.fecha ASC")
    List<PrecioAccion> findBySimboloAndFechaBetween(
        @Param("simbolo") String simbolo, 
        @Param("inicio") LocalDateTime inicio, 
        @Param("fin") LocalDateTime fin
    );
    
    @Query("SELECT p FROM PrecioAccion p WHERE p.simbolo = :simbolo ORDER BY p.fecha DESC LIMIT 1")
    Optional<PrecioAccion> findLatestBySimbolo(@Param("simbolo") String simbolo);
    
    @Query("SELECT p FROM PrecioAccion p WHERE p.esPremium = false AND p.simbolo = :simbolo ORDER BY p.fecha DESC")
    List<PrecioAccion> findFreeBySimbolo(@Param("simbolo") String simbolo);
    
    @Query("SELECT COUNT(p) FROM PrecioAccion p WHERE p.simbolo = :simbolo")
    Long countBySimbolo(@Param("simbolo") String simbolo);
}