package com.compagnie.controller;

import com.compagnie.repository.AvionRepository;
import com.compagnie.service.CompanyCaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("/ca-compagnie")
public class CompanyCaController {

    private final CompanyCaService companyCaService;
    private final AvionRepository avionRepository;

    public CompanyCaController(CompanyCaService companyCaService,
                               AvionRepository avionRepository) {
        this.companyCaService = companyCaService;
        this.avionRepository = avionRepository;
    }

    @GetMapping
    public String index(@RequestParam(required = false) Integer mois,
                        @RequestParam(required = false) Integer annee,
                        @RequestParam(required = false) Long avionId,
                        Model model) {
        model.addAttribute("activePage", "ca-compagnie");
        model.addAttribute("avions", avionRepository.findAll());
        model.addAttribute("mois", mois);
        model.addAttribute("annee", annee);
        model.addAttribute("avionId", avionId);

        Map<String, Object> ca = companyCaService.calculerCaTotalCompagnie(mois, annee, avionId);
        model.addAllAttributes(ca);

        return "ca/compagnie";
    }
}
