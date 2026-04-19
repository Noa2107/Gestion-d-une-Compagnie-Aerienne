package com.compagnie.repository;

import com.compagnie.model.Avion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvionRepository extends JpaRepository<Avion, Long> {
    Optional<Avion> findByImmatriculation(String immatriculation);
    boolean existsByImmatriculation(String immatriculation);
}

