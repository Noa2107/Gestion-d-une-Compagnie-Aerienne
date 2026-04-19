-- Désactive temporairement les triggers pour éviter les problèmes de FK
-- (optionnel, souvent utile si tu as beaucoup de FK)
-- ALTER TABLE nom_table DISABLE TRIGGER ALL;

-- Vider toutes les tables dans le bon ordre
TRUNCATE TABLE 
    historique_etat_paiement_cargo,
    historique_etat_paiement_reservation,
    paiement_cargo,
    paiement_reservation,
    historique_etat_reservation,
    historique_etat_vol,
    historique_etat_avion,
    historique_etat_cargo,
    promotion_tarif,
    tarif_vol_classe,
    reservation,
    passager,
    vol,
    avion,
    etat_avion,
    etat_vol,
    etat_reservation,
    etat_paiement,
    cargo,
    aeroport,
    devise,
    taux_change
    CASCADE;

-- Réactive les triggers si désactivés
-- ALTER TABLE nom_table ENABLE TRIGGER ALL;
