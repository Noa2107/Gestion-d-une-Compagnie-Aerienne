package com.compagnie.service;

import com.compagnie.model.Passager;
import com.compagnie.repository.PassagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PassagerService {
    
    @Autowired
    private PassagerRepository passagerRepository;
    
    public List<Passager> getAllPassagers() {
        return passagerRepository.findAll();
    }
    
    public Optional<Passager> getPassagerById(Long id) {
        return passagerRepository.findById(id);
    }
    
    public Passager addPassager(Passager passager) {
        if (passager.getNumeroPasseport() != null && !passager.getNumeroPasseport().trim().isEmpty()) {
            if (passagerRepository.existsByNumeroPasseport(passager.getNumeroPasseport())) {
                throw new IllegalArgumentException("Le numero de passeport existe deja");
            }
        }
        return passagerRepository.save(passager);
    }
    
    public Passager updatePassager(Long id, Passager passager) {
        Optional<Passager> existing = passagerRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Passager introuvable");
        }
        
        Passager passagerToUpdate = existing.get();
        
        if (passager.getNumeroPasseport() != null && !passager.getNumeroPasseport().trim().isEmpty()) {
            if (!passagerToUpdate.getNumeroPasseport().equals(passager.getNumeroPasseport())) {
                if (passagerRepository.existsByNumeroPasseport(passager.getNumeroPasseport())) {
                    throw new IllegalArgumentException("Le numero de passeport existe deja");
                }
            }
        }
        
        passagerToUpdate.setNom(passager.getNom());
        passagerToUpdate.setPrenom(passager.getPrenom());
        passagerToUpdate.setDateNaissance(passager.getDateNaissance());
        passagerToUpdate.setSexe(passager.getSexe());
        passagerToUpdate.setNationalite(passager.getNationalite());
        passagerToUpdate.setNumeroPasseport(passager.getNumeroPasseport());
        passagerToUpdate.setContact(passager.getContact());
        passagerToUpdate.setEmail(passager.getEmail());
        
        return passagerRepository.save(passagerToUpdate);
    }
    
    public void deletePassager(Long id) {
        if (!passagerRepository.existsById(id)) {
            throw new IllegalArgumentException("Passager introuvable");
        }
        passagerRepository.deleteById(id);
    }
}

