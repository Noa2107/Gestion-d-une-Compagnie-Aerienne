package com.compagnie.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReservationTypePassagerId implements Serializable {

    @Column(name = "id_reservation")
    private Long idReservation;

    @Column(name = "id_type_passager")
    private Integer idTypePassager;

    public ReservationTypePassagerId() {
    }

    public ReservationTypePassagerId(Long idReservation, Integer idTypePassager) {
        this.idReservation = idReservation;
        this.idTypePassager = idTypePassager;
    }

    public Long getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Long idReservation) {
        this.idReservation = idReservation;
    }

    public Integer getIdTypePassager() {
        return idTypePassager;
    }

    public void setIdTypePassager(Integer idTypePassager) {
        this.idTypePassager = idTypePassager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationTypePassagerId that = (ReservationTypePassagerId) o;
        return Objects.equals(idReservation, that.idReservation) && Objects.equals(idTypePassager, that.idTypePassager);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReservation, idTypePassager);
    }
}
