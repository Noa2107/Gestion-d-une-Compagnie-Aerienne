package com.compagnie.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Long idReservation;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_passager", nullable = false)
    private Passager passager;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vol", nullable = false)
    private Vol vol;
    
    @Column(name = "date_reservation")
    private LocalDateTime dateReservation;
    
    @Column(name = "nombre_places", nullable = false)
    private Integer nombrePlaces;
    
    @Column(name = "numero_siege", length = 10)
    private String numeroSiege;
    
    @Column(name = "sieges_attribues", length = 255)
    private String siegesAttribues;
    
    @Column(name = "classe", length = 20)
    private String classe; // Economie, Affaires, Premiere
    
    @Column(name = "bagage_kg")
    private Integer bagageKg;
    
    @Column(name = "prix_total", precision = 12, scale = 2)
    private BigDecimal prixTotal;
    
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatReservation> historiqueEtats = new ArrayList<>();
    
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PaiementReservation> paiements = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationPassager> reservationPassagers = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReservationTypePassager> reservationTypePassagers = new ArrayList<>();

    @Transient
    private Integer nbAdultes;

    @Transient
    private Integer nbEnfants;

    @Transient
    private Integer nbBebes;
    
    public Reservation() {
        this.dateReservation = LocalDateTime.now();
        this.nombrePlaces = 1;
    }
    
    public Reservation(Passager passager, Vol vol, String classe, BigDecimal prixTotal) {
        this.passager = passager;
        this.vol = vol;
        this.classe = classe;
        this.prixTotal = prixTotal;
        this.dateReservation = LocalDateTime.now();
        this.nombrePlaces = 1;
    }
    
    // Getters et Setters
    public Long getIdReservation() {
        return idReservation;
    }
    
    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }
    
    public Passager getPassager() {
        return passager;
    }
    
    public void setPassager(Passager passager) {
        this.passager = passager;
    }
    
    public Vol getVol() {
        return vol;
    }
    
    public void setVol(Vol vol) {
        this.vol = vol;
    }
    
    public LocalDateTime getDateReservation() {
        return dateReservation;
    }
    
    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }
    
    public Integer getNombrePlaces() {
        return nombrePlaces;
    }
    
    public void setNombrePlaces(Integer nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }
    
    public String getNumeroSiege() {
        return numeroSiege;
    }
    
    public void setNumeroSiege(String numeroSiege) {
        this.numeroSiege = numeroSiege;
    }
    
    public String getSiegesAttribues() {
        return siegesAttribues;
    }
    
    public void setSiegesAttribues(String siegesAttribues) {
        this.siegesAttribues = siegesAttribues;
    }
    
    public String getClasse() {
        return classe;
    }
    
    public void setClasse(String classe) {
        this.classe = classe;
    }
    
    public Integer getBagageKg() {
        return bagageKg;
    }
    
    public void setBagageKg(Integer bagageKg) {
        this.bagageKg = bagageKg;
    }
    
    public BigDecimal getPrixTotal() {
        return prixTotal;
    }
    
    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }
    
    public List<HistoriqueEtatReservation> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatReservation> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
    
    public List<PaiementReservation> getPaiements() {
        return paiements;
    }
    
    public void setPaiements(List<PaiementReservation> paiements) {
        this.paiements = paiements;
    }

    public List<ReservationPassager> getReservationPassagers() {
        return reservationPassagers;
    }

    public void setReservationPassagers(List<ReservationPassager> reservationPassagers) {
        this.reservationPassagers = reservationPassagers;
    }

    public List<ReservationTypePassager> getReservationTypePassagers() {
        return reservationTypePassagers;
    }

    public void setReservationTypePassagers(List<ReservationTypePassager> reservationTypePassagers) {
        this.reservationTypePassagers = reservationTypePassagers;
    }

    public Integer getNbAdultes() {
        return nbAdultes;
    }

    public void setNbAdultes(Integer nbAdultes) {
        this.nbAdultes = nbAdultes;
    }

    public Integer getNbEnfants() {
        return nbEnfants;
    }

    public void setNbEnfants(Integer nbEnfants) {
        this.nbEnfants = nbEnfants;
    }

    public Integer getNbBebes() {
        return nbBebes;
    }

    public void setNbBebes(Integer nbBebes) {
        this.nbBebes = nbBebes;
    }
}

