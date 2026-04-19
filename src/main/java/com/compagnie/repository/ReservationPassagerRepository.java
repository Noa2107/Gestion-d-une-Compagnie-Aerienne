package com.compagnie.repository;

import com.compagnie.model.ReservationPassager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationPassagerRepository extends JpaRepository<ReservationPassager, Long> {

    List<ReservationPassager> findByReservationIdReservationOrderBySiegeAsc(Long idReservation);

    boolean existsByReservationIdReservationAndSiege(Long idReservation, String siege);

    Optional<ReservationPassager> findByReservationIdReservationAndSiege(Long idReservation, String siege);
}
