package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paiement_cargo")
public class PaiementCargo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paiement_cargo")
    private Long idPaiementCargo;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cargo", nullable = false)
    private Cargo cargo;
    
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
    
    @Column(name = "type_facturation", length = 20)
    private String typeFacturation;
    
    @Column(name = "reference_facture", length = 50)
    private String referenceFacture;
    
    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    
    @OneToMany(mappedBy = "paiementCargo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatPaiementCargo> historiqueEtats = new ArrayList<>();
    
    public PaiementCargo() {
        this.dateCreation = LocalDateTime.now();
    }
    
    public PaiementCargo(Cargo cargo, Devise devise, BigDecimal montantPaye) {
        this.cargo = cargo;
        this.devise = devise;
        this.montantPaye = montantPaye;
        this.dateCreation = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getIdPaiementCargo() {
        return idPaiementCargo;
    }
    
    public void setIdPaiementCargo(Long idPaiementCargo) {
        this.idPaiementCargo = idPaiementCargo;
    }
    
    public Cargo getCargo() {
        return cargo;
    }
    
    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
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
    
    public String getTypeFacturation() {
        return typeFacturation;
    }
    
    public void setTypeFacturation(String typeFacturation) {
        this.typeFacturation = typeFacturation;
    }
    
    public String getReferenceFacture() {
        return referenceFacture;
    }
    
    public void setReferenceFacture(String referenceFacture) {
        this.referenceFacture = referenceFacture;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public List<HistoriqueEtatPaiementCargo> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatPaiementCargo> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
}

