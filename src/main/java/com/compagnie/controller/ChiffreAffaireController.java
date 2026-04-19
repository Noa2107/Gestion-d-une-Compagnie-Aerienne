package com.compagnie.controller;

import com.compagnie.service.ChiffreAffaireService;
import com.compagnie.service.VolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/chiffre-affaires")
public class ChiffreAffaireController {

    @Autowired
    private VolService volService;

    @Autowired
    private ChiffreAffaireService chiffreAffaireService;

    @GetMapping
    public String page(@RequestParam(required = false) Long idVol, Model model) {
        model.addAttribute("vols", volService.getAllVols());
        model.addAttribute("selectedVolId", idVol);
        model.addAttribute("title", "Chiffre d'Affaires");

        if (idVol != null) {
            try {
                Map<String, Object> ca = chiffreAffaireService.calculerCaParVol(idVol, LocalDateTime.now());
                model.addAllAttributes(ca);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        return "ca/chiffre-affaires";
    }
}
