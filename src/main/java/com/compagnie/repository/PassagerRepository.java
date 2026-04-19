package com.compagnie.repository;

import com.compagnie.model.Passager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassagerRepository extends JpaRepository<Passager, Long> {
    Optional<Passager> findByNumeroPasseport(String numeroPasseport);
    boolean existsByNumeroPasseport(String numeroPasseport);
}

