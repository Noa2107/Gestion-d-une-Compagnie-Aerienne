package com.compagnie.repository;

import com.compagnie.model.TypeProduit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeProduitRepository extends JpaRepository<TypeProduit, Long> {
    Optional<TypeProduit> findByNomTypeProduitIgnoreCase(String nomTypeProduit);
}
