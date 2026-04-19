package com.compagnie.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_tarif")
public class PromotionTarif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_promotion_tarif")
    private Long idPromotionTarif;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;

    @Column(name = "classe", length = 20, nullable = false)
    private String classe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_type_passager")
    private TypePassager typePassager;

    @Column(name = "type_reduction", length = 20, nullable = false)
    private String typeReduction; // POURCENTAGE ou MONTANT

    @Column(name = "valeur", precision = 12, scale = 2, nullable = false)
    private BigDecimal valeur;

    @Column(name = "date_debut")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateFin;

    @Column(name = "est_actif")
    private Boolean estActif;

    public PromotionTarif() {
    }

    public Long getIdPromotionTarif() {
        return idPromotionTarif;
    }

    public void setIdPromotionTarif(Long idPromotionTarif) {
        this.idPromotionTarif = idPromotionTarif;
    }

    public Vol getVol() {
        return vol;
    }

    public void setVol(Vol vol) {
        this.vol = vol;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public TypePassager getTypePassager() {
        return typePassager;
    }

    public void setTypePassager(TypePassager typePassager) {
        this.typePassager = typePassager;
    }

    public String getTypeReduction() {
        return typeReduction;
    }

    public void setTypeReduction(String typeReduction) {
        this.typeReduction = typeReduction;
    }

    public BigDecimal getValeur() {
        return valeur;
    }

    public void setValeur(BigDecimal valeur) {
        this.valeur = valeur;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public Boolean getEstActif() {
        return estActif;
    }

    public void setEstActif(Boolean estActif) {
        this.estActif = estActif;
    }
}
