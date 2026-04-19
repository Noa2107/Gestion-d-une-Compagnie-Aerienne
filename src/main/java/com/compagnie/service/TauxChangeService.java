package com.compagnie.service;

import com.compagnie.model.TauxChange;
import com.compagnie.repository.TauxChangeRepository;
import com.compagnie.repository.DeviseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TauxChangeService {
    
    @Autowired
    private TauxChangeRepository tauxChangeRepository;
    
    @Autowired
    private DeviseRepository deviseRepository;
    
    public List<TauxChange> getTauxActifs() {
        return tauxChangeRepository.findAllActifs();
    }
    
    public Optional<TauxChange> getDernierTaux(Long idDevise) {
        return tauxChangeRepository.findDernierTauxActifByDevise(idDevise);
    }
    
    public TauxChange addTaux(TauxChange tauxChange) {
        if (tauxChange.getTaux().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le taux doit etre strictement positif");
        }
        
        if (tauxChange.getDeviseSource() == null || tauxChange.getDeviseSource().getIdDevise() == null) {
            throw new IllegalArgumentException("La devise source est obligatoire");
        }
        
        if (tauxChange.getDeviseCible() == null || tauxChange.getDeviseCible().getIdDevise() == null) {
            throw new IllegalArgumentException("La devise cible est obligatoire");
        }
        
        if (tauxChange.getEstActif() == null || tauxChange.getEstActif()) {
            List<TauxChange> tauxActifs = tauxChangeRepository.findAllActifs();
            for (TauxChange taux : tauxActifs) {
                if (taux.getDeviseSource().getIdDevise().equals(tauxChange.getDeviseSource().getIdDevise())) {
                    taux.setEstActif(false);
                    taux.setDateFinValidite(LocalDateTime.now());
                    tauxChangeRepository.save(taux);
                }
            }
        }
        
        if (tauxChange.getDateDebutValidite() == null) {
            tauxChange.setDateDebutValidite(LocalDateTime.now());
        }
        
        return tauxChangeRepository.save(tauxChange);
    }
}

