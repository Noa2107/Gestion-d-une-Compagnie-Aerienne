package com.compagnie.repository;

import com.compagnie.model.TauxChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TauxChangeRepository extends JpaRepository<TauxChange, Long> {
    
    @Query("SELECT t FROM TauxChange t WHERE t.estActif = true")
    List<TauxChange> findAllActifs();
    
    @Query("SELECT t FROM TauxChange t WHERE t.deviseSource.idDevise = :idDevise AND t.estActif = true " +
           "ORDER BY t.dateDebutValidite DESC")
    Optional<TauxChange> findDernierTauxActifByDevise(@Param("idDevise") Long idDevise);
    
    @Query("SELECT t FROM TauxChange t WHERE t.deviseSource.idDevise = :idDevise " +
           "ORDER BY t.dateDebutValidite DESC")
    Optional<TauxChange> findDernierTauxByDevise(@Param("idDevise") Long idDevise);
}

