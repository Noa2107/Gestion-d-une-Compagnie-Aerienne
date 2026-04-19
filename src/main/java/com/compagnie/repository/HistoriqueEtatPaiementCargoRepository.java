package com.compagnie.repository;

import com.compagnie.model.HistoriqueEtatPaiementCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueEtatPaiementCargoRepository extends JpaRepository<HistoriqueEtatPaiementCargo, Long> {
    List<HistoriqueEtatPaiementCargo> findByPaiementCargoIdPaiementCargoOrderByDateChangementDesc(Long idPaiement);
    
    @Query("SELECT h FROM HistoriqueEtatPaiementCargo h WHERE h.paiementCargo.idPaiementCargo = :idPaiement ORDER BY h.dateChangement DESC")
    Optional<HistoriqueEtatPaiementCargo> findLatestByPaiementId(@Param("idPaiement") Long idPaiement);
}

