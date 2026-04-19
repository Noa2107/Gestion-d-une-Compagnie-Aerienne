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
public class VolService {
    
    @Autowired
    private VolRepository volRepository;
    
    @Autowired
    private AvionRepository avionRepository;
    
    @Autowired
    private AeroportRepository aeroportRepository;
    
    @Autowired
    private EtatVolRepository etatVolRepository;
    
    @Autowired
    private HistoriqueEtatVolRepository historiqueEtatVolRepository;
    
    @Autowired
    private AvionService avionService;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    public List<Vol> getAllVols() {
        return volRepository.findAll();
    }
    
    public Optional<Vol> getVolById(Long id) {
        return volRepository.findById(id);
    }

    public void deleteVol(Long id) {
        if (!volRepository.existsById(id)) {
            throw new IllegalArgumentException("Vol introuvable");
        }
        if (reservationRepository.existsByVolIdVol(id)) {
            throw new IllegalArgumentException("Impossible de supprimer: ce vol contient des reservations");
        }
        volRepository.deleteById(id);
    }
    
    public Vol planifierVol(Vol vol) {
        if (vol.getAvion() == null || vol.getAvion().getIdAvion() == null) {
            throw new IllegalArgumentException("Un avion doit etre selectionne");
        }
        
        Optional<Avion> avionOpt = avionRepository.findById(vol.getAvion().getIdAvion());
        if (avionOpt.isEmpty()) {
            throw new IllegalArgumentException("Avion introuvable");
        }
        vol.setAvion(avionOpt.get());
        
        if (vol.getAeroportDepart() == null || vol.getAeroportDepart().getIdAeroport() == null) {
            throw new IllegalArgumentException("Un aeroport de depart doit etre selectionne");
        }
        
        Optional<Aeroport> aeroportDepartOpt = aeroportRepository.findById(vol.getAeroportDepart().getIdAeroport());
        if (aeroportDepartOpt.isEmpty()) {
            throw new IllegalArgumentException("Aeroport de depart introuvable");
        }
        vol.setAeroportDepart(aeroportDepartOpt.get());
        
        if (vol.getAeroportArrivee() == null || vol.getAeroportArrivee().getIdAeroport() == null) {
            throw new IllegalArgumentException("Un aeroport d'arrivee doit etre selectionne");
        }
        
        Optional<Aeroport> aeroportArriveeOpt = aeroportRepository.findById(vol.getAeroportArrivee().getIdAeroport());
        if (aeroportArriveeOpt.isEmpty()) {
            throw new IllegalArgumentException("Aeroport d'arrivee introuvable");
        }
        vol.setAeroportArrivee(aeroportArriveeOpt.get());
        
        if (!avionService.peutEtreAssignerVol(vol.getAvion().getIdAvion())) {
            throw new IllegalArgumentException("L'avion n'est pas disponible (maintenance ou indisponible)");
        }
        
        if (vol.getDateVol().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date du vol doit etre dans le futur");
        }
        
        LocalDateTime debut = vol.getDateVol().minusHours(2);
        LocalDateTime fin = vol.getDateVol().plusHours(2);
        List<Vol> volsConflictuels = volRepository.findVolsByAvionAndDateRange(
            vol.getAvion().getIdAvion(), debut, fin);
        
        if (!volsConflictuels.isEmpty()) {
            throw new IllegalArgumentException("L'avion est deja assigne a un autre vol a cet horaire");
        }
        
        return volRepository.save(vol);
    }
    
    public Vol updateVol(Long id, Vol vol) {
        Optional<Vol> existing = volRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Vol introuvable");
        }
        
        Vol volToUpdate = existing.get();
        volToUpdate.setCodeVol(vol.getCodeVol());
        volToUpdate.setAvion(vol.getAvion());
        volToUpdate.setAeroportDepart(vol.getAeroportDepart());
        volToUpdate.setAeroportArrivee(vol.getAeroportArrivee());
        volToUpdate.setDateVol(vol.getDateVol());
        volToUpdate.setDateArrivee(vol.getDateArrivee());
        volToUpdate.setTypeVol(vol.getTypeVol());
        
