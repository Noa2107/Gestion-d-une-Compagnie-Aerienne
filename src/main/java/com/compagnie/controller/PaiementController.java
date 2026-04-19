package com.compagnie.controller;

import com.compagnie.model.PaiementReservation;
import com.compagnie.model.PaiementCargo;
import com.compagnie.service.PdfExportService;
import com.compagnie.service.PaiementService;
import com.compagnie.service.ReservationService;
import com.compagnie.service.CargoService;
import com.compagnie.service.DeviseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/paiements")
public class PaiementController {
    
    @Autowired
    private PaiementService paiementService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private CargoService cargoService;
    
    @Autowired
    private DeviseService deviseService;

    @Autowired
    private PdfExportService pdfExportService;
    
    @GetMapping
    public String listAllPaiements(Model model) {
        model.addAttribute("paiementsReservation", paiementService.getAllPaiementsReservation());
        model.addAttribute("paiementsCargo", paiementService.getAllPaiementsCargo());
        model.addAttribute("title", "Liste des Paiements");
        return "paiement/list";
    }

    @GetMapping("/reservation")
    public String listPaiementsReservation(Model model) {
        model.addAttribute("paiementsReservation", paiementService.getAllPaiementsReservation());
        model.addAttribute("title", "Liste des Paiements Reservation");
        return "paiement/reservation/list";
    }

    @GetMapping("/reservation/details/{id}")
    public String detailsPaiementReservation(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return paiementService.getPaiementReservationById(id)
                .map(paiement -> {
                    model.addAttribute("paiement", paiement);
                    model.addAttribute("title", "Details Paiement Reservation");
                    return "paiement/reservation/details";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Paiement introuvable");
                    return "redirect:/paiements/reservation";
                });
    }

    @GetMapping("/reservation/details/{id}/pdf")
    public ResponseEntity<byte[]> exportPaiementReservationPdf(@PathVariable Long id) {
        PaiementReservation paiement = paiementService.getPaiementReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable"));

        byte[] pdf = pdfExportService.exportPaiementReservationReceipt(paiement);
        String filename = "recu_airline_paiement_reservation_" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/reservation/delete/{id}")
    public String deletePaiementReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paiementService.deletePaiementReservation(id);
            redirectAttributes.addFlashAttribute("success", "Paiement supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/paiements/reservation";
    }

    @GetMapping("/cargo")
    public String listPaiementsCargo(Model model) {
        model.addAttribute("paiementsCargo", paiementService.getAllPaiementsCargo());
        model.addAttribute("title", "Liste des Paiements Cargo");
        return "paiement/cargo/list";
    }

    @GetMapping("/cargo/details/{id}")
    public String detailsPaiementCargo(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return paiementService.getPaiementCargoById(id)
                .map(paiement -> {
                    model.addAttribute("paiement", paiement);
                    model.addAttribute("title", "Details Paiement Cargo");
                    return "paiement/cargo/details";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Paiement introuvable");
                    return "redirect:/paiements/cargo";
                });
    }

    @GetMapping("/cargo/details/{id}/pdf")
    public ResponseEntity<byte[]> exportPaiementCargoPdf(@PathVariable Long id) {
        PaiementCargo paiement = paiementService.getPaiementCargoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paiement introuvable"));

        byte[] pdf = pdfExportService.exportPaiementCargoReceipt(paiement);
        String filename = "recu_airline_paiement_cargo_" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/cargo/delete/{id}")
    public String deletePaiementCargo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paiementService.deletePaiementCargo(id);
            redirectAttributes.addFlashAttribute("success", "Paiement supprime avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/paiements/cargo";
    }
    
    @GetMapping("/reservation/add")
    public String showAddPaiementReservationForm(Model model) {
        model.addAttribute("paiement", new PaiementReservation());
        model.addAttribute("reservations", reservationService.getAllReservations());
        model.addAttribute("devises", deviseService.getAllDevises());
        model.addAttribute("title", "Paiement Reservation");
        return "paiement/reservation/add";
    }
    
    @PostMapping("/reservation/add")
    public String addPaiementReservation(@Valid @ModelAttribute PaiementReservation paiement,
                                        BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("reservations", reservationService.getAllReservations());
            redirectAttributes.addFlashAttribute("devises", deviseService.getAllDevises());
            return "paiement/reservation/add";
        }
        
        try {
            paiementService.addPaiementReservation(paiement);
            redirectAttributes.addFlashAttribute("success", "Paiement enregistre avec succes");
            return "redirect:/paiements";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("reservations", reservationService.getAllReservations());
            redirectAttributes.addFlashAttribute("devises", deviseService.getAllDevises());
            return "redirect:/paiements/reservation/add";
        }
    }
    
    @GetMapping("/cargo/add")
    public String showAddPaiementCargoForm(Model model) {
        model.addAttribute("paiement", new PaiementCargo());
        model.addAttribute("cargos", cargoService.getAllCargos());
        model.addAttribute("devises", deviseService.getAllDevises());
        model.addAttribute("title", "Paiement Cargo");
        return "paiement/cargo/add";
    }
    
    @PostMapping("/cargo/add")
    public String addPaiementCargo(@Valid @ModelAttribute PaiementCargo paiement,
                                  BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("cargos", cargoService.getAllCargos());
            redirectAttributes.addFlashAttribute("devises", deviseService.getAllDevises());
            return "paiement/cargo/add";
        }
        
        try {
            paiementService.addPaiementCargo(paiement);
            redirectAttributes.addFlashAttribute("success", "Paiement enregistre avec succes");
            return "redirect:/paiements";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("cargos", cargoService.getAllCargos());
            redirectAttributes.addFlashAttribute("devises", deviseService.getAllDevises());
            return "redirect:/paiements/cargo/add";
        }
    }
    
    @PostMapping("/reservation/changer-etat/{id}")
    public String changerEtatPaiementReservation(@PathVariable Long id, @RequestParam Integer codeEtat,
                                                 RedirectAttributes redirectAttributes) {
        try {
            paiementService.changerEtatPaiementReservation(id, codeEtat);
            redirectAttributes.addFlashAttribute("success", "Etat du paiement modifie avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/paiements";
    }
    
    @PostMapping("/cargo/changer-etat/{id}")
    public String changerEtatPaiementCargo(@PathVariable Long id, @RequestParam Integer codeEtat,
                                          RedirectAttributes redirectAttributes) {
        try {
            paiementService.changerEtatPaiementCargo(id, codeEtat);
            redirectAttributes.addFlashAttribute("success", "Etat du paiement modifie avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/paiements";
    }
}

