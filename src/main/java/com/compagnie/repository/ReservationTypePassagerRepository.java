package com.compagnie.repository;

import com.compagnie.model.ReservationTypePassager;
import com.compagnie.model.ReservationTypePassagerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationTypePassagerRepository extends JpaRepository<ReservationTypePassager, ReservationTypePassagerId> {
    List<ReservationTypePassager> findByReservationIdReservation(Long idReservation);

    void deleteByReservationIdReservation(Long idReservation);
}
