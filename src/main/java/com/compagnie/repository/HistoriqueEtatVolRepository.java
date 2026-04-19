package com.compagnie.repository;

import com.compagnie.model.HistoriqueEtatVol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueEtatVolRepository extends JpaRepository<HistoriqueEtatVol, Long> {
    List<HistoriqueEtatVol> findByVolIdVolOrderByDateChangementDesc(Long idVol);
    
    @Query("SELECT h FROM HistoriqueEtatVol h WHERE h.vol.idVol = :idVol ORDER BY h.dateChangement DESC")
    Optional<HistoriqueEtatVol> findLatestByVolId(@Param("idVol") Long idVol);
}

