package com.compagnie.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_etat_cargo")
public class HistoriqueEtatCargo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long idHistorique;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cargo", nullable = false)
    private Cargo cargo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etat_cargo", nullable = false)
    private EtatCargo etatCargo;
    
    @Column(name = "date_changement")
    private LocalDateTime dateChangement;
    
    public HistoriqueEtatCargo() {
        this.dateChangement = LocalDateTime.now();
    }
    
    public HistoriqueEtatCargo(Cargo cargo, EtatCargo etatCargo) {
        this.cargo = cargo;
        this.etatCargo = etatCargo;
        this.dateChangement = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getIdHistorique() {
        return idHistorique;
    }
    
    public void setIdHistorique(Long idHistorique) {
        this.idHistorique = idHistorique;
    }
    
    public Cargo getCargo() {
        return cargo;
    }
    
    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
    
    public EtatCargo getEtatCargo() {
        return etatCargo;
    }
    
    public void setEtatCargo(EtatCargo etatCargo) {
        this.etatCargo = etatCargo;
    }
    
    public LocalDateTime getDateChangement() {
        return dateChangement;
    }
    
    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }
}

