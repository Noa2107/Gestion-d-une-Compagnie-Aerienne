package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "avion")
public class Avion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avion")
    private Long idAvion;
    
    @NotBlank(message = "L'immatriculation est obligatoire")
    @Column(name = "immatriculation", unique = true, nullable = false, length = 50)
    private String immatriculation;
    
    @Column(name = "modele", length = 50)
    private String modele;
    
    @Column(name = "capacite_passagers")
    private Integer capacitePassagers;
    
    @NotNull(message = "Le nombre de places economique est obligatoire")
    @Min(value = 0, message = "Le nombre de places economique doit etre positif")
    @Column(name = "nb_places_economique")
    private Integer nbPlacesEconomique;
    
    @NotNull(message = "Le nombre de places premiere est obligatoire")
    @Min(value = 0, message = "Le nombre de places premiere doit etre positif")
    @Column(name = "nb_places_premiere")
    private Integer nbPlacesPremiere;

    @NotNull(message = "Le nombre de places premium est obligatoire")
    @Min(value = 0, message = "Le nombre de places premium doit etre positif")
    @Column(name = "nb_places_premium")
    private Integer nbPlacesPremium;
    
    @NotNull(message = "La capacite cargo est obligatoire")
    @Positive(message = "La capacite cargo doit etre positive")
    @Column(name = "capacite_cargo")
    private Integer capaciteCargo;
    
    @OneToMany(mappedBy = "avion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatAvion> historiqueEtats = new ArrayList<>();
    
    @OneToMany(mappedBy = "avion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vol> vols = new ArrayList<>();
    
    public Avion() {}
    
    public Avion(String immatriculation, String modele, Integer nbPlacesEconomique, Integer nbPlacesPremium, Integer nbPlacesPremiere, Integer capaciteCargo) {
        this.immatriculation = immatriculation;
        this.modele = modele;
        this.nbPlacesEconomique = nbPlacesEconomique;
        this.nbPlacesPremium = nbPlacesPremium;
        this.nbPlacesPremiere = nbPlacesPremiere;
        this.capaciteCargo = capaciteCargo;
    }
    
    // Getters et Setters
    public Long getIdAvion() {
        return idAvion;
    }
    
    public void setIdAvion(Long idAvion) {
        this.idAvion = idAvion;
    }
    
    public String getImmatriculation() {
        return immatriculation;
    }
    
    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }
    
    public String getModele() {
        return modele;
    }
    
    public void setModele(String modele) {
        this.modele = modele;
    }
    
    public Integer getCapacitePassagers() {
        return calculateCapacitePassagers();
    }

    private Integer calculateCapacitePassagers() {
        int eco = nbPlacesEconomique == null ? 0 : nbPlacesEconomique;
        int premium = nbPlacesPremium == null ? 0 : nbPlacesPremium;
        int prem = nbPlacesPremiere == null ? 0 : nbPlacesPremiere;
        int total = eco + premium + prem;
        return total <= 0 ? capacitePassagers : total;
    }

    @PrePersist
    @PreUpdate
    private void syncCapacitePassagers() {
        this.capacitePassagers = calculateCapacitePassagers();
    }

    public Integer getNbPlacesEconomique() {
        return nbPlacesEconomique;
    }

    public void setNbPlacesEconomique(Integer nbPlacesEconomique) {
        this.nbPlacesEconomique = nbPlacesEconomique;
    }

    public Integer getNbPlacesPremiere() {
        return nbPlacesPremiere;
    }

    public void setNbPlacesPremiere(Integer nbPlacesPremiere) {
        this.nbPlacesPremiere = nbPlacesPremiere;
    }

    public Integer getNbPlacesPremium() {
        return nbPlacesPremium;
    }

    public void setNbPlacesPremium(Integer nbPlacesPremium) {
        this.nbPlacesPremium = nbPlacesPremium;
    }
    
    public Integer getCapaciteCargo() {
        return capaciteCargo;
    }
    
    public void setCapaciteCargo(Integer capaciteCargo) {
        this.capaciteCargo = capaciteCargo;
    }
    
    public List<HistoriqueEtatAvion> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatAvion> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
    
    public List<Vol> getVols() {
        return vols;
    }
    
    public void setVols(List<Vol> vols) {
        this.vols = vols;
    }
}

