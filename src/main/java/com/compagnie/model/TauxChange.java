package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "taux_change")
public class TauxChange {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_taux")
    private Long idTaux;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_devise_source", nullable = false)
    private Devise deviseSource;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_devise_cible", nullable = false)
    private Devise deviseCible;
    
    @NotNull(message = "Le taux est obligatoire")
    @Column(name = "taux", precision = 18, scale = 6)
    private BigDecimal taux;
    
    @Column(name = "date_debut_validite")
    private LocalDateTime dateDebutValidite;
    
    @Column(name = "date_fin_validite")
    private LocalDateTime dateFinValidite;
    
    @Column(name = "est_actif")
    private Boolean estActif = true;
    
    public TauxChange() {
        this.dateDebutValidite = LocalDateTime.now();
    }
    
    public TauxChange(Devise deviseSource, Devise deviseCible, BigDecimal taux) {
        this.deviseSource = deviseSource;
        this.deviseCible = deviseCible;
        this.taux = taux;
        this.dateDebutValidite = LocalDateTime.now();
        this.estActif = true;
    }
    
    // Getters et Setters
    public Long getIdTaux() {
        return idTaux;
    }
    
    public void setIdTaux(Long idTaux) {
        this.idTaux = idTaux;
    }
    
    public Devise getDeviseSource() {
        return deviseSource;
    }
    
    public void setDeviseSource(Devise deviseSource) {
        this.deviseSource = deviseSource;
    }
    
    public Devise getDeviseCible() {
        return deviseCible;
    }
    
    public void setDeviseCible(Devise deviseCible) {
        this.deviseCible = deviseCible;
    }
    
    public BigDecimal getTaux() {
        return taux;
    }
    
    public void setTaux(BigDecimal taux) {
        this.taux = taux;
    }
    
    public LocalDateTime getDateDebutValidite() {
        return dateDebutValidite;
    }
    
    public void setDateDebutValidite(LocalDateTime dateDebutValidite) {
        this.dateDebutValidite = dateDebutValidite;
    }
    
    public LocalDateTime getDateFinValidite() {
        return dateFinValidite;
    }
    
    public void setDateFinValidite(LocalDateTime dateFinValidite) {
        this.dateFinValidite = dateFinValidite;
    }
    
    public Boolean getEstActif() {
        return estActif;
    }
    
    public void setEstActif(Boolean estActif) {
        this.estActif = estActif;
    }
}

