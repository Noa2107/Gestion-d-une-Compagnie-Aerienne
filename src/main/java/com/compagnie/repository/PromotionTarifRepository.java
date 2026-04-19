package com.compagnie.repository;

import com.compagnie.model.PromotionTarif;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PromotionTarifRepository extends JpaRepository<PromotionTarif, Long> {

    @Query("SELECT p FROM PromotionTarif p WHERE p.vol.idVol = :idVol " +
            "AND UPPER(p.classe) = UPPER(:classe) " +
            "AND p.estActif = true " +
            "AND (p.dateDebut IS NULL OR p.dateDebut <= :now) " +
            "AND (p.dateFin IS NULL OR p.dateFin >= :now) " +
            "ORDER BY p.idPromotionTarif DESC")
    List<PromotionTarif> findPromotionsActives(@Param("idVol") Long idVol,
                                              @Param("classe") String classe,
                                              @Param("now") LocalDateTime now);

    @Query("SELECT p FROM PromotionTarif p WHERE p.vol.idVol = :idVol " +
            "AND UPPER(p.classe) = UPPER(:classe) " +
            "AND p.typePassager.idTypePassager = :idTypePassager " +
            "AND p.estActif = true " +
            "AND (p.dateDebut IS NULL OR p.dateDebut <= :now) " +
            "AND (p.dateFin IS NULL OR p.dateFin >= :now) " +
            "ORDER BY p.idPromotionTarif DESC")
    List<PromotionTarif> findPromotionsActivesByTypePassager(@Param("idVol") Long idVol,
                                                            @Param("classe") String classe,
                                                            @Param("idTypePassager") Integer idTypePassager,
                                                            @Param("now") LocalDateTime now);
}
