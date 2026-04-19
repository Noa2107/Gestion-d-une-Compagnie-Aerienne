package com.compagnie.controller;

import com.compagnie.model.TarifVolClasse;
import com.compagnie.service.TarifVolClasseService;
import com.compagnie.service.TypePassagerService;
import com.compagnie.service.VolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tarifs")
public class TarifVolClasseController {

    @Autowired
    private TarifVolClasseService tarifVolClasseService;

    @Autowired
    private VolService volService;

    @Autowired
    private TypePassagerService typePassagerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tarifs", tarifVolClasseService.getAllTarifs());
        model.addAttribute("title", "Tarifs Vol / Classe");
        return "tarif/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("tarif", new TarifVolClasse());
        model.addAttribute("vols", volService.getAllVols());
        model.addAttribute("typesPassager", typePassagerService.getAllTypes());
        model.addAttribute("title", "Ajouter un Tarif");
        return "tarif/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("tarif") TarifVolClasse tarif,
                      BindingResult result,
                      RedirectAttributes redirectAttributes,
                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("vols", volService.getAllVols());
            model.addAttribute("typesPassager", typePassagerService.getAllTypes());
            return "tarif/add";
        }

        try {
            tarifVolClasseService.addTarif(tarif);
            redirectAttributes.addFlashAttribute("success", "Tarif ajoute avec succes");
            return "redirect:/tarifs";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vols", volService.getAllVols());
            model.addAttribute("typesPassager", typePassagerService.getAllTypes());
            return "tarif/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return tarifVolClasseService.getTarifById(id)
                .map(t -> {
                    model.addAttribute("tarif", t);
                    model.addAttribute("vols", volService.getAllVols());
                    model.addAttribute("typesPassager", typePassagerService.getAllTypes());
                    model.addAttribute("title", "Modifier un Tarif");
                    return "tarif/edit";
                })
                .orElse("redirect:/tarifs");
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("tarif") TarifVolClasse tarif,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("vols", volService.getAllVols());
            model.addAttribute("typesPassager", typePassagerService.getAllTypes());
            return "tarif/edit";
        }

        try {
            tarifVolClasseService.updateTarif(id, tarif);
            redirectAttributes.addFlashAttribute("success", "Tarif modifie avec succes");
            return "redirect:/tarifs";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vols", volService.getAllVols());
            model.addAttribute("typesPassager", typePassagerService.getAllTypes());
            return "tarif/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            tarifVolClasseService.deleteTarif(id);
            redirectAttributes.addFlashAttribute("success", "Tarif supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tarifs";
    }
}
