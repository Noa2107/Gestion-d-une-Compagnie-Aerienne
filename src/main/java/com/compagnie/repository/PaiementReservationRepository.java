package com.compagnie.repository;

import com.compagnie.model.PaiementReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaiementReservationRepository extends JpaRepository<PaiementReservation, Long> {
    List<PaiementReservation> findByReservationIdReservation(Long idReservation);

    List<PaiementReservation> findByReservationVolIdVol(Long idVol);

    boolean existsByReservationIdReservation(Long idReservation);

    Optional<PaiementReservation> findTopByOrderByIdPaiementReservationDesc();

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(p.montantPaye), 0) FROM PaiementReservation p WHERE p.reservation.vol.idVol = :idVol")
    java.math.BigDecimal sumMontantPayeByVolId(@org.springframework.data.repository.query.Param("idVol") Long idVol);
}

