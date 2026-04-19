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
