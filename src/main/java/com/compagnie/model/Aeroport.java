package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "aeroport")
public class Aeroport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aeroport")
    private Long idAeroport;
    
    @NotBlank(message = "Le code aeroport est obligatoire")
    @Column(name = "code", unique = true, nullable = false, length = 10)
    private String code;
    
    @Column(name = "nom", length = 100)
    private String nom;
    
    @NotBlank(message = "La ville est obligatoire")
    @Column(name = "ville", length = 100)
    private String ville;
    
    @NotBlank(message = "Le pays est obligatoire")
    @Column(name = "pays", length = 100)
    private String pays;
    
    public Aeroport() {}
    
    public Aeroport(String code, String nom, String ville, String pays) {
        this.code = code;
        this.nom = nom;
        this.ville = ville;
        this.pays = pays;
    }
    
    // Getters et Setters
    public Long getIdAeroport() {
        return idAeroport;
    }
    
    public void setIdAeroport(Long idAeroport) {
        this.idAeroport = idAeroport;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getVille() {
        return ville;
    }
    
    public void setVille(String ville) {
        this.ville = ville;
    }
    
    public String getPays() {
        return pays;
    }
    
    public void setPays(String pays) {
        this.pays = pays;
    }
}

