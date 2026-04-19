package com.compagnie.repository;

import com.compagnie.model.Aeroport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AeroportRepository extends JpaRepository<Aeroport, Long> {
    Optional<Aeroport> findByCode(String code);
    boolean existsByCode(String code);
}

