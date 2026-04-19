package com.compagnie.service.dto;

import java.math.BigDecimal;

public class SocieteReglement {
    private Long idSociete;
    private String nom;
    private BigDecimal ca;
    private BigDecimal paye;
    private BigDecimal reste;

    public SocieteReglement(Long idSociete, String nom, BigDecimal ca, BigDecimal paye) {
        this.idSociete = idSociete;
        this.nom = nom;
        this.ca = ca != null ? ca : BigDecimal.ZERO;
        this.paye = paye != null ? paye : BigDecimal.ZERO;
        this.reste = this.ca.subtract(this.paye);
    }

    public Long getIdSociete() { return idSociete; }
    public String getNom() { return nom; }
    public BigDecimal getCa() { return ca; }
    public BigDecimal getPaye() { return paye; }
    public BigDecimal getReste() { return reste; }
}
