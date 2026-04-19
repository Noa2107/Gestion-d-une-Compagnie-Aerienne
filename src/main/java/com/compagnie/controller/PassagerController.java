package com.compagnie.controller;

import com.compagnie.model.Passager;
import com.compagnie.service.PassagerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/passagers")
public class PassagerController {
    
    @Autowired
    private PassagerService passagerService;
    
    @GetMapping
    public String listPassagers(Model model) {
        List<Passager> passagers = passagerService.getAllPassagers();
        model.addAttribute("passagers", passagers);
        model.addAttribute("title", "Liste des Passagers");
        return "passager/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("passager", new Passager());
        model.addAttribute("title", "Ajouter un Passager");
        return "passager/add";
    }
    
    @PostMapping("/add")
    public String addPassager(@Valid @ModelAttribute Passager passager, BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "passager/add";
        }
        
        try {
            passagerService.addPassager(passager);
            redirectAttributes.addFlashAttribute("success", "Passager ajoute avec succes");
            return "redirect:/passagers";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/passagers/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return passagerService.getPassagerById(id)
            .map(passager -> {
                model.addAttribute("passager", passager);
                model.addAttribute("title", "Modifier un Passager");
                return "passager/edit";
            })
            .orElse("redirect:/passagers");
    }
    
    @PostMapping("/edit/{id}")
    public String updatePassager(@PathVariable Long id, @Valid @ModelAttribute Passager passager,
                                BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "passager/edit";
        }
        
        try {
            passagerService.updatePassager(id, passager);
            redirectAttributes.addFlashAttribute("success", "Passager modifie avec succes");
            return "redirect:/passagers";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/passagers/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deletePassager(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            passagerService.deletePassager(id);
            redirectAttributes.addFlashAttribute("success", "Passager supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/passagers";
    }
}

