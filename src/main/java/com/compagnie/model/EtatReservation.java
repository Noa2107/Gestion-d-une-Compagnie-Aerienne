package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etat_reservation")
public class EtatReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etat_reservation")
    private Long idEtatReservation;
    
    @NotNull(message = "Le code etat est obligatoire")
    @Column(name = "code_etat", unique = true, nullable = false)
    private Integer codeEtat;
    
    @NotNull(message = "La description est obligatoire")
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    
    @OneToMany(mappedBy = "etatReservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatReservation> historiqueEtats = new ArrayList<>();
    
    public EtatReservation() {}
    
    public EtatReservation(Integer codeEtat, String description) {
        this.codeEtat = codeEtat;
        this.description = description;
    }
    
    // Getters et Setters
    public Long getIdEtatReservation() {
        return idEtatReservation;
    }
    
    public void setIdEtatReservation(Long idEtatReservation) {
        this.idEtatReservation = idEtatReservation;
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
    
    public List<HistoriqueEtatReservation> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatReservation> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
}

