package com.compagnie.controller;

import com.compagnie.model.Cargo;
import com.compagnie.service.CargoService;
import com.compagnie.service.VolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/cargo")
public class CargoController {
    
    @Autowired
    private CargoService cargoService;
    
    @GetMapping
    public String listCargos(Model model) {
        List<Cargo> cargos = cargoService.getAllCargos();
        model.addAttribute("cargos", cargos);
        model.addAttribute("title", "Liste des Cargos");
        return "cargo/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("cargo", new Cargo());
        model.addAttribute("title", "Ajouter un Cargo");
        return "cargo/add";
    }
    
    @PostMapping("/add")
    public String addCargo(@Valid @ModelAttribute Cargo cargo, BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cargo/add";
        }
        
        try {
            cargoService.addCargo(cargo);
            redirectAttributes.addFlashAttribute("success", "Cargo ajoute avec succes");
            return "redirect:/cargo";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cargo/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return cargoService.getCargoById(id)
            .map(cargo -> {
                model.addAttribute("cargo", cargo);
                model.addAttribute("title", "Modifier un Cargo");
                return "cargo/edit";
            })
            .orElse("redirect:/cargo");
    }
    
    @PostMapping("/edit/{id}")
    public String updateCargo(@PathVariable Long id, @Valid @ModelAttribute Cargo cargo,
                             BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cargo/edit";
        }
        
        try {
            cargoService.updateCargo(id, cargo);
            redirectAttributes.addFlashAttribute("success", "Cargo modifie avec succes");
            return "redirect:/cargo";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cargo/edit/" + id;
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCargo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            cargoService.deleteCargo(id);
            redirectAttributes.addFlashAttribute("success", "Cargo supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cargo";
    }
    
    @PostMapping("/changer-etat/{id}")
    public String changerEtat(@PathVariable Long id, @RequestParam Integer codeEtat,
                             RedirectAttributes redirectAttributes) {
        try {
            cargoService.changerEtatCargo(id, codeEtat);
            redirectAttributes.addFlashAttribute("success", "Etat du cargo modifie avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cargo";
    }
    
    @PostMapping("/charger/{id}")
    public String chargerCargo(@PathVariable Long id, @RequestParam Long idVol,
                              RedirectAttributes redirectAttributes) {
        try {
            cargoService.chargerCargo(id, idVol);
            redirectAttributes.addFlashAttribute("success", "Cargo charge sur le vol avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cargo";
    }
}

