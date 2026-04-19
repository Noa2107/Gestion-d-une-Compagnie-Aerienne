package com.compagnie.service;

import com.compagnie.repository.ProduitExtraRepository;
import com.compagnie.service.dto.VolCa;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanyCaService {

    private final VolCaService volCaService;
    private final ProduitExtraRepository produitExtraRepository;

    public CompanyCaService(VolCaService volCaService,
                            ProduitExtraRepository produitExtraRepository) {
        this.volCaService = volCaService;
        this.produitExtraRepository = produitExtraRepository;
    }

    public Map<String, Object> calculerCaTotalCompagnie(Integer mois, Integer annee, Long avionId) {
        Map<String, Object> res = new HashMap<>();

        BigDecimal caBillets = BigDecimal.ZERO;
        BigDecimal caPublicites = BigDecimal.ZERO;
        BigDecimal payePubs = BigDecimal.ZERO;
        BigDecimal restePubs = BigDecimal.ZERO;
        BigDecimal caProduitsExtra = BigDecimal.ZERO;

        List<VolCa> vols = java.util.Collections.emptyList();

        if (mois != null && annee != null) {
            vols = volCaService.listerVolsAvecCaPourPeriode(mois, annee, avionId, null);

            for (VolCa v : vols) {
                if (v == null) continue;
                if (v.getCaBillets() != null) caBillets = caBillets.add(v.getCaBillets());
                if (v.getCaPublicites() != null) caPublicites = caPublicites.add(v.getCaPublicites());
                if (v.getMontantPayePublicites() != null) payePubs = payePubs.add(v.getMontantPayePublicites());
                if (v.getResteAPayerPublicites() != null) restePubs = restePubs.add(v.getResteAPayerPublicites());
            }

            caProduitsExtra = produitExtraRepository.caTheoriqueParPeriode(mois, annee);
            if (caProduitsExtra == null) caProduitsExtra = BigDecimal.ZERO;
        }

        BigDecimal caTotal = caBillets.add(caPublicites).add(caProduitsExtra);

        res.put("vols", vols);
        res.put("caBillets", caBillets);
        res.put("caPublicites", caPublicites);
        res.put("montantPayePublicites", payePubs);
        res.put("resteAPayerPublicites", restePubs);
        res.put("caProduitsExtraTheorique", caProduitsExtra);
        res.put("caTotal", caTotal);

        return res;
    }
}
