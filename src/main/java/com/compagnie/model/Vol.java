package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vol")
public class Vol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vol")
    private Long idVol;
    
    @Column(name = "code_vol", length = 20)
    private String codeVol;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_avion", nullable = false)
    private Avion avion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_aeroport_depart", nullable = false)
    private Aeroport aeroportDepart;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_aeroport_arrivee", nullable = false)
    private Aeroport aeroportArrivee;
    
    @NotNull(message = "La date du vol est obligatoire")
    @Column(name = "date_vol", nullable = false)
    private LocalDateTime dateVol;
    
    @Column(name = "date_arrivee")
    private LocalDateTime dateArrivee;
    
    @Column(name = "type_vol", length = 20)
    private String typeVol; // PASSAGER, CARGO, MIXTE
    
    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatVol> historiqueEtats = new ArrayList<>();
    
    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();
    
    @OneToMany(mappedBy = "vol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChargementCargo> chargementsCargo = new ArrayList<>();
    
    public Vol() {}
    
    public Vol(Avion avion, Aeroport aeroportDepart, Aeroport aeroportArrivee, 
               LocalDateTime dateVol, String typeVol) {
        this.avion = avion;
        this.aeroportDepart = aeroportDepart;
        this.aeroportArrivee = aeroportArrivee;
        this.dateVol = dateVol;
        this.typeVol = typeVol;
    }
    
    // Getters et Setters
    public Long getIdVol() {
        return idVol;
    }
    
    public void setIdVol(Long idVol) {
        this.idVol = idVol;
    }
    
    public String getCodeVol() {
        return codeVol;
    }

    public void setCodeVol(String codeVol) {
        this.codeVol = codeVol;
    }
    
    public Avion getAvion() {
        return avion;
    }
    
    public void setAvion(Avion avion) {
        this.avion = avion;
    }
    
    public Aeroport getAeroportDepart() {
        return aeroportDepart;
    }
    
    public void setAeroportDepart(Aeroport aeroportDepart) {
        this.aeroportDepart = aeroportDepart;
    }
    
    public Aeroport getAeroportArrivee() {
        return aeroportArrivee;
    }
    
    public void setAeroportArrivee(Aeroport aeroportArrivee) {
        this.aeroportArrivee = aeroportArrivee;
    }
    
    public LocalDateTime getDateVol() {
        return dateVol;
    }
    
    public void setDateVol(LocalDateTime dateVol) {
        this.dateVol = dateVol;
    }
    
    public LocalDateTime getDateArrivee() {
        return dateArrivee;
    }
    
    public void setDateArrivee(LocalDateTime dateArrivee) {
        this.dateArrivee = dateArrivee;
    }
    
    public String getTypeVol() {
        return typeVol;
    }
    
    public void setTypeVol(String typeVol) {
        this.typeVol = typeVol;
    }
    
    public List<HistoriqueEtatVol> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatVol> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
    
    public List<Reservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
    
    public List<ChargementCargo> getChargementsCargo() {
        return chargementsCargo;
    }
    
    public void setChargementsCargo(List<ChargementCargo> chargementsCargo) {
        this.chargementsCargo = chargementsCargo;
    }
}

