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
