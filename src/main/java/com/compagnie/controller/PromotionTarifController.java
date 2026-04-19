package com.compagnie.controller;

import com.compagnie.model.PromotionTarif;
import com.compagnie.service.PromotionTarifService;
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
@RequestMapping("/promotions")
public class PromotionTarifController {

    @Autowired
    private PromotionTarifService promotionTarifService;

    @Autowired
    private VolService volService;

    @Autowired
    private TypePassagerService typePassagerService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("promotions", promotionTarifService.getAllPromotions());
        model.addAttribute("title", "Promotions Tarifs");
        return "promotion/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("promotion", new PromotionTarif());
        model.addAttribute("vols", volService.getAllVols());
        model.addAttribute("typesPassager", typePassagerService.getAllTypes());
        model.addAttribute("title", "Ajouter une Promotion");
        return "promotion/add";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("promotion") PromotionTarif promotion,
                      BindingResult result,
                      RedirectAttributes redirectAttributes,
                      Model model) {
        if (result.hasErrors()) {
            model.addAttribute("vols", volService.getAllVols());
            model.addAttribute("typesPassager", typePassagerService.getAllTypes());
            return "promotion/add";
        }

        try {
            promotionTarifService.addPromotion(promotion);
            redirectAttributes.addFlashAttribute("success", "Promotion ajoutee avec succes");
            return "redirect:/promotions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vols", volService.getAllVols());
            model.addAttribute("typesPassager", typePassagerService.getAllTypes());
            return "promotion/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return promotionTarifService.getPromotionById(id)
                .map(p -> {
                    model.addAttribute("promotion", p);
                    model.addAttribute("vols", volService.getAllVols());
                    model.addAttribute("typesPassager", typePassagerService.getAllTypes());
                    model.addAttribute("title", "Modifier une Promotion");
                    return "promotion/edit";
                })
                .orElse("redirect:/promotions");
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("promotion") PromotionTarif promotion,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            model.addAttribute("vols", volService.getAllVols());
            model.addAttribute("typesPassager", typePassagerService.getAllTypes());
            return "promotion/edit";
        }

        try {
            promotionTarifService.updatePromotion(id, promotion);
            redirectAttributes.addFlashAttribute("success", "Promotion modifiee avec succes");
            return "redirect:/promotions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("vols", volService.getAllVols());
            model.addAttribute("typesPassager", typePassagerService.getAllTypes());
            return "promotion/edit";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            promotionTarifService.deletePromotion(id);
            redirectAttributes.addFlashAttribute("success", "Promotion supprimee avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/promotions";
    }
}
