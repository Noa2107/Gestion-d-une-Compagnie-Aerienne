package com.compagnie.repository;

import com.compagnie.model.Devise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviseRepository extends JpaRepository<Devise, Long> {
    Optional<Devise> findByCodeDevise(String codeDevise);
    boolean existsByCodeDevise(String codeDevise);
    Optional<Devise> findByEstReferenceTrue();
}

