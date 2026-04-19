package com.compagnie.repository;

import com.compagnie.model.EtatPaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtatPaiementRepository extends JpaRepository<EtatPaiement, Long> {
    Optional<EtatPaiement> findByCodeEtat(Integer codeEtat);
}

