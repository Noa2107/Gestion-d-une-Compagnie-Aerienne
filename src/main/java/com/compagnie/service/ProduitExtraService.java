package com.compagnie.service;

import com.compagnie.model.ProduitExtra;
import com.compagnie.model.TypeProduit;
import com.compagnie.repository.ProduitExtraRepository;
import com.compagnie.repository.TypeProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProduitExtraService {

    @Autowired
    private ProduitExtraRepository produitExtraRepository;

    @Autowired
    private TypeProduitRepository typeProduitRepository;

    public List<ProduitExtra> getAllProduitsExtra() {
        return produitExtraRepository.findAll();
    }

    public Optional<ProduitExtra> getProduitExtraById(Long id) {
        return produitExtraRepository.findById(id);
    }

    public ProduitExtra addProduitExtra(ProduitExtra produitExtra) {
        normalizeAndValidate(produitExtra);

        TypeProduit typeProduit = typeProduitRepository.findById(produitExtra.getTypeProduit().getIdTypeProduit())
                .orElseThrow(() -> new IllegalArgumentException("Type produit introuvable"));

        produitExtra.setTypeProduit(typeProduit);
        return produitExtraRepository.save(produitExtra);
    }

    public ProduitExtra updateProduitExtra(Long id, ProduitExtra produitExtra) {
        normalizeAndValidate(produitExtra);

        ProduitExtra toUpdate = produitExtraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produit extra introuvable"));

        TypeProduit typeProduit = typeProduitRepository.findById(produitExtra.getTypeProduit().getIdTypeProduit())
                .orElseThrow(() -> new IllegalArgumentException("Type produit introuvable"));

        toUpdate.setTypeProduit(typeProduit);
        toUpdate.setNomProduit(produitExtra.getNomProduit());
        toUpdate.setPrixUnitaire(produitExtra.getPrixUnitaire());
        toUpdate.setQuantiteMensuelle(produitExtra.getQuantiteMensuelle());
        toUpdate.setMois(produitExtra.getMois());
        toUpdate.setAnnee(produitExtra.getAnnee());

        return produitExtraRepository.save(toUpdate);
    }

    public void deleteProduitExtra(Long id) {
        if (!produitExtraRepository.existsById(id)) {
            throw new IllegalArgumentException("Produit extra introuvable");
        }
        produitExtraRepository.deleteById(id);
    }

    private void normalizeAndValidate(ProduitExtra produitExtra) {
        if (produitExtra.getTypeProduit() == null || produitExtra.getTypeProduit().getIdTypeProduit() == null) {
            throw new IllegalArgumentException("Le type produit est obligatoire");
        }
        if (produitExtra.getNomProduit() == null || produitExtra.getNomProduit().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est obligatoire");
        }
        if (produitExtra.getPrixUnitaire() == null) {
            throw new IllegalArgumentException("Le prix unitaire est obligatoire");
        }
        if (produitExtra.getPrixUnitaire().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix unitaire doit etre positif");
        }
        if (produitExtra.getQuantiteMensuelle() == null) {
            throw new IllegalArgumentException("La quantite mensuelle est obligatoire");
        }
        if (produitExtra.getQuantiteMensuelle() < 0) {
            throw new IllegalArgumentException("La quantite mensuelle doit etre positive");
        }
        if (produitExtra.getMois() == null) {
            throw new IllegalArgumentException("Le mois est obligatoire");
        }
        if (produitExtra.getMois() < 1 || produitExtra.getMois() > 12) {
            throw new IllegalArgumentException("Mois invalide");
        }
        if (produitExtra.getAnnee() == null) {
            throw new IllegalArgumentException("L'annee est obligatoire");
        }
        if (produitExtra.getAnnee() < 1900) {
            throw new IllegalArgumentException("Annee invalide");
        }

        produitExtra.setNomProduit(produitExtra.getNomProduit().trim());
    }
}
