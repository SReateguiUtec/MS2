package com.example.historialprecios.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "precios_acciones", indexes = {
    @Index(name = "idx_simbolo_fecha", columnList = "simbolo, fecha")
})
public class PrecioAccion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 10)
    private String simbolo;
    
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal open;
    
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal close;
    
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal high;
    
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal low;
    
    @Column(nullable = false)
    private Long volumen;
    
    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(name = "es_premium", nullable = false)
    private Boolean esPremium = false;

    // Relación N:1 — muchos precios pertenecen a un símbolo
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simbolo_id", referencedColumnName = "id")
    private Simbolo simboloRef;

    public PrecioAccion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSimbolo() { return simbolo; }
    public void setSimbolo(String simbolo) { this.simbolo = simbolo; }

    public BigDecimal getOpen() { return open; }
    public void setOpen(BigDecimal open) { this.open = open; }

    public BigDecimal getClose() { return close; }
    public void setClose(BigDecimal close) { this.close = close; }

    public BigDecimal getHigh() { return high; }
    public void setHigh(BigDecimal high) { this.high = high; }

    public BigDecimal getLow() { return low; }
    public void setLow(BigDecimal low) { this.low = low; }

    public Long getVolumen() { return volumen; }
    public void setVolumen(Long volumen) { this.volumen = volumen; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public Boolean getEsPremium() { return esPremium; }
    public void setEsPremium(Boolean esPremium) { this.esPremium = esPremium; }

    public Simbolo getSimboloRef() { return simboloRef; }
    public void setSimboloRef(Simbolo simboloRef) { this.simboloRef = simboloRef; }
}