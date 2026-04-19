package com.compagnie.service;

import com.compagnie.model.Avion;
import com.compagnie.model.TarifVolClasse;
import com.compagnie.model.Vol;
import com.compagnie.repository.AvionRepository;
import com.compagnie.repository.TarifVolClasseRepository;
import com.compagnie.repository.VolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AvionRevenuMaxService {

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private VolRepository volRepository;

    @Autowired
    private TarifVolClasseRepository tarifVolClasseRepository;

    public List<Vol> getVolsByAvion(Long idAvion) {
        if (idAvion == null) {
            throw new IllegalArgumentException("Avion obligatoire");
        }
        return volRepository.findByAvionIdAvionOrderByDateVolAsc(idAvion);
    }

    public BigDecimal calculerRevenuMaxEconomie(Long idAvion, Long idVol) {
        return calculerRevenuMaxParClasse(idAvion, idVol, "Economie");
    }

    public BigDecimal calculerRevenuMaxPremium(Long idAvion, Long idVol) {
        return calculerRevenuMaxParClasse(idAvion, idVol, "Premium");
    }

    public BigDecimal calculerRevenuMaxPremiere(Long idAvion, Long idVol) {
        return calculerRevenuMaxParClasse(idAvion, idVol, "Premiere");
    }

    private BigDecimal calculerRevenuMaxParClasse(Long idAvion, Long idVol, String classe) {
        if (idAvion == null || idVol == null) {
            throw new IllegalArgumentException("Avion et vol obligatoires");
        }
        if (classe == null || classe.trim().isEmpty()) {
            throw new IllegalArgumentException("Classe obligatoire");
        }

        Avion avion = avionRepository.findById(idAvion)
                .orElseThrow(() -> new IllegalArgumentException("Avion introuvable"));

        Vol vol = volRepository.findById(idVol)
                .orElseThrow(() -> new IllegalArgumentException("Vol introuvable"));

        if (vol.getAvion() == null || vol.getAvion().getIdAvion() == null || !vol.getAvion().getIdAvion().equals(idAvion)) {
            throw new IllegalArgumentException("Le vol selectionne ne correspond pas a l'avion selectionne");
        }

        int nbPlaces;
        if ("Economie".equalsIgnoreCase(classe)) {
            nbPlaces = avion.getNbPlacesEconomique() == null ? 0 : avion.getNbPlacesEconomique();
        } else if ("Premium".equalsIgnoreCase(classe)) {
            nbPlaces = avion.getNbPlacesPremium() == null ? 0 : avion.getNbPlacesPremium();
        } else if ("Premiere".equalsIgnoreCase(classe)) {
            nbPlaces = avion.getNbPlacesPremiere() == null ? 0 : avion.getNbPlacesPremiere();
        } else {
            nbPlaces = 0;
        }

        Optional<TarifVolClasse> tarifOpt = tarifVolClasseRepository.findByVolIdVolAndClasseIgnoreCase(idVol, classe.trim());
        if (tarifOpt.isEmpty() || tarifOpt.get().getPrixUnitaire() == null) {
            return BigDecimal.ZERO;
        }

        return tarifOpt.get().getPrixUnitaire().multiply(BigDecimal.valueOf(nbPlaces));
    }

    public BigDecimal calculerRevenuMax(Long idAvion, Long idVol) {
        if (idAvion == null || idVol == null) {
            throw new IllegalArgumentException("Avion et vol obligatoires");
        }

        BigDecimal eco = calculerRevenuMaxEconomie(idAvion, idVol);
        BigDecimal premium = calculerRevenuMaxPremium(idAvion, idVol);
        BigDecimal prem = calculerRevenuMaxPremiere(idAvion, idVol);
        return eco.add(premium).add(prem);
    }
}
