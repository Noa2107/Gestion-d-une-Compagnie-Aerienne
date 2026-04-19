-- Ajout Type Passager (Adulte / Enfant) + adaptation Tarifs/Promotions/Reservations

CREATE TABLE IF NOT EXISTS type_passager (
    id_type_passager SERIAL PRIMARY KEY,
    nom_type VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO type_passager (nom_type)
SELECT 'Adulte'
WHERE NOT EXISTS (SELECT 1 FROM type_passager WHERE UPPER(nom_type) = 'ADULTE');

INSERT INTO type_passager (nom_type)
SELECT 'Enfant'
WHERE NOT EXISTS (SELECT 1 FROM type_passager WHERE UPPER(nom_type) = 'ENFANT');

INSERT INTO type_passager (nom_type)
SELECT 'Bebe'
WHERE NOT EXISTS (SELECT 1 FROM type_passager WHERE UPPER(nom_type) = 'BEBE');

-- Détail du nombre de passagers par type dans une réservation (mix Adulte/Enfant)
CREATE TABLE IF NOT EXISTS reservation_type_passager (
    id_reservation BIGINT NOT NULL,
    id_type_passager INT NOT NULL,
    nombre INT NOT NULL DEFAULT 0,
    PRIMARY KEY (id_reservation, id_type_passager),
    CONSTRAINT fk_rtp_reservation FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation) ON DELETE CASCADE,
    CONSTRAINT fk_rtp_type_passager FOREIGN KEY (id_type_passager) REFERENCES type_passager(id_type_passager) ON DELETE RESTRICT
);

-- Tarifs: désormais dépend aussi du type passager
ALTER TABLE IF EXISTS tarif_vol_classe
    ADD COLUMN IF NOT EXISTS id_type_passager INT;

-- Backfill: anciennes lignes de tarif sans type -> Adulte
UPDATE tarif_vol_classe
SET id_type_passager = (SELECT id_type_passager FROM type_passager WHERE UPPER(nom_type) = 'ADULTE')
WHERE id_type_passager IS NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_tarif_type_passager'
    ) THEN
        ALTER TABLE tarif_vol_classe
            ADD CONSTRAINT fk_tarif_type_passager
                FOREIGN KEY (id_type_passager) REFERENCES type_passager(id_type_passager) ON DELETE RESTRICT;
    END IF;
END $$;

-- Remplacer l'unicité (id_vol, classe) par (id_vol, classe, id_type_passager)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'tarif_vol_classe_id_vol_classe_key'
    ) THEN
        ALTER TABLE tarif_vol_classe
            DROP CONSTRAINT tarif_vol_classe_id_vol_classe_key;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_tarif_vol_classe_vol_classe_type'
    ) THEN
        ALTER TABLE tarif_vol_classe
            ADD CONSTRAINT uk_tarif_vol_classe_vol_classe_type
                UNIQUE (id_vol, classe, id_type_passager);
    END IF;
END $$;

-- Promotions: désormais dépend aussi du type passager
ALTER TABLE IF EXISTS promotion_tarif
    ADD COLUMN IF NOT EXISTS id_type_passager INT;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_promo_type_passager'
    ) THEN
        ALTER TABLE promotion_tarif
            ADD CONSTRAINT fk_promo_type_passager
                FOREIGN KEY (id_type_passager) REFERENCES type_passager(id_type_passager) ON DELETE RESTRICT;
    END IF;
END $$;
