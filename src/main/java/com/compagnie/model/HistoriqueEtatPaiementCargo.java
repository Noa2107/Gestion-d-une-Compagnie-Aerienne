package com.compagnie.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique_etat_paiement_cargo")
public class HistoriqueEtatPaiementCargo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historique")
    private Long idHistorique;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paiement_cargo", nullable = false)
    private PaiementCargo paiementCargo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_etat_paiement", nullable = false)
    private EtatPaiement etatPaiement;
    
    @Column(name = "date_changement")
    private LocalDateTime dateChangement;
    
    public HistoriqueEtatPaiementCargo() {
        this.dateChangement = LocalDateTime.now();
    }
    
    public HistoriqueEtatPaiementCargo(PaiementCargo paiementCargo, EtatPaiement etatPaiement) {
        this.paiementCargo = paiementCargo;
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
    
    public PaiementCargo getPaiementCargo() {
        return paiementCargo;
    }
    
    public void setPaiementCargo(PaiementCargo paiementCargo) {
        this.paiementCargo = paiementCargo;
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

