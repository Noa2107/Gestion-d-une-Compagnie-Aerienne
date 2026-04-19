package com.compagnie.service;

import com.compagnie.repository.PublicitePaiementRepository;
import com.compagnie.repository.PubliciteRepository;
import com.compagnie.service.dto.SocieteCa;
import com.compagnie.service.dto.SocieteReglement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PubliciteService {

    private final PubliciteRepository publiciteRepository;
    private final PublicitePaiementRepository publicitePaiementRepository;

    public PubliciteService(PubliciteRepository publiciteRepository,
                            PublicitePaiementRepository publicitePaiementRepository) {
        this.publiciteRepository = publiciteRepository;
        this.publicitePaiementRepository = publicitePaiementRepository;
    }

    public BigDecimal calculerCaTotal(int mois, int annee) {
        return publiciteRepository.caTotalParMoisAnnee(mois, annee);
    }

    public BigDecimal calculerCaParSociete(Long idSociete) {
        return publiciteRepository.caParSociete(idSociete);
    }

    public List<SocieteCa> calculerCaParSocietePourPeriode(int mois, int annee) {
        List<Object[]> rows = publiciteRepository.caParSocietePourPeriode(mois, annee);
        List<SocieteCa> list = new ArrayList<>();
        for (Object[] r : rows) {
            Long id = (Long) r[0];
            String nom = (String) r[1];
            BigDecimal ca = (BigDecimal) r[2];
            list.add(new SocieteCa(id, nom, ca));
        }
        return list;
    }

    public BigDecimal totalPayePeriode(int mois, int annee) {
        return publicitePaiementRepository.totalPayeParPeriode(mois, annee);
    }

    public List<SocieteReglement> reglementsParSocietePourPeriode(int mois, int annee) {
        // CA par société
        List<SocieteCa> cas = calculerCaParSocietePourPeriode(mois, annee);
        // Payé groupé par société
        List<Object[]> payes = publicitePaiementRepository.totalPayeGroupeParSocietePourPeriode(mois, annee);
        Map<Long, BigDecimal> mapPaye = new HashMap<>();
        for (Object[] r : payes) {
            Long id = (Long) r[0];
            BigDecimal montant = (BigDecimal) r[1];
            mapPaye.put(id, montant);
        }
        List<SocieteReglement> result = new ArrayList<>();
        for (SocieteCa sca : cas) {
            BigDecimal paye = mapPaye.getOrDefault(sca.getIdSociete(), BigDecimal.ZERO);
            result.add(new SocieteReglement(sca.getIdSociete(), sca.getNom(), sca.getCa(), paye));
        }
        return result;
    }
}
