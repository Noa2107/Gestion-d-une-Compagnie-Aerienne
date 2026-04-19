package com.compagnie.repository;

import com.compagnie.model.EtatAvion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtatAvionRepository extends JpaRepository<EtatAvion, Long> {
    Optional<EtatAvion> findByCodeEtat(Integer codeEtat);
}

