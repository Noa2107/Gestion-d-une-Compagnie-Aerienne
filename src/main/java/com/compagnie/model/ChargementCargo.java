package com.compagnie.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chargement_cargo")
public class ChargementCargo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chargement")
    private Long idChargement;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_cargo", nullable = false)
    private Cargo cargo;
    
    public ChargementCargo() {}
    
    public ChargementCargo(Vol vol, Cargo cargo) {
        this.vol = vol;
        this.cargo = cargo;
    }
    
    // Getters et Setters
    public Long getIdChargement() {
        return idChargement;
    }
    
    public void setIdChargement(Long idChargement) {
        this.idChargement = idChargement;
    }
    
    public Vol getVol() {
        return vol;
    }
    
    public void setVol(Vol vol) {
        this.vol = vol;
    }
    
    public Cargo getCargo() {
        return cargo;
    }
    
    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }
}

