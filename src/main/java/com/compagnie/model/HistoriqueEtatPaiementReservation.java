package com.compagnie.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_etat_paiement_reservation")
public class HistoriqueEtatPaiementReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long idHistorique;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paiement_reservation", nullable = false)
    private PaiementReservation paiementReservation;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etat_paiement", nullable = false)
    private EtatPaiement etatPaiement;
    
    @Column(name = "date_changement")
    private LocalDateTime dateChangement;
    
    public HistoriqueEtatPaiementReservation() {
        this.dateChangement = LocalDateTime.now();
    }
    
    public HistoriqueEtatPaiementReservation(PaiementReservation paiementReservation, EtatPaiement etatPaiement) {
        this.paiementReservation = paiementReservation;
        this.etatPaiement = etatPaiement;
        this.dateChangement = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getIdHistorique() {
        return idHistorique;
    }
    
    public void setIdHistorique(Long idHistorique) {
        this.idHistorique = idHistorique;
    }
    
    public PaiementReservation getPaiementReservation() {
        return paiementReservation;
    }
    
    public void setPaiementReservation(PaiementReservation paiementReservation) {
        this.paiementReservation = paiementReservation;
    }
    
    public EtatPaiement getEtatPaiement() {
        return etatPaiement;
    }
    
    public void setEtatPaiement(EtatPaiement etatPaiement) {
        this.etatPaiement = etatPaiement;
    }
    
    public LocalDateTime getDateChangement() {
        return dateChangement;
    }
    
    public void setDateChangement(LocalDateTime dateChangement) {
        this.dateChangement = dateChangement;
    }
}

