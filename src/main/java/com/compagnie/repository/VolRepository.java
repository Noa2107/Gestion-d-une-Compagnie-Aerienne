package com.compagnie.repository;

import com.compagnie.model.Vol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VolRepository extends JpaRepository<Vol, Long> {

    List<Vol> findByAvionIdAvionOrderByDateVolAsc(Long idAvion);
    
    @Query("SELECT v FROM Vol v WHERE v.avion.idAvion = :idAvion " +
           "AND v.dateVol BETWEEN :debut AND :fin")
    List<Vol> findVolsByAvionAndDateRange(@Param("idAvion") Long idAvion,
                                          @Param("debut") LocalDateTime debut,
                                          @Param("fin") LocalDateTime fin);
    
    @Query("SELECT v FROM Vol v WHERE v.aeroportDepart.idAeroport = :idDepart " +
           "AND v.aeroportArrivee.idAeroport = :idArrivee " +
           "AND CAST(v.dateVol AS date) = CAST(:dateRecherche AS date) " +
           "AND (v.typeVol = 'PASSAGER' OR v.typeVol = 'MIXTE') " +
           "ORDER BY v.dateVol ASC")
    List<Vol> rechercherVolsDisponibles(@Param("idDepart") Long idDepart,
                                        @Param("idArrivee") Long idArrivee,
                                        @Param("dateRecherche") LocalDateTime dateRecherche);
    
    @Query("SELECT v FROM Vol v WHERE UPPER(v.aeroportDepart.code) = UPPER(:codeDepart) " +
           "AND UPPER(v.aeroportArrivee.code) = UPPER(:codeArrivee) " +
           "AND CAST(v.dateVol AS date) = CAST(:dateRecherche AS date) " +
           "AND (v.typeVol = 'PASSAGER' OR v.typeVol = 'MIXTE') " +
           "ORDER BY v.dateVol ASC")
    List<Vol> rechercherVolsParCodeAeroport(@Param("codeDepart") String codeDepart,
                                            @Param("codeArrivee") String codeArrivee,
                                            @Param("dateRecherche") LocalDateTime dateRecherche);

    @Query("SELECT v FROM Vol v WHERE UPPER(v.aeroportDepart.code) = UPPER(:codeDepart) " +
           "AND UPPER(v.aeroportArrivee.code) = UPPER(:codeArrivee) " +
           "AND v.dateVol BETWEEN :debut AND :fin " +
           "AND (v.typeVol = 'PASSAGER' OR v.typeVol = 'MIXTE') " +
           "ORDER BY v.dateVol ASC")
    List<Vol> rechercherVolsParCodeAeroportEtPlage(@Param("codeDepart") String codeDepart,
                                                   @Param("codeArrivee") String codeArrivee,
                                                   @Param("debut") LocalDateTime debut,
                                                   @Param("fin") LocalDateTime fin);

    List<Vol> findByDateVolBetweenOrderByDateVolAsc(LocalDateTime debut, LocalDateTime fin);
}
