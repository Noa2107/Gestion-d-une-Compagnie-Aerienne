package com.compagnie.repository;

import com.compagnie.model.HistoriqueEtatPaiementReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueEtatPaiementReservationRepository extends JpaRepository<HistoriqueEtatPaiementReservation, Long> {
    List<HistoriqueEtatPaiementReservation> findByPaiementReservationIdPaiementReservationOrderByDateChangementDesc(Long idPaiement);
    
    @Query("SELECT h FROM HistoriqueEtatPaiementReservation h WHERE h.paiementReservation.idPaiementReservation = :idPaiement ORDER BY h.dateChangement DESC")
    Optional<HistoriqueEtatPaiementReservation> findLatestByPaiementId(@Param("idPaiement") Long idPaiement);
}

