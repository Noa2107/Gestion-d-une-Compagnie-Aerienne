package com.compagnie.controller;

import com.compagnie.model.TauxChange;
import com.compagnie.service.TauxChangeService;
import com.compagnie.service.DeviseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/taux-change")
public class TauxChangeController {
    
    @Autowired
    private TauxChangeService tauxChangeService;
    
    @Autowired
    private DeviseService deviseService;
    
    @GetMapping
    public String listTauxActifs(Model model) {
        List<TauxChange> taux = tauxChangeService.getTauxActifs();
        model.addAttribute("taux", taux);
        model.addAttribute("title", "Taux de Change Actifs");
        return "tauxchange/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("tauxChange", new TauxChange());
        model.addAttribute("devises", deviseService.getAllDevises());
        model.addAttribute("title", "Ajouter un Taux de Change");
        return "tauxchange/add";
    }
    
    @PostMapping("/add")
    public String addTaux(@Valid @ModelAttribute TauxChange tauxChange, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("devises", deviseService.getAllDevises());
            return "tauxchange/add";
        }
        
        try {
            tauxChangeService.addTaux(tauxChange);
            redirectAttributes.addFlashAttribute("success", "Taux de change ajoute avec succes");
            return "redirect:/taux-change";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("devises", deviseService.getAllDevises());
            return "redirect:/taux-change/add";
        }
    }
}

