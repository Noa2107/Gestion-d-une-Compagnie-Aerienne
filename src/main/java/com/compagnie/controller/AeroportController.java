package com.compagnie.controller;

import com.compagnie.model.Aeroport;
import com.compagnie.service.AeroportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aeroports")
public class AeroportController {
    
    @Autowired
    private AeroportService aeroportService;
    
    @GetMapping
    public String listAeroports(Model model) {
        List<Aeroport> aeroports = aeroportService.getAllAeroports();
        model.addAttribute("aeroports", aeroports);
        model.addAttribute("title", "Liste des Aeroports");
        return "aeroport/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("aeroport", new Aeroport());
        model.addAttribute("title", "Ajouter un Aeroport");
        return "aeroport/add";
    }
    
    @PostMapping("/add")
    public String addAeroport(@Valid @ModelAttribute Aeroport aeroport, BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "aeroport/add";
        }
        
        try {
            aeroportService.addAeroport(aeroport);
            redirectAttributes.addFlashAttribute("success", "Aeroport ajoute avec succes");
            return "redirect:/aeroports";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/aeroports/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return aeroportService.getAeroportById(id)
            .map(aeroport -> {
                model.addAttribute("aeroport", aeroport);
                model.addAttribute("title", "Modifier un Aeroport");
                return "aeroport/edit";
            })
            .orElse("redirect:/aeroports");
    }
    
    @PostMapping("/edit/{id}")
    public String updateAeroport(@PathVariable Long id, @Valid @ModelAttribute Aeroport aeroport,
                                BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "aeroport/edit";
        }
        
        try {
            aeroportService.updateAeroport(id, aeroport);
            redirectAttributes.addFlashAttribute("success", "Aeroport modifie avec succes");
            return "redirect:/aeroports";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/aeroports/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteAeroport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            aeroportService.deleteAeroport(id);
            redirectAttributes.addFlashAttribute("success", "Aeroport supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/aeroports";
    }
}

