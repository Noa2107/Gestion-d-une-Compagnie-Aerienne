package com.compagnie.service;

import com.compagnie.model.Devise;
import com.compagnie.repository.DeviseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DeviseService {
    
    @Autowired
    private DeviseRepository deviseRepository;
    
    public List<Devise> getAllDevises() {
        return deviseRepository.findAll();
    }
    
    public Optional<Devise> getDeviseById(Long id) {
        return deviseRepository.findById(id);
    }
    
    public Devise addDevise(Devise devise) {
        if (deviseRepository.existsByCodeDevise(devise.getCodeDevise())) {
            throw new IllegalArgumentException("Le code devise existe deja");
        }
        
        if (devise.getEstReference() != null && devise.getEstReference()) {
            Optional<Devise> deviseRefOpt = deviseRepository.findByEstReferenceTrue();
            if (deviseRefOpt.isPresent() && !deviseRefOpt.get().getIdDevise().equals(devise.getIdDevise())) {
                throw new IllegalArgumentException("Une devise de reference existe deja (MGA)");
            }
        }
        
        return deviseRepository.save(devise);
    }
    
    public Devise updateDevise(Long id, Devise devise) {
        Optional<Devise> existing = deviseRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Devise introuvable");
        }
        
        Devise deviseToUpdate = existing.get();
        
        if (!deviseToUpdate.getCodeDevise().equals(devise.getCodeDevise())) {
            if (deviseRepository.existsByCodeDevise(devise.getCodeDevise())) {
                throw new IllegalArgumentException("Le code devise existe deja");
            }
        }
        
        if (devise.getEstReference() != null && devise.getEstReference()) {
            Optional<Devise> deviseRefOpt = deviseRepository.findByEstReferenceTrue();
            if (deviseRefOpt.isPresent() && !deviseRefOpt.get().getIdDevise().equals(id)) {
                throw new IllegalArgumentException("Une devise de reference existe deja (MGA)");
            }
        }
        
        deviseToUpdate.setCodeDevise(devise.getCodeDevise());
        deviseToUpdate.setNomDevise(devise.getNomDevise());
        deviseToUpdate.setSymbole(devise.getSymbole());
        deviseToUpdate.setEstReference(devise.getEstReference());
        
        return deviseRepository.save(deviseToUpdate);
    }
}

