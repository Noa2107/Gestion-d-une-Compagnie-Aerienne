package com.compagnie.service;

import com.compagnie.model.*;
import com.compagnie.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CargoService {
    
    @Autowired
    private CargoRepository cargoRepository;
    
    @Autowired
    private EtatCargoRepository etatCargoRepository;
    
    @Autowired
    private HistoriqueEtatCargoRepository historiqueEtatCargoRepository;
    
    @Autowired
    private ChargementCargoRepository chargementCargoRepository;
    
    @Autowired
    private VolRepository volRepository;
    
    public List<Cargo> getAllCargos() {
        return cargoRepository.findAll();
    }
    
    public Optional<Cargo> getCargoById(Long id) {
        return cargoRepository.findById(id);
    }
    
    public Cargo addCargo(Cargo cargo) {
        if (cargo.getReference() != null && !cargo.getReference().trim().isEmpty()) {
            if (cargoRepository.existsByReference(cargo.getReference())) {
                throw new IllegalArgumentException("La reference existe deja");
            }
        }
        
        if (cargo.getPoids() <= 0) {
            throw new IllegalArgumentException("Le poids doit etre strictement positif");
        }
        
        return cargoRepository.save(cargo);
    }
    
    public Cargo updateCargo(Long id, Cargo cargo) {
        Optional<Cargo> existing = cargoRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Cargo introuvable");
        }
        
        Cargo cargoToUpdate = existing.get();
        
        if (cargo.getReference() != null && !cargo.getReference().trim().isEmpty()) {
            if (!cargoToUpdate.getReference().equals(cargo.getReference())) {
                if (cargoRepository.existsByReference(cargo.getReference())) {
                    throw new IllegalArgumentException("La reference existe deja");
                }
            }
        }
        
        if (cargo.getPoids() <= 0) {
            throw new IllegalArgumentException("Le poids doit etre strictement positif");
        }
        
        cargoToUpdate.setReference(cargo.getReference());
        cargoToUpdate.setPoids(cargo.getPoids());
        cargoToUpdate.setTypeMarchandise(cargo.getTypeMarchandise());
        cargoToUpdate.setExpediteur(cargo.getExpediteur());
        cargoToUpdate.setDestinataire(cargo.getDestinataire());
        cargoToUpdate.setAdresseDepart(cargo.getAdresseDepart());
        cargoToUpdate.setAdresseArrivee(cargo.getAdresseArrivee());
        cargoToUpdate.setValeurDeclaree(cargo.getValeurDeclaree());
        
        return cargoRepository.save(cargoToUpdate);
    }
    
    public void changerEtatCargo(Long idCargo, Integer codeEtat) {
        Optional<Cargo> cargoOpt = cargoRepository.findById(idCargo);
        if (cargoOpt.isEmpty()) {
            throw new IllegalArgumentException("Cargo introuvable");
        }
        
        Optional<EtatCargo> etatOpt = etatCargoRepository.findByCodeEtat(codeEtat);
        if (etatOpt.isEmpty()) {
            throw new IllegalArgumentException("Code etat invalide");
        }
        
        Cargo cargo = cargoOpt.get();
        EtatCargo etat = etatOpt.get();
        
        HistoriqueEtatCargo historique = new HistoriqueEtatCargo(cargo, etat);
        historique.setDateChangement(LocalDateTime.now());
        historiqueEtatCargoRepository.save(historique);
        
        cargo.getHistoriqueEtats().add(historique);
        cargoRepository.save(cargo);
    }
    
    public void chargerCargo(Long idCargo, Long idVol) {
        Optional<Cargo> cargoOpt = cargoRepository.findById(idCargo);
        if (cargoOpt.isEmpty()) {
            throw new IllegalArgumentException("Cargo introuvable");
        }
        
        Optional<Vol> volOpt = volRepository.findById(idVol);
        if (volOpt.isEmpty()) {
            throw new IllegalArgumentException("Vol introuvable");
        }
        
        Cargo cargo = cargoOpt.get();
        Vol vol = volOpt.get();
        
        if (chargementCargoRepository.existsByCargoIdCargo(idCargo)) {
            throw new IllegalArgumentException("Le cargo est deja charge sur un vol");
        }
        
        if (vol.getAvion().getCapaciteCargo() < cargo.getPoids()) {
            throw new IllegalArgumentException("Le poids du cargo depasse la capacite cargo de l'avion");
        }
        
        ChargementCargo chargement = new ChargementCargo(vol, cargo);
        chargementCargoRepository.save(chargement);
        
        cargo.getChargements().add(chargement);
        cargoRepository.save(cargo);
    }
    
    public void deleteCargo(Long id) {
        if (!cargoRepository.existsById(id)) {
            throw new IllegalArgumentException("Cargo introuvable");
        }
        cargoRepository.deleteById(id);
    }
}

