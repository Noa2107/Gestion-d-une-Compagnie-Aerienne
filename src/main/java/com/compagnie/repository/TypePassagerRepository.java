package com.compagnie.repository;

import com.compagnie.model.TypePassager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypePassagerRepository extends JpaRepository<TypePassager, Integer> {
    Optional<TypePassager> findByNomTypeIgnoreCase(String nomType);
}
