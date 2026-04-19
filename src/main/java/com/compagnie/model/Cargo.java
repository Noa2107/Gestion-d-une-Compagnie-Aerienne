package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cargo")
public class Cargo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cargo")
    private Long idCargo;
    
    @Column(name = "reference", length = 50)
    private String reference;
    
    @NotNull(message = "Le poids est obligatoire")
    @Positive(message = "Le poids doit etre positif")
    @Column(name = "poids", nullable = false)
    private Integer poids;
    
    @Column(name = "type_marchandise", length = 50)
    private String typeMarchandise;
    
    @Column(name = "expediteur", length = 100)
    private String expediteur;
    
    @Column(name = "destinataire", length = 100)
    private String destinataire;
    
    @Column(name = "adresse_depart", length = 150)
    private String adresseDepart;
    
    @Column(name = "adresse_arrivee", length = 150)
    private String adresseArrivee;
    
    @Column(name = "valeur_declaree", precision = 12, scale = 2)
    private BigDecimal valeurDeclaree;
    
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatCargo> historiqueEtats = new ArrayList<>();
    
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChargementCargo> chargements = new ArrayList<>();
    
    @OneToMany(mappedBy = "cargo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaiementCargo> paiements = new ArrayList<>();
    
    public Cargo() {}
    
    public Cargo(String reference, Integer poids, String typeMarchandise) {
        this.reference = reference;
        this.poids = poids;
        this.typeMarchandise = typeMarchandise;
    }
    
    // Getters et Setters
    public Long getIdCargo() {
        return idCargo;
    }
    
    public void setIdCargo(Long idCargo) {
        this.idCargo = idCargo;
    }
    
    public String getReference() {
        return reference;
    }
    
    public void setReference(String reference) {
        this.reference = reference;
    }
    
    public Integer getPoids() {
        return poids;
    }
    
    public void setPoids(Integer poids) {
        this.poids = poids;
    }
    
    public String getTypeMarchandise() {
        return typeMarchandise;
    }
    
    public void setTypeMarchandise(String typeMarchandise) {
        this.typeMarchandise = typeMarchandise;
    }
    
    public String getExpediteur() {
        return expediteur;
    }
    
    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }
    
    public String getDestinataire() {
        return destinataire;
    }
    
    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }
    
    public String getAdresseDepart() {
        return adresseDepart;
    }
    
    public void setAdresseDepart(String adresseDepart) {
        this.adresseDepart = adresseDepart;
    }
    
    public String getAdresseArrivee() {
        return adresseArrivee;
    }
    
    public void setAdresseArrivee(String adresseArrivee) {
        this.adresseArrivee = adresseArrivee;
    }
    
    public BigDecimal getValeurDeclaree() {
        return valeurDeclaree;
    }
    
    public void setValeurDeclaree(BigDecimal valeurDeclaree) {
        this.valeurDeclaree = valeurDeclaree;
    }
    
    public List<HistoriqueEtatCargo> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatCargo> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
    
    public List<ChargementCargo> getChargements() {
        return chargements;
    }
    
    public void setChargements(List<ChargementCargo> chargements) {
        this.chargements = chargements;
    }
    
    public List<PaiementCargo> getPaiements() {
        return paiements;
    }
    
    public void setPaiements(List<PaiementCargo> paiements) {
        this.paiements = paiements;
    }
}

