package com.compagnie.controller;

import com.compagnie.service.PubliciteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/publicites")
public class PubliciteRestController {

    private final PubliciteService publiciteService;

    public PubliciteRestController(PubliciteService publiciteService) {
        this.publiciteService = publiciteService;
    }

    // GET /api/publicites/ca?mois=12&annee=2025
    @GetMapping("/ca")
    public ResponseEntity<BigDecimal> getCaTotal(
            @RequestParam int mois,
            @RequestParam int annee
    ) {
        return ResponseEntity.ok(publiciteService.calculerCaTotal(mois, annee));
    }

    // GET /api/publicites/ca/societe/{id}
    @GetMapping("/ca/societe/{id}")
    public ResponseEntity<BigDecimal> getCaParSociete(@PathVariable("id") Long idSociete) {
        return ResponseEntity.ok(publiciteService.calculerCaParSociete(idSociete));
    }
}
