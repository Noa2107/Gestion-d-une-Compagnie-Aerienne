package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "diffusion_pub_vol")
public class DiffusionPubVol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diffusion_pub_vol")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_societe", nullable = false)
    private Societe societe;

    @Column(name = "mois", nullable = false)
    @Min(1)
    private Integer mois;

    @Column(name = "annee", nullable = false)
    @Min(2000)
    private Integer annee;

    @Column(name = "nb_diffusions", nullable = false)
    @Min(0)
    private Integer nbDiffusions;

    @Column(name = "prix_unitaire", nullable = false, precision = 18, scale = 2)
    @NotNull
    private BigDecimal prixUnitaire;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Vol getVol() { return vol; }
    public void setVol(Vol vol) { this.vol = vol; }
    public Societe getSociete() { return societe; }
    public void setSociete(Societe societe) { this.societe = societe; }
    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }
    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }
    public Integer getNbDiffusions() { return nbDiffusions; }
    public void setNbDiffusions(Integer nbDiffusions) { this.nbDiffusions = nbDiffusions; }
    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }
}
