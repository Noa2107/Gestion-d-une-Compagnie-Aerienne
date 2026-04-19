-- Table societe
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

-- Sociétés
INSERT INTO societe (nom) VALUES ('Vaniala') ON CONFLICT DO NOTHING;
INSERT INTO societe (nom) VALUES ('Lewis') ON CONFLICT DO NOTHING;

-- Récupérer les IDs
-- Suppose: SELECT id_societe FROM societe WHERE nom='Vania';
-- Suppose: SELECT id_societe FROM societe WHERE nom='Lewis';

-- Publicités Décembre 2025
-- Vania: 20 diffusions
INSERT INTO publicite (id_societe, mois, annee, nombre_diffusions, prix_unitaire)
SELECT id_societe, 12, 2025, 20, 400000.00 FROM societe WHERE nom='Vaniala';

-- Lewis: 10 diffusions
INSERT INTO publicite (id_societe, mois, annee, nombre_diffusions, prix_unitaire)
SELECT id_societe, 12, 2025, 10, 400000.00 FROM societe WHERE nom='Lewis';