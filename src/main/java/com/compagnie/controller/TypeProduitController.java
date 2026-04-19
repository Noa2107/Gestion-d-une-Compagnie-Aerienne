package com.compagnie.controller;

import com.compagnie.model.TypeProduit;
import com.compagnie.service.TypeProduitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/types-produit")
public class TypeProduitController {

    @Autowired
    private TypeProduitService typeProduitService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("typesProduit", typeProduitService.getAllTypesProduit());
        model.addAttribute("title", "Types de Produit");
        return "typeproduit/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("typeProduit", new TypeProduit());
        model.addAttribute("title", "Ajouter un Type de Produit");
        return "typeproduit/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("typeProduit") TypeProduit typeProduit,
                      BindingResult result,
                      RedirectAttributes redirectAttributes,
                      Model model) {
        if (result.hasErrors()) {
            return "typeproduit/add";
        }

        try {
            typeProduitService.addTypeProduit(typeProduit);
            redirectAttributes.addFlashAttribute("success", "Type de produit ajoute avec succes");
            return "redirect:/types-produit";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "typeproduit/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return typeProduitService.getTypeProduitById(id)
                .map(tp -> {
                    model.addAttribute("typeProduit", tp);
                    model.addAttribute("title", "Modifier un Type de Produit");
                    return "typeproduit/edit";
                })
                .orElse("redirect:/types-produit");
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("typeProduit") TypeProduit typeProduit,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            return "typeproduit/edit";
        }

        try {
            typeProduitService.updateTypeProduit(id, typeProduit);
            redirectAttributes.addFlashAttribute("success", "Type de produit modifie avec succes");
            return "redirect:/types-produit";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "typeproduit/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            typeProduitService.deleteTypeProduit(id);
            redirectAttributes.addFlashAttribute("success", "Type de produit supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/types-produit";
    }
}