        return volRepository.save(volToUpdate);
    }
    
    public void changerEtatVol(Long idVol, Integer codeEtat) {
        Optional<Vol> volOpt = volRepository.findById(idVol);
        if (volOpt.isEmpty()) {
            throw new IllegalArgumentException("Vol introuvable");
        }
        
        Optional<EtatVol> etatOpt = etatVolRepository.findByCodeEtat(codeEtat);
        if (etatOpt.isEmpty()) {
            throw new IllegalArgumentException("Code etat invalide");
        }
        
        Vol vol = volOpt.get();
        EtatVol etat = etatOpt.get();
        
        HistoriqueEtatVol historique = new HistoriqueEtatVol(vol, etat);
        historique.setDateChangement(LocalDateTime.now());
        historiqueEtatVolRepository.save(historique);
        
        vol.getHistoriqueEtats().add(historique);
        volRepository.save(vol);
    }
    
    public List<Vol> rechercherVolsDisponibles(Long idDepart, Long idArrivee, LocalDateTime dateRecherche) {
        if (idDepart == null || idArrivee == null || dateRecherche == null) {
            throw new IllegalArgumentException("Tous les parametres de recherche sont obligatoires");
        }
        
        if (idDepart.equals(idArrivee)) {
            throw new IllegalArgumentException("L'aeroport de depart et d'arrivee doivent etre differents");
        }
        
        return volRepository.rechercherVolsDisponibles(idDepart, idArrivee, dateRecherche);
    }
    
    public List<Vol> rechercherVolsParCodeAeroport(String codeDepart, String codeArrivee, LocalDateTime dateRecherche) {
        if (codeDepart == null || codeArrivee == null || dateRecherche == null) {
            throw new IllegalArgumentException("Tous les parametres de recherche sont obligatoires");
        }
        
        if (codeDepart.equalsIgnoreCase(codeArrivee)) {
            throw new IllegalArgumentException("Les codes aeroport de depart et d'arrivee doivent etre differents");
        }
        
        return volRepository.rechercherVolsParCodeAeroport(codeDepart, codeArrivee, dateRecherche);
    }
    
    public List<Vol> rechercherVolsParCodeEtDateHeure(String codeDepart, String codeArrivee, LocalDateTime dateHeure) {
        if (codeDepart == null || codeArrivee == null || dateHeure == null) {
            throw new IllegalArgumentException("Tous les parametres de recherche sont obligatoires");
        }
        if (codeDepart.equalsIgnoreCase(codeArrivee)) {
            throw new IllegalArgumentException("Les codes aeroport de depart et d'arrivee doivent etre differents");
        }

        LocalDateTime debut = dateHeure.minusMinutes(30);
        LocalDateTime fin = dateHeure.plusMinutes(30);
        return volRepository.rechercherVolsParCodeAeroportEtPlage(codeDepart, codeArrivee, debut, fin);
    }
    
    public boolean volADisponibilite(Vol vol, int nombrePlacesDemandees) {
        if (vol == null || vol.getAvion() == null) {
            return false;
        }
        
        if (!"PASSAGER".equals(vol.getTypeVol()) && !"MIXTE".equals(vol.getTypeVol())) {
            return false;
        }

        Integer placesReservees = reservationRepository.sumNombrePlacesByVolId(vol.getIdVol());
        int capaciteDisponible = vol.getAvion().getCapacitePassagers() - (placesReservees == null ? 0 : placesReservees);

        return capaciteDisponible >= nombrePlacesDemandees;
    }
    
    public int getPlacesDisponibles(Vol vol) {
        if (vol == null || vol.getAvion() == null) {
            return 0;
        }
        
        if (!"PASSAGER".equals(vol.getTypeVol()) && !"MIXTE".equals(vol.getTypeVol())) {
            return 0;
        }

        Integer placesReservees = reservationRepository.sumNombrePlacesByVolId(vol.getIdVol());
        int total = placesReservees == null ? 0 : placesReservees;
        return Math.max(0, vol.getAvion().getCapacitePassagers() - total);
    }
}
