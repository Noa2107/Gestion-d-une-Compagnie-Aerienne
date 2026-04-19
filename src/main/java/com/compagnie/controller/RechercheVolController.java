package com.compagnie.controller;

import com.compagnie.model.Vol;
import com.compagnie.service.VolService;
import com.compagnie.service.AeroportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/recherche-vols")
public class RechercheVolController {
    
    @Autowired
    private VolService volService;
    
    @Autowired
    private AeroportService aeroportService;
    
    @GetMapping
    public String showRechercheForm(Model model) {
        model.addAttribute("aeroports", aeroportService.getAllAeroports());
        model.addAttribute("title", "Rechercher un Vol");
        return "recherche/search";
    }
    
    @PostMapping
    public String rechercherVols(@RequestParam(required = false) Long idDepart,
                                 @RequestParam(required = false) Long idArrivee,
                                 @RequestParam(required = false) String codeDepart,
                                 @RequestParam(required = false) String codeArrivee,
                                 @RequestParam(required = false) String dateRecherche,
                                 @RequestParam(required = false) String heureRecherche,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        
        try {
            LocalDateTime dateTimeRecherche = null;
            
            if (dateRecherche != null && !dateRecherche.trim().isEmpty()) {
                LocalDate date = LocalDate.parse(dateRecherche);
                if (heureRecherche != null && !heureRecherche.trim().isEmpty()) {
                    LocalTime heure = LocalTime.parse(heureRecherche);
                    dateTimeRecherche = LocalDateTime.of(date, heure);
                } else {
                    dateTimeRecherche = LocalDateTime.of(date, LocalTime.MIDNIGHT);
                }
            } else {
                throw new IllegalArgumentException("La date de recherche est obligatoire");
            }
            
            List<Vol> vols;
            
            if (codeDepart != null && !codeDepart.trim().isEmpty() && 
                codeArrivee != null && !codeArrivee.trim().isEmpty()) {
                vols = volService.rechercherVolsParCodeAeroport(codeDepart, codeArrivee, dateTimeRecherche);
            } else if (idDepart != null && idArrivee != null) {
                vols = volService.rechercherVolsDisponibles(idDepart, idArrivee, dateTimeRecherche);
            } else {
                throw new IllegalArgumentException("Veuillez selectionner les aeroports de depart et d'arrivee");
            }
            
            List<VolDisponibleDTO> volsDisponibles = vols.stream()
                .map(vol -> {
                    int placesDispo = volService.getPlacesDisponibles(vol);
                    return new VolDisponibleDTO(vol, placesDispo);
                })
                .filter(dto -> dto.getPlacesDisponibles() > 0)
                .collect(Collectors.toList());
            
            model.addAttribute("volsDisponibles", volsDisponibles);
            model.addAttribute("aeroports", aeroportService.getAllAeroports());
            model.addAttribute("codeDepart", codeDepart);
            model.addAttribute("codeArrivee", codeArrivee);
            model.addAttribute("dateRecherche", dateRecherche);
            model.addAttribute("heureRecherche", heureRecherche);
            model.addAttribute("title", "Resultats de Recherche");
            
            if (volsDisponibles.isEmpty()) {
                model.addAttribute("message", "Aucun vol disponible pour les criteres de recherche specifies.");
            }
            
            return "recherche/results";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/recherche-vols";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la recherche: " + e.getMessage());
            return "redirect:/recherche-vols";
        }
    }
    
    public static class VolDisponibleDTO {
        private Vol vol;
        private int placesDisponibles;
        
        public VolDisponibleDTO(Vol vol, int placesDisponibles) {
            this.vol = vol;
            this.placesDisponibles = placesDisponibles;
        }
        
        public Vol getVol() {
            return vol;
        }
        
        public int getPlacesDisponibles() {
            return placesDisponibles;
        }
    }
}

