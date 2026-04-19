package com.compagnie.repository;

import com.compagnie.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByVolIdVol(Long idVol);

    boolean existsByVolIdVol(Long idVol);
    
    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r WHERE r.vol.idVol = :idVol")
    Integer sumNombrePlacesByVolId(@Param("idVol") Long idVol);
    
    @Query("SELECT SUM(r.prixTotal) FROM Reservation r WHERE r.vol.idVol = :idVol " +
            "AND CAST(r.vol.dateVol AS date) = CAST(:dateHeureVol AS date)")
    BigDecimal sumChiffreAffaireByVolIdAndDateHeureVol(@Param("idVol") Long idVol,
                                                       @Param("dateHeureVol") LocalDateTime dateHeureVol);

    @Query("SELECT r FROM Reservation r WHERE r.vol.idVol = :idVol AND r.numeroSiege = :numeroSiege")
    Optional<Reservation> findByVolAndSiege(@Param("idVol") Long idVol, @Param("numeroSiege") String numeroSiege);
    
    boolean existsByVolIdVolAndNumeroSiege(Long idVol, String numeroSiege);

    @Query("SELECT COALESCE(SUM(r.prixTotal), 0) FROM Reservation r WHERE r.vol.idVol = :idVol")
    java.math.BigDecimal sumPrixTotalByVolId(@Param("idVol") Long idVol);
}
