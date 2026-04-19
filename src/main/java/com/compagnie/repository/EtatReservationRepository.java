package com.compagnie.repository;

import com.compagnie.model.EtatReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtatReservationRepository extends JpaRepository<EtatReservation, Long> {
    Optional<EtatReservation> findByCodeEtat(Integer codeEtat);
}

