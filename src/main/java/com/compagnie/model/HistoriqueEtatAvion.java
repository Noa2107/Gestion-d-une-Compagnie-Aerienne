package com.compagnie.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_etat_avion")
public class HistoriqueEtatAvion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long idHistorique;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_avion", nullable = false)
    private Avion avion;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etat_avion", nullable = false)
    private EtatAvion etatAvion;
    
    @Column(name = "date_changement")
    private LocalDateTime dateChangement;
    
    public HistoriqueEtatAvion() {
        this.dateChangement = LocalDateTime.now();
    }
    
    public HistoriqueEtatAvion(Avion avion, EtatAvion etatAvion) {
        this.avion = avion;
        this.etatAvion = etatAvion;
        this.dateChangement = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getIdHistorique() {
        return idHistorique;
    }
    
    public void setIdHistorique(Long idHistorique) {
        this.idHistorique = idHistorique;
    }
    
    public Avion getAvion() {
        return avion;
    }
    
    public void setAvion(Avion avion) {
        this.avion = avion;
    }
    
    public EtatAvion getEtatAvion() {
        return etatAvion;
    }
    
    public void setEtatAvion(EtatAvion etatAvion) {
        this.etatAvion = etatAvion;
    }
    
    public LocalDateTime getDateChangement() {
        return dateChangement;
    }
    
    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }
}

