package com.compagnie.service;

import com.compagnie.model.PromotionTarif;
import com.compagnie.model.TarifVolClasse;
import com.compagnie.model.TypePassager;
import com.compagnie.repository.PromotionTarifRepository;
import com.compagnie.repository.TarifVolClasseRepository;
import com.compagnie.repository.TypePassagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TarificationService {

    @Autowired
    private TarifVolClasseRepository tarifVolClasseRepository;

    @Autowired
    private PromotionTarifRepository promotionTarifRepository;

    @Autowired
    private TypePassagerRepository typePassagerRepository;

    public BigDecimal calculerPrixTotal(Long idVol, String classe, Integer nombrePlaces, LocalDateTime now) {
        if (nombrePlaces == null) {
            nombrePlaces = 1;
        }
        return calculerPrixTotalMix(idVol, classe, nombrePlaces, 0, now);
    }

    public BigDecimal calculerPrixTotalMix(Long idVol, String classe, Integer nbAdultes, Integer nbEnfants, LocalDateTime now) {
        return calculerPrixTotalMix(idVol, classe, nbAdultes, nbEnfants, 0, now);
    }

    public BigDecimal calculerPrixTotalMix(Long idVol, String classe, Integer nbAdultes, Integer nbEnfants, Integer nbBebes, LocalDateTime now) {
        if (idVol == null || classe == null || classe.trim().isEmpty()) {
            throw new IllegalArgumentException("Le vol et la classe sont obligatoires");
        }

        int a = nbAdultes == null ? 0 : nbAdultes;
        int e = nbEnfants == null ? 0 : nbEnfants;
        int b = nbBebes == null ? 0 : nbBebes;
        if (a < 0 || e < 0 || b < 0) {
            throw new IllegalArgumentException("Le nombre de passagers doit etre positif");
        }
        if (a + e + b <= 0) {
            throw new IllegalArgumentException("Le nombre total de places doit etre strictement positif");
        }

        LocalDateTime date = now == null ? LocalDateTime.now() : now;
        String classeNorm = classe.trim();

        BigDecimal total = BigDecimal.ZERO;
        if (a > 0) {
            total = total.add(calculerSousTotalParType(idVol, classeNorm, "Adulte", a, date));
        }
        if (e > 0) {
            total = total.add(calculerSousTotalParType(idVol, classeNorm, "Enfant", e, date));
        }
        if (b > 0) {
            total = total.add(calculerSousTotalParType(idVol, classeNorm, "Bebe", b, date));
        }

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculerSousTotalParType(Long idVol, String classe, String nomTypePassager, int quantite, LocalDateTime date) {
        TypePassager type = typePassagerRepository.findByNomTypeIgnoreCase(nomTypePassager)
                .orElseThrow(() -> new IllegalArgumentException("Type passager introuvable: " + nomTypePassager));

        return calculerSousTotalParType(idVol, classe, type.getIdTypePassager(), quantite, date);
    }

    public BigDecimal calculerSousTotalParType(Long idVol, String classe, Integer idTypePassager, int quantite, LocalDateTime date) {
        if (idTypePassager == null) {
            throw new IllegalArgumentException("Type passager obligatoire");
        }

        Optional<TarifVolClasse> tarifOpt = tarifVolClasseRepository
                .findByVolIdVolAndClasseIgnoreCaseAndTypePassagerIdTypePassager(idVol, classe, idTypePassager);
        if (tarifOpt.isEmpty() || tarifOpt.get().getPrixUnitaire() == null) {
            throw new IllegalArgumentException("Tarif introuvable pour ce vol, cette classe et ce type passager");
        }

        BigDecimal sousTotal = tarifOpt.get().getPrixUnitaire().multiply(BigDecimal.valueOf(quantite));

        List<PromotionTarif> promos = promotionTarifRepository.findPromotionsActivesByTypePassager(idVol, classe, idTypePassager, date);
        if (!promos.isEmpty()) {
            PromotionTarif promo = promos.get(0);
            if (promo.getTypeReduction() != null && promo.getValeur() != null) {
                if ("POURCENTAGE".equalsIgnoreCase(promo.getTypeReduction())) {
                    BigDecimal pourcentage = promo.getValeur();
                    BigDecimal reduction = sousTotal.multiply(pourcentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                    sousTotal = sousTotal.subtract(reduction);
                } else if ("MONTANT".equalsIgnoreCase(promo.getTypeReduction())) {
                    sousTotal = sousTotal.subtract(promo.getValeur());
                }
            }
        }

        if (sousTotal.compareTo(BigDecimal.ZERO) < 0) {
            sousTotal = BigDecimal.ZERO;
        }
        return sousTotal;
    }
}
