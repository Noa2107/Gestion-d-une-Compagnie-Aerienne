package com.compagnie.repository;

import com.compagnie.model.ProduitExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface ProduitExtraRepository extends JpaRepository<ProduitExtra, Long> {

    @Query("SELECT COALESCE(SUM(p.prixUnitaire * p.quantiteMensuelle), 0) FROM ProduitExtra p WHERE p.mois = :mois AND p.annee = :annee")
    BigDecimal caTheoriqueParPeriode(@Param("mois") int mois, @Param("annee") int annee);
}
