package com.compagnie.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttribute {

    @ModelAttribute("activePage")
    public String activePage(HttpServletRequest request) {
        String uri = request.getRequestURI();

        if (uri.startsWith("/dashboard")) return "dashboard";
        if (uri.startsWith("/avions")) return "avions";
        if (uri.startsWith("/aeroports")) return "aeroports";
        if (uri.startsWith("/vols")) return "vols";
        if (uri.startsWith("/passagers")) return "passagers";
        if (uri.startsWith("/reservations")) return "reservations";
        if (uri.startsWith("/billets")) return "billets";
        if (uri.startsWith("/tarifs")) return "tarifs";
        if (uri.startsWith("/promotions")) return "promotions";
        if (uri.startsWith("/types-produit")) return "types-produit";
        if (uri.startsWith("/produits-extra")) return "produits-extra";
        if (uri.startsWith("/cargo")) return "cargo";
        if (uri.startsWith("/paiements")) return "paiements";
        if (uri.startsWith("/devises")) return "devises";
        if (uri.startsWith("/taux-change")) return "taux-change";

        return "";
    }
}
