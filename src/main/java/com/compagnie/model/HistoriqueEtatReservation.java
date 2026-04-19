package com.compagnie.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_etat_reservation")
public class HistoriqueEtatReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long idHistorique;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation", nullable = false)
    private Reservation reservation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etat_reservation", nullable = false)
    private EtatReservation etatReservation;
    
    @Column(name = "date_changement")
    private LocalDateTime dateChangement;
    
    public HistoriqueEtatReservation() {
        this.dateChangement = LocalDateTime.now();
    }
    
    public HistoriqueEtatReservation(Reservation reservation, EtatReservation etatReservation) {
        this.reservation = reservation;
        this.etatReservation = etatReservation;
        this.dateChangement = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getIdHistorique() {
        return idHistorique;
    }
    
    public void setIdHistorique(Long idHistorique) {
        this.idHistorique = idHistorique;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public EtatReservation getEtatReservation() {
        return etatReservation;
    }
    
    public void setEtatReservation(EtatReservation etatReservation) {
        this.etatReservation = etatReservation;
    }
    
    public LocalDateTime getDateChangement() {
        return dateChangement;
    }
    
    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }
}

