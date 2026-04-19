package com.compagnie.controller;

import com.compagnie.model.Publicite;
import com.compagnie.model.Societe;
import com.compagnie.repository.PubliciteRepository;
import com.compagnie.repository.SocieteRepository;
import com.compagnie.repository.VolRepository;
import com.compagnie.repository.AvionRepository;
import com.compagnie.repository.DiffusionPubVolRepository;
import com.compagnie.service.VolCaService;
import com.compagnie.service.dto.DiffusionVolSocieteLigne;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/gestion-publicites")
public class GestionPublicitesController {

    private final SocieteRepository societeRepository;
    private final PubliciteRepository publiciteRepository;
    private final VolRepository volRepository;
    private final VolCaService volCaService;
    private final AvionRepository avionRepository;
    private final DiffusionPubVolRepository diffusionPubVolRepository;

    public GestionPublicitesController(SocieteRepository societeRepository, PubliciteRepository publiciteRepository, VolRepository volRepository, VolCaService volCaService, AvionRepository avionRepository, DiffusionPubVolRepository diffusionPubVolRepository) {
        this.societeRepository = societeRepository;
        this.publiciteRepository = publiciteRepository;
        this.volRepository = volRepository;
        this.volCaService = volCaService;
        this.avionRepository = avionRepository;
        this.diffusionPubVolRepository = diffusionPubVolRepository;
    }

