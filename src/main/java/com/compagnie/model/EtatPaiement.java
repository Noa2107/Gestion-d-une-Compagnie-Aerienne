package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etat_paiement")
public class EtatPaiement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etat_paiement")
    private Long idEtatPaiement;
    
    @NotNull(message = "Le code etat est obligatoire")
    @Column(name = "code_etat", unique = true, nullable = false)
    private Integer codeEtat;
    
    @NotNull(message = "La description est obligatoire")
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    
    @OneToMany(mappedBy = "etatPaiement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatPaiementReservation> historiqueReservations = new ArrayList<>();
    
    @OneToMany(mappedBy = "etatPaiement", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatPaiementCargo> historiqueCargos = new ArrayList<>();
    
    public EtatPaiement() {}
    
    public EtatPaiement(Integer codeEtat, String description) {
        this.codeEtat = codeEtat;
        this.description = description;
    }
    
    // Getters et Setters
    public Long getIdEtatPaiement() {
        return idEtatPaiement;
    }
    
    public void setIdEtatPaiement(Long idEtatPaiement) {
        this.idEtatPaiement = idEtatPaiement;
    }
    
    public Integer getCodeEtat() {
        return codeEtat;
    }
    
    public void setCodeEtat(Integer codeEtat) {
        this.codeEtat = codeEtat;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<HistoriqueEtatPaiementReservation> getHistoriqueReservations() {
        return historiqueReservations;
    }
    
    public void setHistoriqueReservations(List<HistoriqueEtatPaiementReservation> historiqueReservations) {
        this.historiqueReservations = historiqueReservations;
    }
    
    public List<HistoriqueEtatPaiementCargo> getHistoriqueCargos() {
        return historiqueCargos;
    }
    
    public void setHistoriqueCargos(List<HistoriqueEtatPaiementCargo> historiqueCargos) {
        this.historiqueCargos = historiqueCargos;
    }
}

