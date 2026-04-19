package com.compagnie.service;

import com.compagnie.model.PromotionTarif;
import com.compagnie.model.TypePassager;
import com.compagnie.model.Vol;
import com.compagnie.repository.PromotionTarifRepository;
import com.compagnie.repository.TypePassagerRepository;
import com.compagnie.repository.VolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PromotionTarifService {

    @Autowired
    private PromotionTarifRepository promotionTarifRepository;

    @Autowired
    private VolRepository volRepository;

    @Autowired
    private TypePassagerRepository typePassagerRepository;

    public List<PromotionTarif> getAllPromotions() {
        return promotionTarifRepository.findAll();
    }

    public Optional<PromotionTarif> getPromotionById(Long id) {
        return promotionTarifRepository.findById(id);
    }

    public PromotionTarif addPromotion(PromotionTarif promo) {
        normalizeAndValidate(promo);

        Optional<Vol> volOpt = volRepository.findById(promo.getVol().getIdVol());
        if (volOpt.isEmpty()) {
            throw new IllegalArgumentException("Vol introuvable");
        }

        Optional<TypePassager> typeOpt = typePassagerRepository.findById(promo.getTypePassager().getIdTypePassager());
        if (typeOpt.isEmpty()) {
            throw new IllegalArgumentException("Type passager introuvable");
        }

        promo.setVol(volOpt.get());
        promo.setTypePassager(typeOpt.get());
        return promotionTarifRepository.save(promo);
    }

    public PromotionTarif updatePromotion(Long id, PromotionTarif promo) {
        Optional<PromotionTarif> existing = promotionTarifRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Promotion introuvable");
        }

        normalizeAndValidate(promo);

        Optional<Vol> volOpt = volRepository.findById(promo.getVol().getIdVol());
        if (volOpt.isEmpty()) {
            throw new IllegalArgumentException("Vol introuvable");
        }

        Optional<TypePassager> typeOpt = typePassagerRepository.findById(promo.getTypePassager().getIdTypePassager());
        if (typeOpt.isEmpty()) {
            throw new IllegalArgumentException("Type passager introuvable");
        }

        PromotionTarif toUpdate = existing.get();
        toUpdate.setVol(volOpt.get());
        toUpdate.setClasse(promo.getClasse());
        toUpdate.setTypePassager(typeOpt.get());
        toUpdate.setTypeReduction(promo.getTypeReduction());
        toUpdate.setValeur(promo.getValeur());
        toUpdate.setDateDebut(promo.getDateDebut());
        toUpdate.setDateFin(promo.getDateFin());
        toUpdate.setEstActif(promo.getEstActif());

        return promotionTarifRepository.save(toUpdate);
    }

    public void deletePromotion(Long id) {
        if (!promotionTarifRepository.existsById(id)) {
            throw new IllegalArgumentException("Promotion introuvable");
        }
        promotionTarifRepository.deleteById(id);
    }

    private void normalizeAndValidate(PromotionTarif promo) {
        if (promo.getVol() == null || promo.getVol().getIdVol() == null) {
            throw new IllegalArgumentException("Un vol doit etre selectionne");
        }
        if (promo.getClasse() == null || promo.getClasse().trim().isEmpty()) {
            throw new IllegalArgumentException("La classe est obligatoire");
        }
        if (promo.getTypePassager() == null || promo.getTypePassager().getIdTypePassager() == null) {
            throw new IllegalArgumentException("Le type passager est obligatoire");
        }
        if (promo.getTypeReduction() == null || promo.getTypeReduction().trim().isEmpty()) {
            throw new IllegalArgumentException("Le type de reduction est obligatoire");
        }
        if (!"POURCENTAGE".equalsIgnoreCase(promo.getTypeReduction()) && !"MONTANT".equalsIgnoreCase(promo.getTypeReduction())) {
            throw new IllegalArgumentException("Type de reduction invalide");
        }
        if (promo.getValeur() == null) {
            throw new IllegalArgumentException("La valeur de la reduction est obligatoire");
        }
        if (promo.getValeur().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La valeur de la reduction doit etre positive");
        }
        if (promo.getDateDebut() != null && promo.getDateFin() != null && promo.getDateFin().isBefore(promo.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin doit etre apres la date de debut");
        }

        promo.setClasse(promo.getClasse().trim());
        promo.setTypeReduction(promo.getTypeReduction().trim().toUpperCase());
        if (promo.getEstActif() == null) {
            promo.setEstActif(true);
        }
    }
}
