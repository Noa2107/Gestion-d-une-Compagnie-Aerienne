CREATE TABLE avion (
    id_avion SERIAL PRIMARY KEY,
    immatriculation VARCHAR(50) UNIQUE NOT NULL,
    modele VARCHAR(50),
    capacite_passagers INT,
    capacite_cargo INT
);

CREATE TABLE aeroport (
    id_aeroport SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    nom VARCHAR(100),
    ville VARCHAR(100),
    pays VARCHAR(100)
);

CREATE TABLE vol (
    id_vol SERIAL PRIMARY KEY,
    code_vol VARCHAR(20),
    id_avion INT NOT NULL REFERENCES avion(id_avion),
    id_aeroport_depart INT NOT NULL REFERENCES aeroport(id_aeroport),
    id_aeroport_arrivee INT NOT NULL REFERENCES aeroport(id_aeroport),
    date_vol TIMESTAMP NOT NULL,
    date_arrivee TIMESTAMP,
    type_vol VARCHAR(20) CHECK (type_vol IN ('PASSAGER','CARGO','MIXTE'))
);

CREATE TABLE passager (
    id_passager SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    date_naissance DATE,
    sexe VARCHAR(10),
    nationalite VARCHAR(50),
    numero_passeport VARCHAR(50),
    contact VARCHAR(50),
    email VARCHAR(100)
);

CREATE TABLE reservation (
    id_reservation SERIAL PRIMARY KEY,
    id_passager INT NOT NULL REFERENCES passager(id_passager),
    id_vol INT NOT NULL REFERENCES vol(id_vol),
    date_reservation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    nombre_places INT NOT NULL DEFAULT 1,
    numero_siege VARCHAR(10),
    classe VARCHAR(20),
    bagage_kg INT,
    prix_total NUMERIC(12,2)
);

CREATE TABLE IF NOT EXISTS tarif_vol_classe (
    id_tarif_vol_classe SERIAL PRIMARY KEY,
    id_vol INT NOT NULL REFERENCES vol(id_vol),
    classe VARCHAR(20) NOT NULL,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    UNIQUE(id_vol, classe)
);

-- ================================
-- TABLE PROMOTION TARIF
-- ================================
CREATE TABLE IF NOT EXISTS promotion_tarif (
    id_promotion_tarif SERIAL PRIMARY KEY,
    id_vol INT NOT NULL REFERENCES vol(id_vol),
    classe VARCHAR(20) NOT NULL,
    type_reduction VARCHAR(20) NOT NULL CHECK (type_reduction IN ('POURCENTAGE','MONTANT')),
    valeur NUMERIC(12,2) NOT NULL,
    date_debut TIMESTAMP,
    date_fin TIMESTAMP,
    est_actif BOOLEAN DEFAULT TRUE
);

CREATE TABLE devise (
    id_devise SERIAL PRIMARY KEY,
    code_devise VARCHAR(10) UNIQUE NOT NULL,
    nom_devise VARCHAR(50),
    symbole VARCHAR(10),
    est_reference BOOLEAN DEFAULT FALSE
);

CREATE TABLE taux_change (
    id_taux SERIAL PRIMARY KEY,
    id_devise_source INT NOT NULL REFERENCES devise(id_devise),
    id_devise_cible INT NOT NULL REFERENCES devise(id_devise),
    taux NUMERIC(18,6),
    date_debut_validite TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_fin_validite TIMESTAMP,
    est_actif BOOLEAN DEFAULT TRUE
);

