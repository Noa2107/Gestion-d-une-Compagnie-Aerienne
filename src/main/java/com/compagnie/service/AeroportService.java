package com.compagnie.service;

import com.compagnie.model.Aeroport;
import com.compagnie.repository.AeroportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AeroportService {
    
    @Autowired
    private AeroportRepository aeroportRepository;
    
    public List<Aeroport> getAllAeroports() {
        return aeroportRepository.findAll();
    }
    
    public Optional<Aeroport> getAeroportById(Long id) {
        return aeroportRepository.findById(id);
    }
    
    public Aeroport addAeroport(Aeroport aeroport) {
        if (aeroportRepository.existsByCode(aeroport.getCode())) {
            throw new IllegalArgumentException("Le code aeroport existe deja");
        }
        if (aeroport.getVille() == null || aeroport.getVille().trim().isEmpty()) {
            throw new IllegalArgumentException("La ville est obligatoire");
        }
        if (aeroport.getPays() == null || aeroport.getPays().trim().isEmpty()) {
            throw new IllegalArgumentException("Le pays est obligatoire");
        }
        return aeroportRepository.save(aeroport);
    }
    
    public Aeroport updateAeroport(Long id, Aeroport aeroport) {
        Optional<Aeroport> existing = aeroportRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Aeroport introuvable");
        }
        Aeroport aeroportToUpdate = existing.get();
        
        if (!aeroportToUpdate.getCode().equals(aeroport.getCode())) {
            if (aeroportRepository.existsByCode(aeroport.getCode())) {
                throw new IllegalArgumentException("Le code aeroport existe deja");
            }
        }
        
        if (aeroport.getVille() == null || aeroport.getVille().trim().isEmpty()) {
            throw new IllegalArgumentException("La ville est obligatoire");
        }
        if (aeroport.getPays() == null || aeroport.getPays().trim().isEmpty()) {
            throw new IllegalArgumentException("Le pays est obligatoire");
        }
        
        aeroportToUpdate.setCode(aeroport.getCode());
        aeroportToUpdate.setNom(aeroport.getNom());
        aeroportToUpdate.setVille(aeroport.getVille());
        aeroportToUpdate.setPays(aeroport.getPays());
        
        return aeroportRepository.save(aeroportToUpdate);
    }
    
    public void deleteAeroport(Long id) {
        if (!aeroportRepository.existsById(id)) {
            throw new IllegalArgumentException("Aeroport introuvable");
        }
        aeroportRepository.deleteById(id);
    }
}

