package com.compagnie.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reservation_passager")
public class ReservationPassager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation_passager")
    private Long idReservationPassager;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservation", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_passager", nullable = false)
    private Passager passager;

    @Column(name = "siege", length = 10, nullable = false)
    private String siege;

    public ReservationPassager() {
    }

    public ReservationPassager(Reservation reservation, Passager passager, String siege) {
        this.reservation = reservation;
        this.passager = passager;
        this.siege = siege;
    }

    public Long getIdReservationPassager() {
        return idReservationPassager;
    }

    public void setIdReservationPassager(Long idReservationPassager) {
        this.idReservationPassager = idReservationPassager;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Passager getPassager() {
        return passager;
    }

    public void setPassager(Passager passager) {
        this.passager = passager;
    }

    public String getSiege() {
        return siege;
    }

    public void setSiege(String siege) {
        this.siege = siege;
    }
}
