package com.compagnie.controller;

import com.compagnie.model.PublicitePaiement;
import com.compagnie.repository.PublicitePaiementRepository;
import com.compagnie.repository.SocieteRepository;
import com.compagnie.service.SocietePaiementService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/paiements-publicites")
public class PublicitePaiementController {

    private final PublicitePaiementRepository paiementRepository;
    private final SocieteRepository societeRepository;
    private final SocietePaiementService societePaiementService;

    public PublicitePaiementController(PublicitePaiementRepository paiementRepository, SocieteRepository societeRepository, SocietePaiementService societePaiementService) {
        this.paiementRepository = paiementRepository;
        this.societeRepository = societeRepository;
        this.societePaiementService = societePaiementService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("activePage", "paiements-publicites");
        model.addAttribute("paiements", paiementRepository.findAll());
        return "paiementpub/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("activePage", "paiements-publicites");
        model.addAttribute("paiement", new PublicitePaiement());
        model.addAttribute("societes", societeRepository.findAll());
        return "paiementpub/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("paiement") PublicitePaiement p,
                      BindingResult br,
                      @RequestParam("societeId") Long societeId,
                      @RequestParam("date") LocalDate date,
                      Model model) {
        if (br.hasErrors()) {
            model.addAttribute("activePage", "paiements-publicites");
            model.addAttribute("societes", societeRepository.findAll());
            return "paiementpub/add";
        }
        societeRepository.findById(societeId).ifPresent(p::setSociete);
        if (date != null) p.setDatePaiement(date.atStartOfDay()); else p.setDatePaiement(LocalDateTime.now());
        paiementRepository.save(p);
        return "redirect:/paiements-publicites";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        return paiementRepository.findById(id)
                .map(p -> {
                    model.addAttribute("activePage", "paiements-publicites");
                    model.addAttribute("paiement", p);
                    model.addAttribute("societes", societeRepository.findAll());
                    return "paiementpub/edit";
                })
                .orElse("redirect:/paiements-publicites");
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("paiement") PublicitePaiement p,
                       BindingResult br,
                       @RequestParam("societeId") Long societeId,
                       @RequestParam("date") LocalDate date,
                       Model model) {
        if (br.hasErrors()) {
            model.addAttribute("activePage", "paiements-publicites");
            model.addAttribute("societes", societeRepository.findAll());
            return "paiementpub/edit";
        }
        p.setId(id);
        societeRepository.findById(societeId).ifPresent(p::setSociete);
        if (date != null) p.setDatePaiement(date.atStartOfDay()); else p.setDatePaiement(LocalDateTime.now());
        paiementRepository.save(p);
        return "redirect:/paiements-publicites";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        paiementRepository.deleteById(id);
        return "redirect:/paiements-publicites";
    }

    @GetMapping("/detail")
    public String detailSociete(@RequestParam Long societeId,
                                @RequestParam Integer mois,
                                @RequestParam Integer annee,
                                Model model) {
        model.addAttribute("activePage", "paiements-publicites");
        var detail = societePaiementService.construireDetail(societeId, mois, annee);
        model.addAttribute("detail", detail);
        return "paiementpub/detail_societe";
    }
}
