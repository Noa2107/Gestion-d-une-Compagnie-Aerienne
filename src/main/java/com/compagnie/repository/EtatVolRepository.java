package com.compagnie.repository;

import com.compagnie.model.EtatVol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtatVolRepository extends JpaRepository<EtatVol, Long> {
    Optional<EtatVol> findByCodeEtat(Integer codeEtat);
}

