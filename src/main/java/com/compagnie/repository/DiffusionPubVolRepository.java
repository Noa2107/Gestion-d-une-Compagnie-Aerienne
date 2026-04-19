package com.compagnie.repository;

import com.compagnie.model.DiffusionPubVol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface DiffusionPubVolRepository extends JpaRepository<DiffusionPubVol, Long> {

    @Query("SELECT COALESCE(SUM(d.nbDiffusions), 0) FROM DiffusionPubVol d WHERE d.societe.idSociete = :idSociete AND d.mois = :mois AND d.annee = :annee")
    Integer totalDiffusionsConsommees(@Param("idSociete") Long idSociete,
                                      @Param("mois") int mois,
                                      @Param("annee") int annee);

    @Query("SELECT COALESCE(SUM(d.nbDiffusions * d.prixUnitaire), 0) FROM DiffusionPubVol d WHERE d.vol.idVol = :idVol")
    BigDecimal caPublicitesParVol(@Param("idVol") Long idVol);

    @Query("SELECT COALESCE(SUM(d.nbDiffusions * d.prixUnitaire), 0) FROM DiffusionPubVol d " +
           "WHERE d.vol.idVol = :idVol AND d.mois = :mois AND d.annee = :annee")
    BigDecimal caPublicitesParVolPourPeriode(@Param("idVol") Long idVol,
                                            @Param("mois") int mois,
                                            @Param("annee") int annee);

    @Query("SELECT COALESCE(SUM(d.nbDiffusions * d.prixUnitaire), 0) FROM DiffusionPubVol d " +
           "WHERE d.vol.idVol = :idVol AND d.societe.idSociete = :idSociete AND d.mois = :mois AND d.annee = :annee")
    BigDecimal caPublicitesParVolPourPeriodeEtSociete(@Param("idVol") Long idVol,
                                                     @Param("idSociete") Long idSociete,
                                                     @Param("mois") int mois,
                                                     @Param("annee") int annee);

    @Query("SELECT COALESCE(SUM(d.nbDiffusions * d.prixUnitaire), 0) FROM DiffusionPubVol d " +
           "WHERE d.mois = :mois AND d.annee = :annee")
    BigDecimal caPublicitesParPeriode(@Param("mois") int mois,
                                     @Param("annee") int annee);

    @Query("SELECT COALESCE(SUM(d.nbDiffusions * d.prixUnitaire), 0) FROM DiffusionPubVol d " +
           "WHERE d.societe.idSociete = :idSociete AND d.mois = :mois AND d.annee = :annee")
    BigDecimal montantTotalParSocieteEtPeriode(@Param("idSociete") Long idSociete,
                                               @Param("mois") int mois,
                                               @Param("annee") int annee);

    @Query("SELECT d.vol.idVol, d.vol.aeroportDepart.code, d.vol.aeroportArrivee.code, d.vol.avion.immatriculation, d.vol.dateVol, " +
           "COALESCE(SUM(d.nbDiffusions * d.prixUnitaire), 0) " +
           "FROM DiffusionPubVol d WHERE d.societe.idSociete = :idSociete AND d.mois = :mois AND d.annee = :annee " +
           "GROUP BY d.vol.idVol, d.vol.aeroportDepart.code, d.vol.aeroportArrivee.code, d.vol.avion.immatriculation, d.vol.dateVol " +
           "ORDER BY d.vol.dateVol")
    java.util.List<Object[]> montantParVolPourSocieteEtPeriode(@Param("idSociete") Long idSociete,
                                                               @Param("mois") int mois,
                                                               @Param("annee") int annee);

    @Query("SELECT d.societe.idSociete, COALESCE(SUM(d.nbDiffusions * d.prixUnitaire), 0) " +
           "FROM DiffusionPubVol d WHERE d.vol.idVol = :idVol AND d.mois = :mois AND d.annee = :annee " +
           "GROUP BY d.societe.idSociete")
    List<Object[]> caPublicitesParVolEtSocietePourPeriode(@Param("idVol") Long idVol,
                                                         @Param("mois") int mois,
                                                         @Param("annee") int annee);

    @Query("SELECT d.annee, d.mois, d.vol.idVol, d.vol.aeroportDepart.code, d.vol.aeroportArrivee.code, d.vol.avion.immatriculation, d.vol.dateVol, " +
           "d.societe.idSociete, d.societe.nom, COALESCE(SUM(d.nbDiffusions), 0), COALESCE(AVG(d.prixUnitaire), 0), COALESCE(SUM(d.nbDiffusions * d.prixUnitaire), 0) " +
           "FROM DiffusionPubVol d " +
           "GROUP BY d.annee, d.mois, d.vol.idVol, d.vol.aeroportDepart.code, d.vol.aeroportArrivee.code, d.vol.avion.immatriculation, d.vol.dateVol, d.societe.idSociete, d.societe.nom " +
           "ORDER BY d.annee DESC, d.mois DESC, d.vol.dateVol DESC, d.societe.nom")
    List<Object[]> listeDiffusionsGroupeesParVolEtSociete();
}
