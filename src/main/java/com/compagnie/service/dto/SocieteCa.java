package com.compagnie.service.dto;

import java.math.BigDecimal;

public class SocieteCa {
    private Long idSociete;
    private String nom;
    private BigDecimal ca;

    public SocieteCa(Long idSociete, String nom, BigDecimal ca) {
        this.idSociete = idSociete;
        this.nom = nom;
        this.ca = ca;
    }

    public Long getIdSociete() { return idSociete; }
    public String getNom() { return nom; }
    public BigDecimal getCa() { return ca; }
}
