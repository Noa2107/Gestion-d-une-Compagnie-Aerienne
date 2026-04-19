package com.compagnie.repository;

import com.compagnie.model.Publicite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PubliciteRepository extends JpaRepository<Publicite, Long> {

    @Query("SELECT COALESCE(SUM(p.nombreDiffusions * p.prixUnitaire), 0) FROM Publicite p WHERE p.annee = :annee AND p.mois = :mois")
    BigDecimal caTotalParMoisAnnee(@Param("mois") int mois, @Param("annee") int annee);

    @Query("SELECT COALESCE(SUM(p.nombreDiffusions * p.prixUnitaire), 0) FROM Publicite p WHERE p.societe.idSociete = :idSociete")
    BigDecimal caParSociete(@Param("idSociete") Long idSociete);

    @Query("SELECT p.societe.idSociete, p.societe.nom, COALESCE(SUM(p.nombreDiffusions * p.prixUnitaire), 0) " +
            "FROM Publicite p WHERE p.mois = :mois AND p.annee = :annee " +
            "GROUP BY p.societe.idSociete, p.societe.nom ORDER BY p.societe.nom")
    List<Object[]> caParSocietePourPeriode(@Param("mois") int mois, @Param("annee") int annee);

    @Query("SELECT p.societe.idSociete, COALESCE(SUM(p.nombreDiffusions * p.prixUnitaire), 0) " +
            "FROM Publicite p WHERE p.mois = :mois AND p.annee = :annee " +
            "GROUP BY p.societe.idSociete")
    List<Object[]> totalPubsGroupeParSocietePourPeriode(@Param("mois") int mois, @Param("annee") int annee);

    @Query("SELECT COALESCE(SUM(p.nombreDiffusions), 0) FROM Publicite p WHERE p.societe.idSociete = :idSociete AND p.mois = :mois AND p.annee = :annee")
    Integer quotaDiffusionsPourSocietePeriode(@Param("idSociete") Long idSociete,
                                              @Param("mois") int mois,
                                              @Param("annee") int annee);

    @Query("SELECT COALESCE(MAX(p.prixUnitaire), 0) FROM Publicite p WHERE p.societe.idSociete = :idSociete AND p.mois = :mois AND p.annee = :annee")
    java.math.BigDecimal prixUnitairePourSocietePeriode(@Param("idSociete") Long idSociete,
                                                        @Param("mois") int mois,
                                                        @Param("annee") int annee);
}
