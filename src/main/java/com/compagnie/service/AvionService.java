package com.compagnie.service;

import com.compagnie.model.Avion;
import com.compagnie.model.EtatAvion;
import com.compagnie.model.HistoriqueEtatAvion;
import com.compagnie.repository.AvionRepository;
import com.compagnie.repository.EtatAvionRepository;
import com.compagnie.repository.HistoriqueEtatAvionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AvionService {
    
    @Autowired
    private AvionRepository avionRepository;
    
    @Autowired
    private EtatAvionRepository etatAvionRepository;
    
    @Autowired
    private HistoriqueEtatAvionRepository historiqueEtatAvionRepository;
    
    public List<Avion> getAllAvions() {
        return avionRepository.findAll();
    }
    
    public Optional<Avion> getAvionById(Long id) {
        return avionRepository.findById(id);
    }
    
    public Avion addAvion(Avion avion) {
        if (avionRepository.existsByImmatriculation(avion.getImmatriculation())) {
            throw new IllegalArgumentException("L'immatriculation existe deja");
        }
        if (avion.getNbPlacesEconomique() == null || avion.getNbPlacesEconomique() < 0
                || avion.getNbPlacesPremium() == null || avion.getNbPlacesPremium() < 0
                || avion.getNbPlacesPremiere() == null || avion.getNbPlacesPremiere() < 0) {
            throw new IllegalArgumentException("Les capacites passagers par classe doivent etre positives");
        }

        if (avion.getCapacitePassagers() <= 0 || avion.getCapaciteCargo() <= 0) {
            throw new IllegalArgumentException("Les capacites doivent etre strictement positives");
        }
        return avionRepository.save(avion);
    }
    
    public Avion updateAvion(Long id, Avion avion) {
        Optional<Avion> existing = avionRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Avion introuvable");
        }
        Avion avionToUpdate = existing.get();
        
        if (!avionToUpdate.getImmatriculation().equals(avion.getImmatriculation())) {
            if (avionRepository.existsByImmatriculation(avion.getImmatriculation())) {
                throw new IllegalArgumentException("L'immatriculation existe deja");
            }
        }

        if (avion.getNbPlacesEconomique() == null || avion.getNbPlacesEconomique() < 0
                || avion.getNbPlacesPremium() == null || avion.getNbPlacesPremium() < 0
                || avion.getNbPlacesPremiere() == null || avion.getNbPlacesPremiere() < 0) {
            throw new IllegalArgumentException("Les capacites passagers par classe doivent etre positives");
        }
        
        if (avion.getCapacitePassagers() <= 0 || avion.getCapaciteCargo() <= 0) {
            throw new IllegalArgumentException("Les capacites doivent etre strictement positives");
        }
        
        avionToUpdate.setImmatriculation(avion.getImmatriculation());
        avionToUpdate.setModele(avion.getModele());
        avionToUpdate.setNbPlacesEconomique(avion.getNbPlacesEconomique());
        avionToUpdate.setNbPlacesPremium(avion.getNbPlacesPremium());
        avionToUpdate.setNbPlacesPremiere(avion.getNbPlacesPremiere());
        avionToUpdate.setCapaciteCargo(avion.getCapaciteCargo());
        
        return avionRepository.save(avionToUpdate);
    }
    
    public void deleteAvion(Long id) {
        if (!avionRepository.existsById(id)) {
            throw new IllegalArgumentException("Avion introuvable");
        }
        avionRepository.deleteById(id);
    }
    
    public void changerEtatAvion(Long idAvion, Integer codeEtat) {
        Optional<Avion> avionOpt = avionRepository.findById(idAvion);
        if (avionOpt.isEmpty()) {
            throw new IllegalArgumentException("Avion introuvable");
        }
        
        Optional<EtatAvion> etatOpt = etatAvionRepository.findByCodeEtat(codeEtat);
        if (etatOpt.isEmpty()) {
            throw new IllegalArgumentException("Code etat invalide");
        }
        
        Avion avion = avionOpt.get();
        EtatAvion etat = etatOpt.get();
        
        HistoriqueEtatAvion historique = new HistoriqueEtatAvion(avion, etat);
        historique.setDateChangement(LocalDateTime.now());
        historiqueEtatAvionRepository.save(historique);
        
        avion.getHistoriqueEtats().add(historique);
        avionRepository.save(avion);
    }
    
    public boolean peutEtreAssignerVol(Long idAvion) {
        Optional<Avion> avionOpt = avionRepository.findById(idAvion);
        if (avionOpt.isEmpty()) {
            return false;
        }
        
        Optional<HistoriqueEtatAvion> dernierEtatOpt = 
            historiqueEtatAvionRepository.findLatestByAvionId(idAvion);
        
        if (dernierEtatOpt.isEmpty()) {
            return true;
        }
        
        HistoriqueEtatAvion dernierEtat = dernierEtatOpt.get();
        Integer codeEtat = dernierEtat.getEtatAvion().getCodeEtat();
        
        return codeEtat == 1; // 1 = Disponible
    }
}

