-- Ajout Produits Extra (CA théorique) : TypeProduit + ProduitExtra

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

-- Seed minimal (optionnel)
INSERT INTO type_produit (nom_type_produit, description)
SELECT 'Repas', 'Repas a bord'
WHERE NOT EXISTS (SELECT 1 FROM type_produit WHERE UPPER(nom_type_produit)='REPAS');

INSERT INTO type_produit (nom_type_produit, description)
SELECT 'Boissons', 'Boissons a bord'
WHERE NOT EXISTS (SELECT 1 FROM type_produit WHERE UPPER(nom_type_produit)='BOISSONS');
