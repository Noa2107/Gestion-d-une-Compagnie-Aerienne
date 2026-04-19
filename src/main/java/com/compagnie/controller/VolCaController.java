package com.compagnie.controller;

import com.compagnie.repository.AvionRepository;
import com.compagnie.repository.DiffusionPubVolRepository;
import com.compagnie.repository.ProduitExtraRepository;
import com.compagnie.repository.PublicitePaiementRepository;
import com.compagnie.service.VolCaService;
import com.compagnie.service.dto.VolCa;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/ca-vols")
public class VolCaController {

    private final VolCaService volCaService;
    private final AvionRepository avionRepository;
    private final DiffusionPubVolRepository diffusionPubVolRepository;
    private final PublicitePaiementRepository publicitePaiementRepository;
    private final ProduitExtraRepository produitExtraRepository;

    public VolCaController(VolCaService volCaService,
                           AvionRepository avionRepository,
                           DiffusionPubVolRepository diffusionPubVolRepository,
                           PublicitePaiementRepository publicitePaiementRepository,
                           ProduitExtraRepository produitExtraRepository) {
        this.volCaService = volCaService;
        this.avionRepository = avionRepository;
        this.diffusionPubVolRepository = diffusionPubVolRepository;
        this.publicitePaiementRepository = publicitePaiementRepository;
        this.produitExtraRepository = produitExtraRepository;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Integer mois,
                        @RequestParam(required = false) Integer annee,
                        @RequestParam(required = false) Long avionId,
                        @RequestParam(required = false, defaultValue = "par-vol") String mode,
                        Model model) {
        model.addAttribute("activePage", "ca-vols");
        model.addAttribute("avions", avionRepository.findAll());
        List<VolCa> vols = java.util.Collections.emptyList();
        BigDecimal totalCaPubs = BigDecimal.ZERO;
        BigDecimal totalPayePubs = BigDecimal.ZERO;
        BigDecimal totalRestePubs = BigDecimal.ZERO;
        BigDecimal totalCaProduitsExtra = BigDecimal.ZERO;
        if (mois != null && annee != null) {
            vols = volCaService.listerVolsAvecCaPourPeriode(mois, annee, avionId, null);

            totalCaPubs = diffusionPubVolRepository.caPublicitesParPeriode(mois, annee);
            totalPayePubs = publicitePaiementRepository.totalPayeParPeriode(mois, annee);
            totalCaProduitsExtra = produitExtraRepository.caTheoriqueParPeriode(mois, annee);
            if (totalCaPubs == null) totalCaPubs = BigDecimal.ZERO;
            if (totalPayePubs == null) totalPayePubs = BigDecimal.ZERO;
            if (totalCaProduitsExtra == null) totalCaProduitsExtra = BigDecimal.ZERO;
            totalRestePubs = totalCaPubs.subtract(totalPayePubs);
            if (totalRestePubs.compareTo(BigDecimal.ZERO) < 0) totalRestePubs = BigDecimal.ZERO;
        }
        model.addAttribute("mois", mois);
        model.addAttribute("annee", annee);
        model.addAttribute("avionId", avionId);
        model.addAttribute("mode", mode);
        model.addAttribute("totalCaPubs", totalCaPubs);
        model.addAttribute("totalPayePubs", totalPayePubs);
        model.addAttribute("totalRestePubs", totalRestePubs);
        model.addAttribute("totalCaProduitsExtra", totalCaProduitsExtra);
        model.addAttribute("vols", vols);
        return "vol/ca";
    }

    @PostMapping("/diffusion")
    public String demanderDiffusion(@RequestParam Long idVol,
                                    @RequestParam Long societeId,
                                    @RequestParam Integer nbDemandes,
                                    @RequestParam Integer mois,
                                    @RequestParam Integer annee,
                                    @RequestParam(required = false) Long avionId,
                                    @RequestParam(required = false, defaultValue = "par-vol") String mode) {
        volCaService.allouerDiffusionsSousQuota(idVol, societeId, nbDemandes == null ? 0 : nbDemandes);
        String redirect = "redirect:/ca-vols?mois=" + mois + "&annee=" + annee;
        if (avionId != null) {
            redirect += "&avionId=" + avionId;
        }
        if (mode != null && !mode.isBlank()) {
            redirect += "&mode=" + mode;
        }
        return redirect;
    }
}
