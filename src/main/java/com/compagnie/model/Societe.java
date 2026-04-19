package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "societe")
public class Societe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_societe")
    private Long idSociete;

    @NotBlank
    @Column(name = "nom", nullable = false, unique = true, length = 150)
    private String nom;

    public Societe() {}

    public Societe(String nom) {
        this.nom = nom;
    }

    public Long getIdSociete() {
        return idSociete;
    }

    public void setIdSociete(Long idSociete) {
        this.idSociete = idSociete;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
