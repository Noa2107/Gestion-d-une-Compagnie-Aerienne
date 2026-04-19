package com.compagnie.repository;

import com.compagnie.model.PublicitePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PublicitePaiementRepository extends JpaRepository<PublicitePaiement, Long> {

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PublicitePaiement p WHERE p.annee = :annee AND p.mois = :mois")
    BigDecimal totalPayeParPeriode(@Param("mois") int mois, @Param("annee") int annee);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM PublicitePaiement p WHERE p.societe.idSociete = :idSociete AND p.annee = :annee AND p.mois = :mois")
    BigDecimal totalPayeParSocieteEtPeriode(@Param("idSociete") Long idSociete, @Param("mois") int mois, @Param("annee") int annee);

    @Query("SELECT p.societe.idSociete, COALESCE(SUM(p.montant), 0) FROM PublicitePaiement p WHERE p.mois = :mois AND p.annee = :annee GROUP BY p.societe.idSociete")
    List<Object[]> totalPayeGroupeParSocietePourPeriode(@Param("mois") int mois, @Param("annee") int annee);
}
