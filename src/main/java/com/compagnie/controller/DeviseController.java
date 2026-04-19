package com.compagnie.controller;

import com.compagnie.model.Devise;
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
@RequestMapping("/devises")
public class DeviseController {
    
    @Autowired
    private DeviseService deviseService;
    
    @GetMapping
    public String listDevises(Model model) {
        List<Devise> devises = deviseService.getAllDevises();
        model.addAttribute("devises", devises);
        model.addAttribute("title", "Liste des Devises");
        return "devise/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("devise", new Devise());
        model.addAttribute("title", "Ajouter une Devise");
        return "devise/add";
    }
    
    @PostMapping("/add")
    public String addDevise(@Valid @ModelAttribute Devise devise, BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "devise/add";
        }
        
        try {
            deviseService.addDevise(devise);
            redirectAttributes.addFlashAttribute("success", "Devise ajoutee avec succes");
            return "redirect:/devises";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/devises/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return deviseService.getDeviseById(id)
            .map(devise -> {
                model.addAttribute("devise", devise);
                model.addAttribute("title", "Modifier une Devise");
                return "devise/edit";
            })
            .orElse("redirect:/devises");
    }
    
    @PostMapping("/edit/{id}")
    public String updateDevise(@PathVariable Long id, @Valid @ModelAttribute Devise devise,
                              BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "devise/edit";
        }
        
        try {
            deviseService.updateDevise(id, devise);
            redirectAttributes.addFlashAttribute("success", "Devise modifiee avec succes");
            return "redirect:/devises";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/devises/edit/" + id;
        }
    }
}

