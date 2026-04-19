package com.compagnie.repository;

import com.compagnie.model.ChargementCargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargementCargoRepository extends JpaRepository<ChargementCargo, Long> {
    List<ChargementCargo> findByVolIdVol(Long idVol);
    Optional<ChargementCargo> findByCargoIdCargo(Long idCargo);
    boolean existsByCargoIdCargo(Long idCargo);
}

