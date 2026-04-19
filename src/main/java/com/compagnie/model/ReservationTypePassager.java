package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "reservation_type_passager")
public class ReservationTypePassager {

    @EmbeddedId
    private ReservationTypePassagerId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idReservation")
    @JoinColumn(name = "id_reservation", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idTypePassager")
    @JoinColumn(name = "id_type_passager", nullable = false)
    private TypePassager typePassager;

    @NotNull
    @Column(name = "nombre", nullable = false)
    private Integer nombre;

    public ReservationTypePassager() {
    }

    public ReservationTypePassager(Reservation reservation, TypePassager typePassager, Integer nombre) {
        this.reservation = reservation;
        this.typePassager = typePassager;
        this.nombre = nombre;
        this.id = new ReservationTypePassagerId(
                reservation != null ? reservation.getIdReservation() : null,
                typePassager != null ? typePassager.getIdTypePassager() : null
        );
    }

    public ReservationTypePassagerId getId() {
        return id;
    }

    public void setId(ReservationTypePassagerId id) {
        this.id = id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public TypePassager getTypePassager() {
        return typePassager;
    }

    public void setTypePassager(TypePassager typePassager) {
        this.typePassager = typePassager;
    }

    public Integer getNombre() {
        return nombre;
    }

    public void setNombre(Integer nombre) {
        this.nombre = nombre;
    }
}
