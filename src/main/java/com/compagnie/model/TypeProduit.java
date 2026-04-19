package com.compagnie.model;

import jakarta.persistence.*;

@Entity
@Table(name = "type_produit")
public class TypeProduit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_produit")
    private Long idTypeProduit;

    @Column(name = "nom_type_produit", nullable = false, unique = true, length = 100)
    private String nomTypeProduit;

    @Column(name = "description", length = 255)
    private String description;

    public TypeProduit() {
    }

    public Long getIdTypeProduit() {
        return idTypeProduit;
    }

    public void setIdTypeProduit(Long idTypeProduit) {
        this.idTypeProduit = idTypeProduit;
    }

    public String getNomTypeProduit() {
        return nomTypeProduit;
    }

    public void setNomTypeProduit(String nomTypeProduit) {
        this.nomTypeProduit = nomTypeProduit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