    /* Page d'accueil du module */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("activePage", "gestion-publicites");
        model.addAttribute("societesCount", societeRepository.count());
        model.addAttribute("publicitesCount", publiciteRepository.count());
        return "gestionpub/index";
    }

    @GetMapping("/diffusions")
    public String listDiffusions(Model model) {
        model.addAttribute("activePage", "gestion-publicites");
        java.util.List<DiffusionVolSocieteLigne> lignes = new java.util.ArrayList<>();
        for (Object[] row : diffusionPubVolRepository.listeDiffusionsGroupeesParVolEtSociete()) {
            if (row == null || row.length < 12) continue;
            Integer annee = row[0] == null ? null : ((Number) row[0]).intValue();
            Integer mois = row[1] == null ? null : ((Number) row[1]).intValue();
            Long idVol = row[2] == null ? null : ((Number) row[2]).longValue();
            String dep = (String) row[3];
            String arr = (String) row[4];
            String immat = (String) row[5];
            java.time.LocalDateTime dateVol = (java.time.LocalDateTime) row[6];
            Long idSociete = row[7] == null ? null : ((Number) row[7]).longValue();
            String socNom = (String) row[8];
            Integer nb = row[9] == null ? 0 : ((Number) row[9]).intValue();
            BigDecimal prixU = toBigDecimal(row[10]);
            BigDecimal montant = toBigDecimal(row[11]);
            lignes.add(new DiffusionVolSocieteLigne(annee, mois, idVol, dep, arr, immat, dateVol, idSociete, socNom, nb, prixU, montant));
        }
        model.addAttribute("diffusions", lignes);
        return "gestionpub/diffusions/list";
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal bd) return bd;
        if (value instanceof Number n) {
            if (value instanceof Float || value instanceof Double) {
                return BigDecimal.valueOf(n.doubleValue());
            }
            return BigDecimal.valueOf(n.longValue());
        }
        if (value instanceof String s) {
            if (s.isBlank()) return BigDecimal.ZERO;
            return new BigDecimal(s);
        }
        return new BigDecimal(Objects.toString(value));
    }

    /* CRUD Sociétés */
    @GetMapping("/societes")
    public String listSocietes(Model model) {
        model.addAttribute("activePage", "gestion-publicites");
        model.addAttribute("societes", societeRepository.findAll());
        return "gestionpub/societes/list";
    }

    @GetMapping("/societes/add")
    public String addSocieteForm(Model model) {
        model.addAttribute("activePage", "gestion-publicites");
        model.addAttribute("societe", new Societe());
        return "gestionpub/societes/add";
    }

    @PostMapping("/societes/add")
    public String addSociete(@Valid @ModelAttribute("societe") Societe societe,
                             BindingResult bindingResult,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "gestion-publicites");
            return "gestionpub/societes/add";
        }
        societeRepository.save(societe);
        return "redirect:/gestion-publicites/societes";
    }

    @GetMapping("/societes/edit/{id}")
    public String editSocieteForm(@PathVariable Long id, Model model) {
        Optional<Societe> s = societeRepository.findById(id);
        if (s.isEmpty()) return "redirect:/gestion-publicites/societes";
        model.addAttribute("activePage", "gestion-publicites");
        model.addAttribute("societe", s.get());
        return "gestionpub/societes/edit";
    }

    @PostMapping("/societes/edit/{id}")
    public String editSociete(@PathVariable Long id,
                              @Valid @ModelAttribute("societe") Societe societe,
                              BindingResult bindingResult,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "gestion-publicites");
            return "gestionpub/societes/edit";
        }
        societe.setIdSociete(id);
        societeRepository.save(societe);
        return "redirect:/gestion-publicites/societes";
    }

    @PostMapping("/societes/delete/{id}")
    public String deleteSociete(@PathVariable Long id) {
        societeRepository.deleteById(id);
        return "redirect:/gestion-publicites/societes";
    }

    /* CRUD Publicites */
    @GetMapping("/publicites")
    public String listPublicites(Model model) {
        model.addAttribute("activePage", "gestion-publicites");
        model.addAttribute("publicites", publiciteRepository.findAll());
        return "gestionpub/publicites/list";
    }

    @GetMapping("/publicites/add")
    public String addPubliciteForm(Model model) {
        model.addAttribute("activePage", "gestion-publicites");
        model.addAttribute("publicite", new Publicite());
        model.addAttribute("societes", societeRepository.findAll());
        return "gestionpub/publicites/add";
    }

    @PostMapping("/publicites/add")
    public String addPublicite(@Valid @ModelAttribute("publicite") Publicite publicite,
                               BindingResult bindingResult,
                               @RequestParam("societeId") Long societeId,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "gestion-publicites");
            model.addAttribute("societes", societeRepository.findAll());
            return "gestionpub/publicites/add";
        }
        societeRepository.findById(societeId).ifPresent(publicite::setSociete);
        publiciteRepository.save(publicite);
        return "redirect:/gestion-publicites/publicites";
    }

    @GetMapping("/publicites/edit/{id}")
    public String editPubliciteForm(@PathVariable Long id, Model model) {
        Optional<Publicite> p = publiciteRepository.findById(id);
        if (p.isEmpty()) return "redirect:/gestion-publicites/publicites";
        model.addAttribute("activePage", "gestion-publicites");
        model.addAttribute("publicite", p.get());
        model.addAttribute("societes", societeRepository.findAll());
        return "gestionpub/publicites/edit";
    }

    @PostMapping("/publicites/edit/{id}")
    public String editPublicite(@PathVariable Long id,
                                @Valid @ModelAttribute("publicite") Publicite publicite,
                                BindingResult bindingResult,
                                @RequestParam("societeId") Long societeId,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("activePage", "gestion-publicites");
            model.addAttribute("societes", societeRepository.findAll());
            return "gestionpub/publicites/edit";
        }
        publicite.setIdPublicite(id);
        societeRepository.findById(societeId).ifPresent(publicite::setSociete);
        publiciteRepository.save(publicite);
        return "redirect:/gestion-publicites/publicites";
    }

    @PostMapping("/publicites/delete/{id}")
    public String deletePublicite(@PathVariable Long id) {
        publiciteRepository.deleteById(id);
        return "redirect:/gestion-publicites/publicites";
    }

    /* Détail société: filtrer les vols et allouer des diffusions */
    @GetMapping("/societes/{id}/detail")
    public String detailSociete(@PathVariable("id") Long id,
                                @RequestParam(required = false) String dateDebut,
                                @RequestParam(required = false) String dateFin,
                                @RequestParam(required = false) Long avionId,
                                Model model) {
        Optional<Societe> s = societeRepository.findById(id);
        if (s.isEmpty()) return "redirect:/gestion-publicites/societes";
        model.addAttribute("activePage", "gestion-publicites");
        model.addAttribute("societe", s.get());
        model.addAttribute("avions", avionRepository.findAll());
        java.util.List<com.compagnie.model.Vol> vols = java.util.Collections.emptyList();
        if (dateDebut != null && !dateDebut.isBlank() && dateFin != null && !dateFin.isBlank()) {
            LocalDateTime debut = LocalDate.parse(dateDebut).atStartOfDay();
            LocalDateTime fin = LocalDate.parse(dateFin).atTime(LocalTime.MAX);
            if (avionId != null) {
                vols = volRepository.findVolsByAvionAndDateRange(avionId, debut, fin);
            } else {
                vols = volRepository.findByDateVolBetweenOrderByDateVolAsc(debut, fin);
            }
        }
        model.addAttribute("dateDebut", dateDebut);
        model.addAttribute("dateFin", dateFin);
        model.addAttribute("avionId", avionId);
        model.addAttribute("vols", vols);
        return "gestionpub/societes/detail";
    }

    @PostMapping("/societes/{id}/diffuser")
    public String allouerDiffusions(@PathVariable("id") Long idSociete,
                                    @RequestParam Long idVol,
                                    @RequestParam Integer nbDiffusions,
                                    @RequestParam String dateDebut,
                                    @RequestParam String dateFin) {
        volCaService.allouerDiffusionsSousQuota(idVol, idSociete, nbDiffusions == null ? 0 : nbDiffusions);
        return "redirect:/gestion-publicites/societes/" + idSociete + "/detail?dateDebut=" + dateDebut + "&dateFin=" + dateFin;
    }
}
