package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paiement_reservation")
public class PaiementReservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement_reservation")
    private Long idPaiementReservation;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_reservation", nullable = false)
    private Reservation reservation;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_devise", nullable = false)
    private Devise devise;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_taux")
    private TauxChange taux;
    
    @Column(name = "montant_reference_mga", precision = 12, scale = 2)
    private BigDecimal montantReferenceMga;
    
    @NotNull(message = "Le montant paye est obligatoire")
    @Column(name = "montant_paye", precision = 12, scale = 2, nullable = false)
    private BigDecimal montantPaye;
    
    @Column(name = "mode_paiement", length = 20)
    private String modePaiement;
    
    @Column(name = "reference_transaction", length = 50)
    private String referenceTransaction;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @OneToMany(mappedBy = "paiementReservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatPaiementReservation> historiqueEtats = new ArrayList<>();
    
    public PaiementReservation() {
        this.dateCreation = LocalDateTime.now();
    }
    
    public PaiementReservation(Reservation reservation, Devise devise, BigDecimal montantPaye) {
        this.reservation = reservation;
        this.devise = devise;
        this.montantPaye = montantPaye;
        this.dateCreation = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getIdPaiementReservation() {
        return idPaiementReservation;
    }
    
    public void setIdPaiementReservation(Long idPaiementReservation) {
        this.idPaiementReservation = idPaiementReservation;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public Devise getDevise() {
        return devise;
    }
    
    public void setDevise(Devise devise) {
        this.devise = devise;
    }
    
    public TauxChange getTaux() {
        return taux;
    }
    
    public void setTaux(TauxChange taux) {
        this.taux = taux;
    }
    
    public BigDecimal getMontantReferenceMga() {
        return montantReferenceMga;
    }
    
    public void setMontantReferenceMga(BigDecimal montantReferenceMga) {
        this.montantReferenceMga = montantReferenceMga;
    }
    
    public BigDecimal getMontantPaye() {
        return montantPaye;
    }
    
    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }
    
    public String getModePaiement() {
        return modePaiement;
    }
    
    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }
    
    public String getReferenceTransaction() {
        return referenceTransaction;
    }
    
    public void setReferenceTransaction(String referenceTransaction) {
        this.referenceTransaction = referenceTransaction;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public List<HistoriqueEtatPaiementReservation> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatPaiementReservation> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
}

