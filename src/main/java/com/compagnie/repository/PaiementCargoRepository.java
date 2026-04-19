package com.compagnie.repository;

import com.compagnie.model.PaiementCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementCargoRepository extends JpaRepository<PaiementCargo, Long> {
    List<PaiementCargo> findByCargoIdCargo(Long idCargo);
}

