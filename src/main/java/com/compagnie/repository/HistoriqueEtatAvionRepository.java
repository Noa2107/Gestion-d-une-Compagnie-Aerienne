package com.compagnie.repository;

import com.compagnie.model.HistoriqueEtatAvion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueEtatAvionRepository extends JpaRepository<HistoriqueEtatAvion, Long> {
    List<HistoriqueEtatAvion> findByAvionIdAvionOrderByDateChangementDesc(Long idAvion);
    
    @Query("SELECT h FROM HistoriqueEtatAvion h WHERE h.avion.idAvion = :idAvion ORDER BY h.dateChangement DESC")
    Optional<HistoriqueEtatAvion> findLatestByAvionId(@Param("idAvion") Long idAvion);
}

