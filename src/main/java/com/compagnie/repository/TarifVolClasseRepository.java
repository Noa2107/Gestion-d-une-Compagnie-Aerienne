package com.compagnie.repository;

import com.compagnie.model.TarifVolClasse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifVolClasseRepository extends JpaRepository<TarifVolClasse, Long> {
    Optional<TarifVolClasse> findByVolIdVolAndClasseIgnoreCase(Long idVol, String classe);

    Optional<TarifVolClasse> findByVolIdVolAndClasseIgnoreCaseAndTypePassagerIdTypePassager(Long idVol, String classe, Integer idTypePassager);
}
