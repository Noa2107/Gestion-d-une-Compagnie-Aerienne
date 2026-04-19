package com.compagnie.service;

import com.compagnie.model.DiffusionPubVol;
import com.compagnie.model.Societe;
import com.compagnie.model.Vol;
import com.compagnie.repository.DiffusionPubVolRepository;
import com.compagnie.repository.ProduitExtraRepository;
import com.compagnie.repository.PublicitePaiementRepository;
import com.compagnie.repository.PubliciteRepository;
import com.compagnie.repository.PaiementReservationRepository;
import com.compagnie.repository.ReservationRepository;
import com.compagnie.repository.VolRepository;
import com.compagnie.service.dto.VolCa;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VolCaService {

    private final VolRepository volRepository;
    private final ReservationRepository reservationRepository;
    private final DiffusionPubVolRepository diffusionPubVolRepository;
    private final PubliciteRepository publiciteRepository;
    private final PaiementReservationRepository paiementReservationRepository;
    private final PublicitePaiementRepository publicitePaiementRepository;
    private final ProduitExtraRepository produitExtraRepository;

    public VolCaService(VolRepository volRepository,
                        ReservationRepository reservationRepository,
                        DiffusionPubVolRepository diffusionPubVolRepository,
                        PubliciteRepository publiciteRepository,
                        PaiementReservationRepository paiementReservationRepository,
                        PublicitePaiementRepository publicitePaiementRepository,
                        ProduitExtraRepository produitExtraRepository) {
        this.volRepository = volRepository;
        this.reservationRepository = reservationRepository;
        this.diffusionPubVolRepository = diffusionPubVolRepository;
        this.publiciteRepository = publiciteRepository;
        this.paiementReservationRepository = paiementReservationRepository;
        this.publicitePaiementRepository = publicitePaiementRepository;
        this.produitExtraRepository = produitExtraRepository;
    }

    public List<VolCa> listerVolsAvecCaPourPeriode(int mois, int annee, Long avionId, Long societeId) {
        LocalDate start = LocalDate.of(annee, mois, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal caProduitsExtraTheorique = produitExtraRepository.caTheoriqueParPeriode(mois, annee);
        if (caProduitsExtraTheorique == null) caProduitsExtraTheorique = BigDecimal.ZERO;

        List<Vol> vols;
        if (avionId != null) {
            vols = volRepository.findVolsByAvionAndDateRange(avionId, start.atStartOfDay(), end.atTime(LocalTime.MAX));
        } else {
            vols = volRepository.findByDateVolBetweenOrderByDateVolAsc(
                    start.atStartOfDay(), end.atTime(LocalTime.MAX)
            );
        }

        Map<Long, BigDecimal> totalPayeParSociete = new HashMap<>();
        for (Object[] row : publicitePaiementRepository.totalPayeGroupeParSocietePourPeriode(mois, annee)) {
            if (row == null || row.length < 2) continue;
            Long idSociete = row[0] == null ? null : ((Number) row[0]).longValue();
            BigDecimal total = (BigDecimal) row[1];
            if (idSociete != null) totalPayeParSociete.put(idSociete, total == null ? BigDecimal.ZERO : total);
        }

        Map<Long, Map<Long, BigDecimal>> caPubsParVolParSociete = new HashMap<>();
        Map<Long, BigDecimal> totalPubsParSocieteSurVols = new HashMap<>();
        Map<Long, BigDecimal> caPubsParVol = new HashMap<>();

        for (Vol v : vols) {
            Map<Long, BigDecimal> caParSociete = new HashMap<>();
            BigDecimal totalVol = BigDecimal.ZERO;
            for (Object[] row : diffusionPubVolRepository.caPublicitesParVolEtSocietePourPeriode(v.getIdVol(), mois, annee)) {
                if (row == null || row.length < 2) continue;
                Long idSociete = row[0] == null ? null : ((Number) row[0]).longValue();
                BigDecimal caSocieteSurVol = (BigDecimal) row[1];
                if (idSociete == null) continue;
                if (caSocieteSurVol == null) caSocieteSurVol = BigDecimal.ZERO;

                caParSociete.put(idSociete, caSocieteSurVol);
                totalVol = totalVol.add(caSocieteSurVol);
                totalPubsParSocieteSurVols.merge(idSociete, caSocieteSurVol, BigDecimal::add);
            }
            caPubsParVolParSociete.put(v.getIdVol(), caParSociete);
            caPubsParVol.put(v.getIdVol(), totalVol);
        }

        List<VolCa> res = new ArrayList<>();
        for (Vol v : vols) {
            BigDecimal caBillets = paiementReservationRepository.sumMontantPayeByVolId(v.getIdVol());
            if (caBillets == null) caBillets = BigDecimal.ZERO;
            BigDecimal caPubs = caPubsParVol.getOrDefault(v.getIdVol(), BigDecimal.ZERO);

            BigDecimal payePubsVol = BigDecimal.ZERO;
            BigDecimal restePubsVol = BigDecimal.ZERO;

            Map<Long, BigDecimal> caParSociete = caPubsParVolParSociete.getOrDefault(v.getIdVol(), java.util.Collections.emptyMap());
            for (Map.Entry<Long, BigDecimal> e : caParSociete.entrySet()) {
                Long idSociete = e.getKey();
                BigDecimal caSocieteSurVol = e.getValue() == null ? BigDecimal.ZERO : e.getValue();
                BigDecimal totalSocieteSurVols = totalPubsParSocieteSurVols.getOrDefault(idSociete, BigDecimal.ZERO);
                BigDecimal totalPayeSociete = totalPayeParSociete.getOrDefault(idSociete, BigDecimal.ZERO);

                BigDecimal ratio = BigDecimal.ZERO;
                if (totalSocieteSurVols.compareTo(BigDecimal.ZERO) > 0) {
                    ratio = caSocieteSurVol.divide(totalSocieteSurVols, 10, RoundingMode.HALF_UP);
                }

                BigDecimal partPaye = totalPayeSociete.multiply(ratio);
                BigDecimal partReste = caSocieteSurVol.subtract(partPaye);
                if (partReste.compareTo(BigDecimal.ZERO) < 0) partReste = BigDecimal.ZERO;

                payePubsVol = payePubsVol.add(partPaye);
                restePubsVol = restePubsVol.add(partReste);
            }
            if (restePubsVol.compareTo(BigDecimal.ZERO) < 0) {
                restePubsVol = BigDecimal.ZERO;
            }

            res.add(new VolCa(
                    v.getIdVol(),
                    v.getAeroportDepart() != null ? v.getAeroportDepart().getCode() : "",
                    v.getAeroportArrivee() != null ? v.getAeroportArrivee().getCode() : "",
                    v.getAvion() != null ? v.getAvion().getImmatriculation() : "",
                    v.getDateVol(),
                    caBillets, caPubs,
                    caProduitsExtraTheorique,
                    payePubsVol, restePubsVol
            ));
        }
        return res;
    }

    @Transactional
    public int allouerDiffusionsSousQuota(Long idVol, Long idSociete, int nbDemandes) {
        if (nbDemandes <= 0) return 0;
        Vol vol = volRepository.findById(idVol).orElseThrow();
        int mois = vol.getDateVol().getMonthValue();
        int annee = vol.getDateVol().getYear();
        Integer quota = publiciteRepository.quotaDiffusionsPourSocietePeriode(idSociete, mois, annee);
        if (quota == null) quota = 0;
        Integer consomme = diffusionPubVolRepository.totalDiffusionsConsommees(idSociete, mois, annee);
        if (consomme == null) consomme = 0;
        int restant = Math.max(0, quota - consomme);
        int autorise = Math.min(nbDemandes, restant);
        if (autorise <= 0) return 0;
        BigDecimal prixU = publiciteRepository.prixUnitairePourSocietePeriode(idSociete, mois, annee);
        if (prixU == null) prixU = BigDecimal.ZERO;
        DiffusionPubVol d = new DiffusionPubVol();
        d.setVol(vol);
        Societe s = new Societe();
        s.setIdSociete(idSociete);
        d.setSociete(s);
        d.setMois(mois);
        d.setAnnee(annee);
        d.setNbDiffusions(autorise);
        d.setPrixUnitaire(prixU);
        diffusionPubVolRepository.save(d);
        return autorise;
    }
}
