package com.compagnie.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tarif_vol_classe")
public class TarifVolClasse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarif_vol_classe")
    private Long idTarifVolClasse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;

    @Column(name = "classe", length = 20, nullable = false)
    private String classe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_type_passager")
    private TypePassager typePassager;

    @Column(name = "prix_unitaire", precision = 12, scale = 2, nullable = false)
    private BigDecimal prixUnitaire;

    public TarifVolClasse() {
    }

    public Long getIdTarifVolClasse() {
        return idTarifVolClasse;
    }

    public void setIdTarifVolClasse(Long idTarifVolClasse) {
        this.idTarifVolClasse = idTarifVolClasse;
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

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }
}
