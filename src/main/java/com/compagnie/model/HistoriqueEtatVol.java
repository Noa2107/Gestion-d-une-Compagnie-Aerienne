package com.compagnie.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_etat_vol")
public class HistoriqueEtatVol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long idHistorique;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etat_vol", nullable = false)
    private EtatVol etatVol;
    
    @Column(name = "date_changement")
    private LocalDateTime dateChangement;
    
    public HistoriqueEtatVol() {
        this.dateChangement = LocalDateTime.now();
    }
    
    public HistoriqueEtatVol(Vol vol, EtatVol etatVol) {
        this.vol = vol;
        this.etatVol = etatVol;
        this.dateChangement = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getIdHistorique() {
        return idHistorique;
    }
    
    public void setIdHistorique(Long idHistorique) {
        this.idHistorique = idHistorique;
    }
    
    public Vol getVol() {
        return vol;
    }
    
    public void setVol(Vol vol) {
        this.vol = vol;
    }
    
    public EtatVol getEtatVol() {
        return etatVol;
    }
    
    public void setEtatVol(EtatVol etatVol) {
        this.etatVol = etatVol;
    }
    
    public LocalDateTime getDateChangement() {
        return dateChangement;
    }
    
    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }
}

