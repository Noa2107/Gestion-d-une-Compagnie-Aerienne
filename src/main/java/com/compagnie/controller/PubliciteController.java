package com.compagnie.controller;

import com.compagnie.repository.PublicitePaiementRepository;
import com.compagnie.repository.PubliciteRepository;
import com.compagnie.repository.SocieteRepository;
import com.compagnie.service.PubliciteService;
import com.compagnie.service.dto.SocieteCa;
import com.compagnie.service.dto.SocieteReglement;
import com.compagnie.model.PublicitePaiement;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Controller
@RequestMapping("/publicites")
public class PubliciteController {

    private final PubliciteService publiciteService;
    private final SocieteRepository societeRepository;
    private final PubliciteRepository publiciteRepository;
    private final PublicitePaiementRepository publicitePaiementRepository;

    public PubliciteController(PubliciteService publiciteService, SocieteRepository societeRepository, PubliciteRepository publiciteRepository, PublicitePaiementRepository publicitePaiementRepository) {
        this.publiciteService = publiciteService;
        this.societeRepository = societeRepository;
        this.publiciteRepository = publiciteRepository;
        this.publicitePaiementRepository = publicitePaiementRepository;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Integer mois,
                        @RequestParam(required = false) Integer annee,
                        Model model) {
        model.addAttribute("activePage", "publicites");
        model.addAttribute("societes", societeRepository.findAll());
        BigDecimal caTotal = BigDecimal.ZERO;
        java.util.List<SocieteCa> caParSocietes = java.util.Collections.emptyList();
        java.util.List<SocieteReglement> reglements = java.util.Collections.emptyList();
        BigDecimal totalPaye = BigDecimal.ZERO;
        if (mois != null && annee != null) {
            caTotal = publiciteService.calculerCaTotal(mois, annee);
            caParSocietes = publiciteService.calculerCaParSocietePourPeriode(mois, annee);
            reglements = publiciteService.reglementsParSocietePourPeriode(mois, annee);
            totalPaye = publiciteService.totalPayePeriode(mois, annee);
        }
        model.addAttribute("mois", mois);
        model.addAttribute("annee", annee);
        model.addAttribute("caTotal", caTotal);
        model.addAttribute("caParSocietes", caParSocietes);
        model.addAttribute("reglements", reglements);
        model.addAttribute("totalPaye", totalPaye);
        return "publicite/ca";
    }

    @PostMapping("/payer")
    public String enregistrerPaiement(@RequestParam Long societeId,
                                      @RequestParam Integer mois,
                                      @RequestParam Integer annee,
                                      @RequestParam BigDecimal montant,
                                      @RequestParam(required = false) String reference,
                                      @RequestParam(name = "datePaiement") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datePaiement) {
        PublicitePaiement p = new PublicitePaiement();
        societeRepository.findById(societeId).ifPresent(p::setSociete);
        p.setMois(mois);
        p.setAnnee(annee);
        p.setMontant(montant);
        p.setReference(reference);
        if (datePaiement != null) {
            p.setDatePaiement(datePaiement.atStartOfDay());
        } else {
            p.setDatePaiement(LocalDateTime.now());
        }
        publicitePaiementRepository.save(p);
        return "redirect:/publicites?mois=" + mois + "&annee=" + annee;
    }
}
