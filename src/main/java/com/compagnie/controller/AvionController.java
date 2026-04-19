package com.compagnie.controller;

import com.compagnie.model.Avion;
import com.compagnie.model.Vol;
import com.compagnie.service.AvionService;
import com.compagnie.service.AvionRevenuMaxService;
import com.compagnie.service.VolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/avions")
public class AvionController {
    
    @Autowired
    private AvionService avionService;

    @Autowired
    private AvionRevenuMaxService avionRevenuMaxService;

    @Autowired
    private VolService volService;
    
    @GetMapping
    public String listAvions(Model model) {
        List<Avion> avions = avionService.getAllAvions();
        model.addAttribute("avions", avions);
        model.addAttribute("title", "Liste des Avions");
        return "avion/list";
    }

    @GetMapping("/revenu-max")
    public String revenuMax(@RequestParam(required = false) Long idVol,
                            Model model) {
        model.addAttribute("selectedVolId", idVol);

        model.addAttribute("vols", volService.getAllVols());

        if (idVol != null) {
            Optional<Vol> volOpt = volService.getVolById(idVol);
            if (volOpt.isEmpty()) {
                model.addAttribute("error", "Vol introuvable");
            } else if (volOpt.get().getAvion() == null || volOpt.get().getAvion().getIdAvion() == null) {
                model.addAttribute("error", "Aucun avion associe a ce vol");
            } else {
                Long idAvion = volOpt.get().getAvion().getIdAvion();
                try {
                    model.addAttribute("revenuEconomie", avionRevenuMaxService.calculerRevenuMaxEconomie(idAvion, idVol));
                    model.addAttribute("revenuPremium", avionRevenuMaxService.calculerRevenuMaxPremium(idAvion, idVol));
                    model.addAttribute("revenuPremiere", avionRevenuMaxService.calculerRevenuMaxPremiere(idAvion, idVol));
                    model.addAttribute("revenuMax", avionRevenuMaxService.calculerRevenuMax(idAvion, idVol));
                } catch (IllegalArgumentException e) {
                    model.addAttribute("error", e.getMessage());
                }
            }
        }

        model.addAttribute("title", "Revenu maximal avion");
        return "avion/revenu-max";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("avion", new Avion());
        model.addAttribute("title", "Ajouter un Avion");
        return "avion/add";
    }
    
    @PostMapping("/add")
    public String addAvion(@Valid @ModelAttribute Avion avion, BindingResult result, 
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "avion/add";
        }
        
        try {
            avionService.addAvion(avion);
            redirectAttributes.addFlashAttribute("success", "Avion ajoute avec succes");
            return "redirect:/avions";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/avions/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return avionService.getAvionById(id)
            .map(avion -> {
                model.addAttribute("avion", avion);
                model.addAttribute("title", "Modifier un Avion");
                return "avion/edit";
            })
            .orElse("redirect:/avions");
    }
    
    @PostMapping("/edit/{id}")
    public String updateAvion(@PathVariable Long id, @Valid @ModelAttribute Avion avion,
                             BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "avion/edit";
        }
        
        try {
            avionService.updateAvion(id, avion);
            redirectAttributes.addFlashAttribute("success", "Avion modifie avec succes");
            return "redirect:/avions";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/avions/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteAvion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            avionService.deleteAvion(id);
            redirectAttributes.addFlashAttribute("success", "Avion supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/avions";
    }
    
    @PostMapping("/changer-etat/{id}")
    public String changerEtat(@PathVariable Long id, @RequestParam Integer codeEtat,
                             RedirectAttributes redirectAttributes) {
        try {
            avionService.changerEtatAvion(id, codeEtat);
            redirectAttributes.addFlashAttribute("success", "Etat de l'avion modifie avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/avions";
    }
}

