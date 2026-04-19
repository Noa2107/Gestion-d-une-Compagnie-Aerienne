package com.compagnie.repository;

import com.compagnie.model.HistoriqueEtatCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HistoriqueEtatCargoRepository extends JpaRepository<HistoriqueEtatCargo, Long> {
    List<HistoriqueEtatCargo> findByCargoIdCargoOrderByDateChangementDesc(Long idCargo);
    
    @Query("SELECT h FROM HistoriqueEtatCargo h WHERE h.cargo.idCargo = :idCargo ORDER BY h.dateChangement DESC")
    Optional<HistoriqueEtatCargo> findLatestByCargoId(@Param("idCargo") Long idCargo);
}

