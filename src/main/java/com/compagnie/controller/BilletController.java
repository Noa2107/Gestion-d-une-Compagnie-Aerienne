package com.compagnie.controller;

import com.compagnie.model.Passager;
import com.compagnie.model.Reservation;
import com.compagnie.model.Vol;
import com.compagnie.service.PassagerService;
import com.compagnie.service.ReservationService;
import com.compagnie.service.TarificationService;
import com.compagnie.service.VolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/billets")
public class BilletController {

    @Autowired
    private VolService volService;

    @Autowired
    private PassagerService passagerService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private TarificationService tarificationService;

    @GetMapping
    public String searchForm(Model model) {
        model.addAttribute("passagers", passagerService.getAllPassagers());
        model.addAttribute("title", "Billets");
        return "billet/search";
    }

    @PostMapping("/search")
    public String search(@RequestParam String codeDepart,
                         @RequestParam String codeArrivee,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeure,
                         @RequestParam Integer nombrePlaces,
                         @RequestParam String classe,
                         @RequestParam(required = false) Long idPassager,
                         Model model) {

        List<Vol> vols = volService.rechercherVolsParCodeEtDateHeure(codeDepart, codeArrivee, dateHeure);

        Map<Long, Integer> placesDisponibles = new HashMap<>();
        Map<Long, BigDecimal> prixTotals = new HashMap<>();
        for (Vol v : vols) {
            placesDisponibles.put(v.getIdVol(), volService.getPlacesDisponibles(v));
            prixTotals.put(v.getIdVol(), tarificationService.calculerPrixTotal(v.getIdVol(), classe, nombrePlaces, LocalDateTime.now()));
        }

        model.addAttribute("passagers", passagerService.getAllPassagers());
        model.addAttribute("vols", vols);
        model.addAttribute("placesDisponibles", placesDisponibles);
        model.addAttribute("prixTotals", prixTotals);

        model.addAttribute("codeDepart", codeDepart);
        model.addAttribute("codeArrivee", codeArrivee);
        model.addAttribute("dateHeure", dateHeure);
        model.addAttribute("nombrePlaces", nombrePlaces);
        model.addAttribute("classe", classe);
        model.addAttribute("idPassager", idPassager);

        model.addAttribute("title", "Billets");
        return "billet/search";
    }

    @PostMapping("/acheter")
    public String acheter(@RequestParam Long idVol,
                          @RequestParam Long idPassager,
                          @RequestParam Integer nombrePlaces,
                          @RequestParam String classe,
                          RedirectAttributes redirectAttributes) {
        try {
            Reservation reservation = new Reservation();
            Passager p = new Passager();
            p.setIdPassager(idPassager);
            reservation.setPassager(p);

            Vol v = new Vol();
            v.setIdVol(idVol);
            reservation.setVol(v);

            reservation.setNombrePlaces(nombrePlaces);
            reservation.setClasse(classe);

            reservationService.createReservation(reservation);
            redirectAttributes.addFlashAttribute("success", "Achat effectue: reservation creee avec succes");
            return "redirect:/reservations";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/billets";
        }
    }
}
