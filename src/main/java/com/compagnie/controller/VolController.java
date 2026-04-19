package com.compagnie.controller;

import com.compagnie.model.Vol;
import com.compagnie.service.VolService;
import com.compagnie.service.AvionService;
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
@RequestMapping("/vols")
public class VolController {
    
    @Autowired
    private VolService volService;
    
    @Autowired
    private AvionService avionService;
    
    @Autowired
    private AeroportService aeroportService;
    
    @GetMapping
    public String listVols(Model model) {
        List<Vol> vols = volService.getAllVols();
        model.addAttribute("vols", vols);
        model.addAttribute("title", "Liste des Vols");
        return "vol/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("vol", new Vol());
        model.addAttribute("avions", avionService.getAllAvions());
        model.addAttribute("aeroports", aeroportService.getAllAeroports());
        model.addAttribute("title", "Planifier un Vol");
        return "vol/add";
    }
    
    @PostMapping("/add")
    public String planifierVol(@Valid @ModelAttribute Vol vol, BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "vol/add";
        }
        
        try {
            volService.planifierVol(vol);
            redirectAttributes.addFlashAttribute("success", "Vol planifie avec succes");
            return "redirect:/vols";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("avions", avionService.getAllAvions());
            redirectAttributes.addFlashAttribute("aeroports", aeroportService.getAllAeroports());
            return "redirect:/vols/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return volService.getVolById(id)
            .map(vol -> {
                model.addAttribute("vol", vol);
                model.addAttribute("avions", avionService.getAllAvions());
                model.addAttribute("aeroports", aeroportService.getAllAeroports());
                model.addAttribute("title", "Modifier un Vol");
                return "vol/edit";
            })
            .orElse("redirect:/vols");
    }

    @GetMapping("/delete/{id}")
    public String deleteVol(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            volService.deleteVol(id);
            redirectAttributes.addFlashAttribute("success", "Vol supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vols";
    }
    
    @PostMapping("/edit/{id}")
    public String updateVol(@PathVariable Long id, @Valid @ModelAttribute Vol vol,
                           BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "vol/edit";
        }
        
        try {
            volService.updateVol(id, vol);
            redirectAttributes.addFlashAttribute("success", "Vol modifie avec succes");
            return "redirect:/vols";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/vols/edit/" + id;
        }
    }
    
    @PostMapping("/changer-etat/{id}")
    public String changerEtat(@PathVariable Long id, @RequestParam Integer codeEtat,
                             RedirectAttributes redirectAttributes) {
        try {
            volService.changerEtatVol(id, codeEtat);
            redirectAttributes.addFlashAttribute("success", "Etat du vol modifie avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vols";
    }
}

