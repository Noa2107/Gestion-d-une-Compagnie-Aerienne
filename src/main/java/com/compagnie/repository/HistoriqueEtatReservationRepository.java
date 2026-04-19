package com.compagnie.repository;

import com.compagnie.model.HistoriqueEtatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueEtatReservationRepository extends JpaRepository<HistoriqueEtatReservation, Long> {
    List<HistoriqueEtatReservation> findByReservationIdReservationOrderByDateChangementDesc(Long idReservation);
    
    @Query("SELECT h FROM HistoriqueEtatReservation h WHERE h.reservation.idReservation = :idReservation ORDER BY h.dateChangement DESC")
    Optional<HistoriqueEtatReservation> findLatestByReservationId(@Param("idReservation") Long idReservation);
}

