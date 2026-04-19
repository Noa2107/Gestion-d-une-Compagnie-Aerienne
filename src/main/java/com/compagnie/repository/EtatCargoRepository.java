package com.compagnie.repository;

import com.compagnie.model.EtatCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtatCargoRepository extends JpaRepository<EtatCargo, Long> {
    Optional<EtatCargo> findByCodeEtat(Integer codeEtat);
}

