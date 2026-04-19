package com.compagnie.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "produit_extra")
public class ProduitExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produit_extra")
    private Long idProduitExtra;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_type_produit", nullable = false)
    private TypeProduit typeProduit;

    @Column(name = "nom_produit", nullable = false, length = 150)
    private String nomProduit;

    @Column(name = "prix_unitaire", nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "quantite_mensuelle", nullable = false)
    private Integer quantiteMensuelle;

    @Column(name = "mois", nullable = false)
    private Integer mois;

    @Column(name = "annee", nullable = false)
    private Integer annee;

    public ProduitExtra() {
    }

    public Long getIdProduitExtra() {
        return idProduitExtra;
    }

    public void setIdProduitExtra(Long idProduitExtra) {
        this.idProduitExtra = idProduitExtra;
    }

    public TypeProduit getTypeProduit() {
        return typeProduit;
    }

    public void setTypeProduit(TypeProduit typeProduit) {
        this.typeProduit = typeProduit;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Integer getQuantiteMensuelle() {
        return quantiteMensuelle;
    }

    public void setQuantiteMensuelle(Integer quantiteMensuelle) {
        this.quantiteMensuelle = quantiteMensuelle;
    }

    public Integer getMois() {
        return mois;
    }

    public void setMois(Integer mois) {
        this.mois = mois;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }
}