CREATE TABLE paiement_reservation (
    id_paiement_reservation SERIAL PRIMARY KEY,
    id_reservation INT NOT NULL REFERENCES reservation(id_reservation),
    id_devise INT NOT NULL REFERENCES devise(id_devise),
    id_taux INT REFERENCES taux_change(id_taux),
    montant_reference_mga NUMERIC(12,2),
    montant_paye NUMERIC(12,2),
    mode_paiement VARCHAR(20),
    reference_transaction VARCHAR(50),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE IF NOT EXISTS diffusion_pub_vol (
  id_diffusion_pub_vol SERIAL PRIMARY KEY,
  id_vol INT NOT NULL REFERENCES vol(id_vol),
  id_societe INT NOT NULL REFERENCES societe(id_societe),
  mois INT NOT NULL CHECK (mois BETWEEN 1 AND 12),
  annee INT NOT NULL CHECK (annee >= 2000),
  nb_diffusions INT NOT NULL CHECK (nb_diffusions >= 0),
  prix_unitaire NUMERIC(18,2) NOT NULL CHECK (prix_unitaire >= 0)
);

CREATE INDEX IF NOT EXISTS idx_diff_pub_vol_periode ON diffusion_pub_vol (annee, mois);
CREATE INDEX IF NOT EXISTS idx_diff_pub_vol_societe ON diffusion_pub_vol (id_societe);
CREATE INDEX IF NOT EXISTS idx_diff_pub_vol_vol ON diffusion_pub_vol (id_vol);

CREATE TABLE IF NOT EXISTS type_produit (
    id_type_produit BIGSERIAL PRIMARY KEY,
    nom_type_produit VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS produit_extra (
    id_produit_extra BIGSERIAL PRIMARY KEY,
    id_type_produit BIGINT NOT NULL,
    nom_produit VARCHAR(150) NOT NULL,
    prix_unitaire NUMERIC(12,2) NOT NULL,
    quantite_mensuelle INT NOT NULL DEFAULT 0,
    mois INT NOT NULL,
    annee INT NOT NULL
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_produit_extra_type_produit'
    ) THEN
        ALTER TABLE produit_extra
            ADD CONSTRAINT fk_produit_extra_type_produit
                FOREIGN KEY (id_type_produit) REFERENCES type_produit(id_type_produit) ON DELETE RESTRICT;
    END IF;
END $$;

-- Paiements des publicités (par société et période)
CREATE TABLE IF NOT EXISTS publicite_paiement (
  id_publicite_paiement SERIAL PRIMARY KEY,
  id_societe INT NOT NULL REFERENCES societe(id_societe),
  mois INT NOT NULL CHECK (mois BETWEEN 1 AND 12),
  annee INT NOT NULL CHECK (annee >= 2000),
  montant NUMERIC(18,2) NOT NULL CHECK (montant >= 0),
  date_paiement TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  reference VARCHAR(100)
);

-- Index utiles
CREATE INDEX IF NOT EXISTS idx_pub_paie_periode ON publicite_paiement (annee, mois);
CREATE INDEX IF NOT EXISTS idx_pub_paie_societe ON publicite_paiement (id_societe);

CREATE TABLE IF NOT EXISTS societe (
  id_societe SERIAL PRIMARY KEY,
  nom VARCHAR(150) NOT NULL UNIQUE
);

-- Table publicite
CREATE TABLE IF NOT EXISTS publicite (
  id_publicite SERIAL PRIMARY KEY,
  id_societe INT NOT NULL REFERENCES societe(id_societe),
  mois INT NOT NULL CHECK (mois BETWEEN 1 AND 12),
  annee INT NOT NULL,
  nombre_diffusions INT NOT NULL CHECK (nombre_diffusions >= 0),
  prix_unitaire NUMERIC(18,2) NOT NULL
);

-- Index utiles
CREATE INDEX IF NOT EXISTS idx_publicite_periode ON publicite (annee, mois);
CREATE INDEX IF NOT EXISTS idx_publicite_societe ON publicite (id_societe);

CREATE TABLE IF NOT EXISTS reservation_passager (
    id_reservation_passager BIGSERIAL PRIMARY KEY,
    id_reservation BIGINT NOT NULL,
    id_passager BIGINT NOT NULL,
    siege VARCHAR(10) NOT NULL,
    CONSTRAINT fk_rp_reservation FOREIGN KEY (id_reservation) REFERENCES reservation(id_reservation) ON DELETE CASCADE,
    CONSTRAINT fk_rp_passager FOREIGN KEY (id_passager) REFERENCES passager(id_passager) ON DELETE RESTRICT,
    CONSTRAINT uq_rp_reservation_siege UNIQUE (id_reservation, siege)
);

CREATE INDEX IF NOT EXISTS idx_rp_reservation ON reservation_passager(id_reservation);
CREATE INDEX IF NOT EXISTS idx_rp_passager ON reservation_passager(id_passager);

CREATE TABLE IF NOT EXISTS type_passager (
    id_type_passager SERIAL PRIMARY KEY,
    nom_type VARCHAR(50) NOT NULL UNIQUE
);

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

ALTER TABLE IF EXISTS reservation
    ADD COLUMN IF NOT EXISTS sieges_attribues VARCHAR(255);
