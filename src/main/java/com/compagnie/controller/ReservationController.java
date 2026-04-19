package com.compagnie.controller;

import com.compagnie.model.Reservation;
import com.compagnie.model.ReservationPassager;
import com.compagnie.service.PdfExportService;
import com.compagnie.service.ReservationService;
import com.compagnie.service.PassagerService;
import com.compagnie.service.ReservationPassagerService;
import com.compagnie.service.TarificationService;
import com.compagnie.service.VolService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reservations")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private PassagerService passagerService;
    
    @Autowired
    private VolService volService;

    @Autowired
    private TarificationService tarificationService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private ReservationPassagerService reservationPassagerService;
    
    @GetMapping
    public String listReservations(@RequestParam(required = false) Long idVol,
                                  @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeureVol,
                                  Model model) {
        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);

        model.addAttribute("vols", volService.getAllVols());
        model.addAttribute("selectedVolId", idVol);
        model.addAttribute("selectedDateHeureVol", dateHeureVol);

        if (idVol != null && dateHeureVol != null) {
            try {
                BigDecimal chiffreAffaire = reservationService.getChiffreAffaireParVolEtDateHeure(idVol, dateHeureVol);
                model.addAttribute("chiffreAffaire", chiffreAffaire);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        model.addAttribute("title", "Liste des Reservations");
        return "reservation/list";
    }
    
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("reservation", new Reservation());
        model.addAttribute("passagers", passagerService.getAllPassagers());
        model.addAttribute("vols", volService.getAllVols());
        model.addAttribute("title", "Creer une Reservation");
        return "reservation/add";
    }
    
    @PostMapping("/add")
    public String createReservation(@Valid @ModelAttribute Reservation reservation, BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("passagers", passagerService.getAllPassagers());
            redirectAttributes.addFlashAttribute("vols", volService.getAllVols());
            return "reservation/add";
        }
        
        try {
            reservationService.createReservation(reservation);
            redirectAttributes.addFlashAttribute("success", "Reservation creee avec succes");
            return "redirect:/reservations";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("passagers", passagerService.getAllPassagers());
            redirectAttributes.addFlashAttribute("vols", volService.getAllVols());
            return "redirect:/reservations/add";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return reservationService.getReservationById(id)
            .map(reservation -> {
                model.addAttribute("reservation", reservation);
                model.addAttribute("passagers", passagerService.getAllPassagers());
                model.addAttribute("vols", volService.getAllVols());
                model.addAttribute("title", "Modifier une Reservation");
                return "reservation/edit";
            })
            .orElse("redirect:/reservations");
    }

    @GetMapping("/details/{id}")
    public String detailsReservation(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return reservationService.getReservationById(id)
                .map(reservation -> {
                    model.addAttribute("reservation", reservation);
                    String pnr = "AIR" + String.format("%06d", reservation.getIdReservation() != null ? reservation.getIdReservation() : 0L);
                    model.addAttribute("pnr", pnr);

                    BigDecimal prixParBillet = reservation.getPrixTotal();
                    if (reservation.getPrixTotal() != null
                            && reservation.getNombrePlaces() != null
                            && reservation.getNombrePlaces() > 1) {
                        prixParBillet = reservation.getPrixTotal()
                                .divide(BigDecimal.valueOf(reservation.getNombrePlaces()), 2, RoundingMode.HALF_UP);
                    }
                    model.addAttribute("prixParBillet", prixParBillet);

                    List<String> seatList = new ArrayList<>();
                    if (reservation.getNombrePlaces() != null && reservation.getNombrePlaces() == 1) {
                        if (reservation.getNumeroSiege() != null && !reservation.getNumeroSiege().isBlank()) {
                            seatList.add(reservation.getNumeroSiege().trim());
                        }
                    } else {
                        if (reservation.getSiegesAttribues() != null && !reservation.getSiegesAttribues().isBlank()) {
                            seatList = List.of(reservation.getSiegesAttribues().split(","))
                                    .stream()
                                    .map(String::trim)
                                    .filter(s -> !s.isBlank())
                                    .collect(Collectors.toList());
                        }
                    }
                    model.addAttribute("seatList", seatList);

                    Map<String, ReservationPassager> seatAssignments = new HashMap<>();
                    if (reservation.getIdReservation() != null) {
                        List<ReservationPassager> rps = reservationPassagerService.getByReservation(reservation.getIdReservation());
                        for (ReservationPassager rp : rps) {
                            if (rp == null || rp.getSiege() == null) continue;
                            seatAssignments.put(rp.getSiege().trim().toUpperCase(Locale.ROOT), rp);
                        }
                    }
                    model.addAttribute("seatAssignments", seatAssignments);
                    model.addAttribute("passagers", passagerService.getAllPassagers());

                    model.addAttribute("title", "Details Reservation");
                    return "reservation/details";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Reservation introuvable");
                    return "redirect:/reservations";
                });
    }

    @PostMapping("/details/{id}/assign-passager")
    public String assignPassagerToSeat(@PathVariable Long id,
                                       @RequestParam Long idPassager,
                                       @RequestParam String siege,
                                       RedirectAttributes redirectAttributes) {
        try {
            Reservation reservation = reservationService.getReservationById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Reservation introuvable"));

            List<String> allowedSeats = new ArrayList<>();
            if (reservation.getNombrePlaces() != null && reservation.getNombrePlaces() == 1) {
                if (reservation.getNumeroSiege() != null && !reservation.getNumeroSiege().isBlank()) {
                    allowedSeats.add(reservation.getNumeroSiege().trim());
                }
            } else {
                if (reservation.getSiegesAttribues() != null && !reservation.getSiegesAttribues().isBlank()) {
                    allowedSeats = List.of(reservation.getSiegesAttribues().split(","))
                            .stream()
                            .map(String::trim)
                            .filter(s -> !s.isBlank())
                            .collect(Collectors.toList());
                }
            }

            reservationPassagerService.assignPassagerToSeat(id, idPassager, siege, allowedSeats);
            redirectAttributes.addFlashAttribute("success", "Passager affecte au siege " + siege);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/reservations/details/" + id;
    }

    @GetMapping("/details/{id}/pdf")
    public ResponseEntity<byte[]> exportReservationPdf(@PathVariable Long id) {
        Reservation reservation = reservationService.getReservationById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reservation introuvable"));

        byte[] pdf = pdfExportService.exportReservationTicket(reservation);
        String filename = "billet_airline_reservation_" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
    
    @PostMapping("/edit/{id}")
    public String updateReservation(@PathVariable Long id, @Valid @ModelAttribute Reservation reservation,
                                   BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "reservation/edit";
        }
        
        try {
            reservationService.updateReservation(id, reservation);
            redirectAttributes.addFlashAttribute("success", "Reservation modifiee avec succes");
            return "redirect:/reservations";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/reservations/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservationService.deleteReservation(id);
            redirectAttributes.addFlashAttribute("success", "Reservation supprimee avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservations";
    }
    
    @PostMapping("/changer-etat/{id}")
    public String changerEtat(@PathVariable Long id, @RequestParam Integer codeEtat,
                             RedirectAttributes redirectAttributes) {
        try {
            reservationService.changerEtatReservation(id, codeEtat);
            redirectAttributes.addFlashAttribute("success", "Etat de la reservation modifie avec succes");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/reservations";
    }

    @GetMapping("/prix")
    @ResponseBody
    public Map<String, Object> getPrix(@RequestParam Long idVol,
                                       @RequestParam String classe,
                                       @RequestParam(required = false) Integer nombrePlaces,
                                       @RequestParam(required = false) Integer nbAdultes,
                                       @RequestParam(required = false) Integer nbEnfants,
                                       @RequestParam(required = false) Integer nbBebes) {
        Map<String, Object> response = new HashMap<>();
        try {
            BigDecimal prixTotal;
            if (nbAdultes != null || nbEnfants != null || nbBebes != null) {
                prixTotal = tarificationService.calculerPrixTotalMix(idVol, classe, nbAdultes, nbEnfants, nbBebes, LocalDateTime.now());
            } else {
                prixTotal = tarificationService.calculerPrixTotal(idVol, classe, nombrePlaces, LocalDateTime.now());
            }
            response.put("prixTotal", prixTotal);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
        }
        return response;
    }
}
