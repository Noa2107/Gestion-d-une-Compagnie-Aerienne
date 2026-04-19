package com.compagnie.service;

import com.compagnie.model.TypeProduit;
import com.compagnie.repository.TypeProduitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TypeProduitService {

    @Autowired
    private TypeProduitRepository typeProduitRepository;

    public List<TypeProduit> getAllTypesProduit() {
        return typeProduitRepository.findAll();
    }

    public Optional<TypeProduit> getTypeProduitById(Long id) {
        return typeProduitRepository.findById(id);
    }

    public TypeProduit addTypeProduit(TypeProduit typeProduit) {
        normalizeAndValidate(typeProduit);

        if (typeProduitRepository.findByNomTypeProduitIgnoreCase(typeProduit.getNomTypeProduit()).isPresent()) {
            throw new IllegalArgumentException("Un type de produit avec ce nom existe deja");
        }

        return typeProduitRepository.save(typeProduit);
    }

    public TypeProduit updateTypeProduit(Long id, TypeProduit typeProduit) {
        normalizeAndValidate(typeProduit);

        TypeProduit toUpdate = typeProduitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Type produit introuvable"));

        Optional<TypeProduit> existingWithName = typeProduitRepository.findByNomTypeProduitIgnoreCase(typeProduit.getNomTypeProduit());
        if (existingWithName.isPresent() && !existingWithName.get().getIdTypeProduit().equals(id)) {
            throw new IllegalArgumentException("Un autre type de produit avec ce nom existe deja");
        }

        toUpdate.setNomTypeProduit(typeProduit.getNomTypeProduit());
        toUpdate.setDescription(typeProduit.getDescription());

        return typeProduitRepository.save(toUpdate);
    }

    public void deleteTypeProduit(Long id) {
        if (!typeProduitRepository.existsById(id)) {
            throw new IllegalArgumentException("Type produit introuvable");
        }
        typeProduitRepository.deleteById(id);
    }

    private void normalizeAndValidate(TypeProduit typeProduit) {
        if (typeProduit.getNomTypeProduit() == null || typeProduit.getNomTypeProduit().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du type de produit est obligatoire");
        }

        typeProduit.setNomTypeProduit(typeProduit.getNomTypeProduit().trim());

        if (typeProduit.getDescription() != null) {
            String desc = typeProduit.getDescription().trim();
            typeProduit.setDescription(desc.isEmpty() ? null : desc);
        }
    }
}
