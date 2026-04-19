package com.compagnie.service;

import com.compagnie.model.Societe;
import com.compagnie.repository.DiffusionPubVolRepository;
import com.compagnie.repository.PublicitePaiementRepository;
import com.compagnie.repository.SocieteRepository;
import com.compagnie.service.dto.SocietePaiementVolLigne;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class SocietePaiementService {

    private final DiffusionPubVolRepository diffusionRepo;
    private final PublicitePaiementRepository paiementRepo;
    private final SocieteRepository societeRepository;

    public SocietePaiementService(DiffusionPubVolRepository diffusionRepo,
                                  PublicitePaiementRepository paiementRepo,
                                  SocieteRepository societeRepository) {
        this.diffusionRepo = diffusionRepo;
        this.paiementRepo = paiementRepo;
        this.societeRepository = societeRepository;
    }

    public static class DetailResult {
        public Societe societe;
        public int mois;
        public int annee;
        public BigDecimal montantTotalMensuel; // somme des diffusions du mois
        public BigDecimal montantPayeMensuel;  // somme des paiements du mois
        public BigDecimal pourcentagePaye;     // 0..1
        public List<SocietePaiementVolLigne> lignes = new ArrayList<>();
        public BigDecimal totalPayeReparti;    // somme des montants payés des lignes
        public BigDecimal totalResteReparti;   // somme des restes des lignes
    }

    public DetailResult construireDetail(Long idSociete, int mois, int annee) {
        DetailResult r = new DetailResult();
        r.societe = societeRepository.findById(idSociete).orElse(null);
        r.mois = mois;
        r.annee = annee;
        r.montantTotalMensuel = diffusionRepo.montantTotalParSocieteEtPeriode(idSociete, mois, annee);
        if (r.montantTotalMensuel == null) r.montantTotalMensuel = BigDecimal.ZERO;
        r.montantPayeMensuel = paiementRepo.totalPayeParSocieteEtPeriode(idSociete, mois, annee);
        if (r.montantPayeMensuel == null) r.montantPayeMensuel = BigDecimal.ZERO;
        if (r.montantTotalMensuel.signum() <= 0) {
            r.pourcentagePaye = BigDecimal.ZERO;
        } else {
            r.pourcentagePaye = r.montantPayeMensuel
                    .divide(r.montantTotalMensuel, 10, RoundingMode.HALF_UP);
            if (r.pourcentagePaye.compareTo(BigDecimal.ONE) > 0) {
                r.pourcentagePaye = BigDecimal.ONE; // ne jamais dépasser 100%
            }
        }
        List<Object[]> rows = diffusionRepo.montantParVolPourSocieteEtPeriode(idSociete, mois, annee);
        BigDecimal totalPaye = BigDecimal.ZERO;
        BigDecimal totalReste = BigDecimal.ZERO;
        for (Object[] o : rows) {
            Long idVol = (Long) o[0];
            String dep = (String) o[1];
            String arr = (String) o[2];
            String immat = (String) o[3];
            java.time.LocalDateTime dateVol = (java.time.LocalDateTime) o[4];
            BigDecimal montantVol = (BigDecimal) o[5];
            SocietePaiementVolLigne lig = new SocietePaiementVolLigne(idVol, dep, arr, immat, dateVol, montantVol, r.pourcentagePaye);
            r.lignes.add(lig);
            totalPaye = totalPaye.add(lig.getMontantPayeVol());
            totalReste = totalReste.add(lig.getMontantResteVol());
        }
        r.totalPayeReparti = totalPaye;
        r.totalResteReparti = totalReste;
        return r;
    }
}
