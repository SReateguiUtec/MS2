package com.example.historialprecios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "simbolos", indexes = {
    @Index(name = "idx_simbolo_unique", columnList = "simbolo", unique = true)
})
public class Simbolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String simbolo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 80)
    private String sector;

    @Column(length = 80)
    private String industria;

    @Column(length = 20)
    private String bolsa;      // NYSE, NASDAQ, etc.

    @Column(length = 50)
    private String pais;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relación 1:N — un símbolo tiene muchos precios históricos
    @OneToMany(mappedBy = "simboloRef", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PrecioAccion> precios;

    public Simbolo() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSimbolo() { return simbolo; }
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public String getIndustria() { return industria; }
    public void setIndustria(String industria) { this.industria = industria; }

    public String getBolsa() { return bolsa; }
    public void setBolsa(String bolsa) { this.bolsa = bolsa; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<PrecioAccion> getPrecios() { return precios; }
    public void setPrecios(List<PrecioAccion> precios) { this.precios = precios; }
}
