package com.compagnie.controller;

import com.compagnie.model.ProduitExtra;
import com.compagnie.service.ProduitExtraService;
import com.compagnie.repository.TypeProduitRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produits-extra")
public class ProduitExtraController {

    @Autowired
    private ProduitExtraService produitExtraService;

    @Autowired
    private TypeProduitRepository typeProduitRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("produitsExtra", produitExtraService.getAllProduitsExtra());
        model.addAttribute("title", "Produits Extra");
        return "produitextra/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("produitExtra", new ProduitExtra());
        model.addAttribute("typesProduit", typeProduitRepository.findAll());
        model.addAttribute("title", "Ajouter un Produit Extra");
        return "produitextra/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("produitExtra") ProduitExtra produitExtra,
                      BindingResult result,
                      RedirectAttributes redirectAttributes,
                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("typesProduit", typeProduitRepository.findAll());
            return "produitextra/add";
        }

        try {
            produitExtraService.addProduitExtra(produitExtra);
            redirectAttributes.addFlashAttribute("success", "Produit extra ajoute avec succes");
            return "redirect:/produits-extra";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("typesProduit", typeProduitRepository.findAll());
            return "produitextra/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return produitExtraService.getProduitExtraById(id)
                .map(p -> {
                    model.addAttribute("produitExtra", p);
                    model.addAttribute("typesProduit", typeProduitRepository.findAll());
                    model.addAttribute("title", "Modifier un Produit Extra");
                    return "produitextra/edit";
                })
                .orElse("redirect:/produits-extra");
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("produitExtra") ProduitExtra produitExtra,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("typesProduit", typeProduitRepository.findAll());
            return "produitextra/edit";
        }

        try {
            produitExtraService.updateProduitExtra(id, produitExtra);
            redirectAttributes.addFlashAttribute("success", "Produit extra modifie avec succes");
            return "redirect:/produits-extra";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("typesProduit", typeProduitRepository.findAll());
            return "produitextra/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produitExtraService.deleteProduitExtra(id);
            redirectAttributes.addFlashAttribute("success", "Produit extra supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/produits-extra";
    }
}
