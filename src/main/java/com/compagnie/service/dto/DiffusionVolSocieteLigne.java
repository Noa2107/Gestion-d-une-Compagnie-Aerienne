package com.compagnie.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DiffusionVolSocieteLigne {
    private Integer annee;
    private Integer mois;
    private Long idVol;
    private String aeroportDepartCode;
    private String aeroportArriveeCode;
    private String avionImmatriculation;
    private LocalDateTime dateVol;
    private Long idSociete;
    private String societeNom;
    private Integer nbDiffusions;
    private BigDecimal prixUnitaire;
    private BigDecimal montant;

    public DiffusionVolSocieteLigne(Integer annee, Integer mois,
                                   Long idVol, String aeroportDepartCode, String aeroportArriveeCode,
                                   String avionImmatriculation, LocalDateTime dateVol,
                                   Long idSociete, String societeNom,
                                   Integer nbDiffusions, BigDecimal prixUnitaire, BigDecimal montant) {
        this.annee = annee;
        this.mois = mois;
        this.idVol = idVol;
        this.aeroportDepartCode = aeroportDepartCode;
        this.aeroportArriveeCode = aeroportArriveeCode;
        this.avionImmatriculation = avionImmatriculation;
        this.dateVol = dateVol;
        this.idSociete = idSociete;
        this.societeNom = societeNom;
        this.nbDiffusions = nbDiffusions;
        this.prixUnitaire = prixUnitaire;
        this.montant = montant;
    }

    public Integer getAnnee() { return annee; }
    public Integer getMois() { return mois; }
    public Long getIdVol() { return idVol; }
    public String getAeroportDepartCode() { return aeroportDepartCode; }
    public String getAeroportArriveeCode() { return aeroportArriveeCode; }
    public String getAvionImmatriculation() { return avionImmatriculation; }
    public LocalDateTime getDateVol() { return dateVol; }
    public Long getIdSociete() { return idSociete; }
    public String getSocieteNom() { return societeNom; }
    public Integer getNbDiffusions() { return nbDiffusions; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public BigDecimal getMontant() { return montant; }
}
