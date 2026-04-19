-- Ajout gestion des sieges par classe (Economie / Premiere) pour Avion

ALTER TABLE IF EXISTS avion
    ADD COLUMN IF NOT EXISTS nb_places_economique INTEGER NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS avion
    ADD COLUMN IF NOT EXISTS nb_places_premiere INTEGER NOT NULL DEFAULT 0;

ALTER TABLE IF EXISTS avion
    ADD COLUMN IF NOT EXISTS nb_places_premium INTEGER NOT NULL DEFAULT 0;

UPDATE avion
SET nb_places_economique = COALESCE(nb_places_economique, capacite_passagers, 0),
    nb_places_premium = COALESCE(nb_places_premium, 0),
    nb_places_premiere = COALESCE(nb_places_premiere, 0)
WHERE nb_places_economique = 0 AND nb_places_premium = 0 AND nb_places_premiere = 0;

UPDATE avion
SET capacite_passagers = COALESCE(nb_places_economique, 0) + COALESCE(nb_places_premium, 0) + COALESCE(nb_places_premiere, 0)
WHERE capacite_passagers IS NULL
   OR capacite_passagers <> (COALESCE(nb_places_economique, 0) + COALESCE(nb_places_premium, 0) + COALESCE(nb_places_premiere, 0));
