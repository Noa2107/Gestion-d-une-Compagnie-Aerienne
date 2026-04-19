package com.compagnie.repository;

import com.compagnie.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {
    Optional<Cargo> findByReference(String reference);
    boolean existsByReference(String reference);
}

