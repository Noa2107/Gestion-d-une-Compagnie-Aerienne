-- ================================
-- BASE DE DONNÉES : COMPAGNIE AÉRIENNE
-- PostgreSQL
-- ================================

-- ================================
-- TABLE AVION
-- ================================
CREATE TABLE avion (
    id_avion SERIAL PRIMARY KEY,
    immatriculation VARCHAR(50) UNIQUE NOT NULL,
    modele VARCHAR(50),
    capacite_passagers INT,
    capacite_cargo INT
);

CREATE TABLE etat_avion (
    id_etat_avion SERIAL PRIMARY KEY,
    code_etat INT UNIQUE NOT NULL,
    description VARCHAR(100) NOT NULL
);

CREATE TABLE historique_etat_avion (
    id_historique SERIAL PRIMARY KEY,
    id_avion INT NOT NULL REFERENCES avion(id_avion),
    id_etat_avion INT NOT NULL REFERENCES etat_avion(id_etat_avion),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- TABLE AEROPORT
-- ================================
CREATE TABLE aeroport (
    id_aeroport SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    nom VARCHAR(100),
    ville VARCHAR(100),
    pays VARCHAR(100)
);

-- ================================
-- TABLE VOL
-- ================================
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

CREATE TABLE etat_vol (
    id_etat_vol SERIAL PRIMARY KEY,
    code_etat INT UNIQUE NOT NULL,
    description VARCHAR(100) NOT NULL
);

CREATE TABLE historique_etat_vol (
    id_historique SERIAL PRIMARY KEY,
    id_vol INT NOT NULL REFERENCES vol(id_vol),
    id_etat_vol INT NOT NULL REFERENCES etat_vol(id_etat_vol),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- TABLE PASSAGER
-- ================================
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

-- ================================
-- TABLE RESERVATION
-- ================================
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

-- ================================
-- TABLE TARIF VOL CLASSE
-- ================================
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

CREATE TABLE etat_reservation (
    id_etat_reservation SERIAL PRIMARY KEY,
    code_etat INT UNIQUE NOT NULL,
    description VARCHAR(100) NOT NULL
);

CREATE TABLE historique_etat_reservation (
    id_historique SERIAL PRIMARY KEY,
    id_reservation INT NOT NULL REFERENCES reservation(id_reservation),
    id_etat_reservation INT NOT NULL REFERENCES etat_reservation(id_etat_reservation),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- TABLE CARGO
-- ================================
CREATE TABLE cargo (
    id_cargo SERIAL PRIMARY KEY,
    reference VARCHAR(50),
    poids INT NOT NULL,
    type_marchandise VARCHAR(50),
    expediteur VARCHAR(100),
    destinataire VARCHAR(100),
    adresse_depart VARCHAR(150),
    adresse_arrivee VARCHAR(150),
    valeur_declaree NUMERIC(12,2)
);

CREATE TABLE etat_cargo (
    id_etat_cargo SERIAL PRIMARY KEY,
    code_etat INT UNIQUE NOT NULL,
    description VARCHAR(100) NOT NULL
);

CREATE TABLE historique_etat_cargo (
    id_historique SERIAL PRIMARY KEY,
    id_cargo INT NOT NULL REFERENCES cargo(id_cargo),
    id_etat_cargo INT NOT NULL REFERENCES etat_cargo(id_etat_cargo),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE chargement_cargo (
    id_chargement SERIAL PRIMARY KEY,
    id_vol INT NOT NULL REFERENCES vol(id_vol),
    id_cargo INT NOT NULL REFERENCES cargo(id_cargo)
);

-- ================================
-- TABLE DEVISE
-- ================================
CREATE TABLE devise (
    id_devise SERIAL PRIMARY KEY,
    code_devise VARCHAR(10) UNIQUE NOT NULL,
    nom_devise VARCHAR(50),
    symbole VARCHAR(10),
    est_reference BOOLEAN DEFAULT FALSE
);

-- ================================
-- TABLE TAUX DE CHANGE
-- ================================
CREATE TABLE taux_change (
    id_taux SERIAL PRIMARY KEY,
    id_devise_source INT NOT NULL REFERENCES devise(id_devise),
    id_devise_cible INT NOT NULL REFERENCES devise(id_devise),
    taux NUMERIC(18,6),
    date_debut_validite TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    date_fin_validite TIMESTAMP,
    est_actif BOOLEAN DEFAULT TRUE
);

-- ================================
-- TABLE PAIEMENT RESERVATION
-- ================================
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

-- ================================
-- TABLE PAIEMENT CARGO
-- ================================
CREATE TABLE paiement_cargo (
    id_paiement_cargo SERIAL PRIMARY KEY,
    id_cargo INT NOT NULL REFERENCES cargo(id_cargo),
    id_devise INT NOT NULL REFERENCES devise(id_devise),
    id_taux INT REFERENCES taux_change(id_taux),
    montant_reference_mga NUMERIC(12,2),
    montant_paye NUMERIC(12,2),
    mode_paiement VARCHAR(20),
    type_facturation VARCHAR(20),
    reference_facture VARCHAR(50),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- TABLE ETAT PAIEMENT
-- ================================
CREATE TABLE etat_paiement (
    id_etat_paiement SERIAL PRIMARY KEY,
    code_etat INT UNIQUE NOT NULL,
    description VARCHAR(100) NOT NULL
);

CREATE TABLE historique_etat_paiement_reservation (
    id_historique SERIAL PRIMARY KEY,
    id_paiement_reservation INT NOT NULL REFERENCES paiement_reservation(id_paiement_reservation),
    id_etat_paiement INT NOT NULL REFERENCES etat_paiement(id_etat_paiement),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE historique_etat_paiement_cargo (
    id_historique SERIAL PRIMARY KEY,
    id_paiement_cargo INT NOT NULL REFERENCES paiement_cargo(id_paiement_cargo),
    id_etat_paiement INT NOT NULL REFERENCES etat_paiement(id_etat_paiement),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
