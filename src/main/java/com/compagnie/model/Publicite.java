package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "publicite")
public class Publicite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicite")
    private Long idPublicite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_societe", nullable = false)
    private Societe societe;

    @Column(name = "mois", nullable = false)
    @Min(1)
    @Max(12)
    private Integer mois; // 1..12

    @Column(name = "annee", nullable = false)
    @Min(2000)
    private Integer annee;

    @Min(0)
    @Column(name = "nombre_diffusions", nullable = false)
    private Integer nombreDiffusions;

    @Column(name = "prix_unitaire", nullable = false, precision = 18, scale = 2)
    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal prixUnitaire;

    public Publicite() {}

    public Long getIdPublicite() { return idPublicite; }
    public void setIdPublicite(Long idPublicite) { this.idPublicite = idPublicite; }

    public Societe getSociete() { return societe; }
    public void setSociete(Societe societe) { this.societe = societe; }

    public Integer getMois() { return mois; }
    public void setMois(Integer mois) { this.mois = mois; }

    public Integer getAnnee() { return annee; }
    public void setAnnee(Integer annee) { this.annee = annee; }

    public Integer getNombreDiffusions() { return nombreDiffusions; }
    public void setNombreDiffusions(Integer nombreDiffusions) { this.nombreDiffusions = nombreDiffusions; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    @Transient
    public BigDecimal getCa() {
        if (prixUnitaire == null || nombreDiffusions == null) return BigDecimal.ZERO;
        return prixUnitaire.multiply(BigDecimal.valueOf(nombreDiffusions));
    }
}
