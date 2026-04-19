package com.compagnie.service;

import com.compagnie.model.*;
import com.compagnie.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaiementService {
    
    @Autowired
    private PaiementReservationRepository paiementReservationRepository;
    
    @Autowired
    private PaiementCargoRepository paiementCargoRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private CargoRepository cargoRepository;
    
    @Autowired
    private DeviseRepository deviseRepository;
    
    @Autowired
    private TauxChangeRepository tauxChangeRepository;
    
    @Autowired
    private EtatPaiementRepository etatPaiementRepository;
    
    @Autowired
    private HistoriqueEtatPaiementReservationRepository historiquePaiementReservationRepository;
    
    @Autowired
    private HistoriqueEtatPaiementCargoRepository historiquePaiementCargoRepository;
    
    public List<PaiementReservation> getAllPaiementsReservation() {
        return paiementReservationRepository.findAll();
    }
    
    public List<PaiementCargo> getAllPaiementsCargo() {
        return paiementCargoRepository.findAll();
    }

    public Optional<PaiementReservation> getPaiementReservationById(Long id) {
        return paiementReservationRepository.findById(id);
    }

    public Optional<PaiementCargo> getPaiementCargoById(Long id) {
        return paiementCargoRepository.findById(id);
    }

    public void deletePaiementReservation(Long id) {
        if (!paiementReservationRepository.existsById(id)) {
            throw new IllegalArgumentException("Paiement introuvable");
        }
        paiementReservationRepository.deleteById(id);
    }

    public void deletePaiementCargo(Long id) {
        if (!paiementCargoRepository.existsById(id)) {
            throw new IllegalArgumentException("Paiement introuvable");
        }
        paiementCargoRepository.deleteById(id);
    }

    private String genererReferenceTransactionReservation() {
        long dernierId = paiementReservationRepository.findTopByOrderByIdPaiementReservationDesc()
                .map(PaiementReservation::getIdPaiementReservation)
                .orElse(0L);
        long numero = dernierId + 1;
        return String.format("RES%04d", numero);
    }
    
    public PaiementReservation addPaiementReservation(PaiementReservation paiement) {
        if (paiement.getReservation() == null || paiement.getReservation().getIdReservation() == null) {
            throw new IllegalArgumentException("Une reservation doit etre selectionnee");
        }
        
        Optional<Reservation> reservationOpt = reservationRepository.findById(paiement.getReservation().getIdReservation());
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException("Reservation introuvable");
        }
        
        Reservation reservation = reservationOpt.get();
        paiement.setReservation(reservation);

        if (paiement.getDevise() == null || paiement.getDevise().getIdDevise() == null) {
            throw new IllegalArgumentException("Une devise doit etre selectionnee");
        }
        
        Optional<Devise> deviseOpt = deviseRepository.findById(paiement.getDevise().getIdDevise());
        if (deviseOpt.isEmpty()) {
            throw new IllegalArgumentException("Devise introuvable");
        }
        
        Devise devise = deviseOpt.get();
        paiement.setDevise(devise);
        
        Optional<Devise> deviseMgaOpt = deviseRepository.findByEstReferenceTrue();
        if (deviseMgaOpt.isEmpty()) {
            throw new IllegalArgumentException("Devise de reference (MGA) introuvable");
        }
        
        Devise deviseMga = deviseMgaOpt.get();
        
        BigDecimal montantMga;
        if (devise.getIdDevise().equals(deviseMga.getIdDevise())) {
            montantMga = paiement.getMontantPaye();
        } else {
            Optional<TauxChange> tauxOpt = tauxChangeRepository.findDernierTauxActifByDevise(devise.getIdDevise());
            if (tauxOpt.isEmpty()) {
                throw new IllegalArgumentException("Taux de change non trouve pour cette devise");
            }
            TauxChange taux = tauxOpt.get();
            paiement.setTaux(taux);
            montantMga = paiement.getMontantPaye().multiply(taux.getTaux());
        }
        
        paiement.setMontantReferenceMga(montantMga);
        
        if (reservation.getPrixTotal() != null && montantMga.compareTo(reservation.getPrixTotal()) < 0) {
            throw new IllegalArgumentException("Le montant paye est inferieur au prix total de la reservation");
        }

        paiement.setReferenceTransaction(genererReferenceTransactionReservation());
        
        paiement.setDateCreation(LocalDateTime.now());
        PaiementReservation saved = paiementReservationRepository.save(paiement);
        
        Optional<EtatPaiement> etatEnAttenteOpt = etatPaiementRepository.findByCodeEtat(1);
        if (etatEnAttenteOpt.isPresent()) {
            changerEtatPaiementReservation(saved.getIdPaiementReservation(), 1);
        }
        
        return saved;
    }
    
    public PaiementCargo addPaiementCargo(PaiementCargo paiement) {
        if (paiement.getCargo() == null || paiement.getCargo().getIdCargo() == null) {
            throw new IllegalArgumentException("Un cargo doit etre selectionne");
        }
        
        Optional<Cargo> cargoOpt = cargoRepository.findById(paiement.getCargo().getIdCargo());
        if (cargoOpt.isEmpty()) {
            throw new IllegalArgumentException("Cargo introuvable");
        }
        
        if (paiement.getDevise() == null || paiement.getDevise().getIdDevise() == null) {
            throw new IllegalArgumentException("Une devise doit etre selectionnee");
        }
        
        Optional<Devise> deviseOpt = deviseRepository.findById(paiement.getDevise().getIdDevise());
        if (deviseOpt.isEmpty()) {
            throw new IllegalArgumentException("Devise introuvable");
        }
        
        Devise devise = deviseOpt.get();
        paiement.setDevise(devise);
        
        Optional<Devise> deviseMgaOpt = deviseRepository.findByEstReferenceTrue();
        if (deviseMgaOpt.isEmpty()) {
            throw new IllegalArgumentException("Devise de reference (MGA) introuvable");
        }
        
        Devise deviseMga = deviseMgaOpt.get();
        
        BigDecimal montantMga;
        if (devise.getIdDevise().equals(deviseMga.getIdDevise())) {
            montantMga = paiement.getMontantPaye();
        } else {
            Optional<TauxChange> tauxOpt = tauxChangeRepository.findDernierTauxActifByDevise(devise.getIdDevise());
            if (tauxOpt.isEmpty()) {
                throw new IllegalArgumentException("Taux de change non trouve pour cette devise");
            }
            TauxChange taux = tauxOpt.get();
            paiement.setTaux(taux);
            montantMga = paiement.getMontantPaye().multiply(taux.getTaux());
        }
        
        paiement.setMontantReferenceMga(montantMga);
        paiement.setDateCreation(LocalDateTime.now());
        
        PaiementCargo saved = paiementCargoRepository.save(paiement);
        
        Optional<EtatPaiement> etatEnAttenteOpt = etatPaiementRepository.findByCodeEtat(1);
        if (etatEnAttenteOpt.isPresent()) {
            changerEtatPaiementCargo(saved.getIdPaiementCargo(), 1);
        }
        
        return saved;
    }
    
    public void changerEtatPaiementReservation(Long idPaiement, Integer codeEtat) {
        Optional<PaiementReservation> paiementOpt = paiementReservationRepository.findById(idPaiement);
        if (paiementOpt.isEmpty()) {
            throw new IllegalArgumentException("Paiement introuvable");
        }
        
        Optional<EtatPaiement> etatOpt = etatPaiementRepository.findByCodeEtat(codeEtat);
        if (etatOpt.isEmpty()) {
            throw new IllegalArgumentException("Code etat invalide");
        }
        
        PaiementReservation paiement = paiementOpt.get();
        EtatPaiement etat = etatOpt.get();
        
        HistoriqueEtatPaiementReservation historique = 
            new HistoriqueEtatPaiementReservation(paiement, etat);
        historique.setDateChangement(LocalDateTime.now());
        historiquePaiementReservationRepository.save(historique);
        
        paiement.getHistoriqueEtats().add(historique);
        paiementReservationRepository.save(paiement);
    }
    
    public void changerEtatPaiementCargo(Long idPaiement, Integer codeEtat) {
        Optional<PaiementCargo> paiementOpt = paiementCargoRepository.findById(idPaiement);
        if (paiementOpt.isEmpty()) {
            throw new IllegalArgumentException("Paiement introuvable");
        }
        
        Optional<EtatPaiement> etatOpt = etatPaiementRepository.findByCodeEtat(codeEtat);
        if (etatOpt.isEmpty()) {
            throw new IllegalArgumentException("Code etat invalide");
        }
        
        PaiementCargo paiement = paiementOpt.get();
        EtatPaiement etat = etatOpt.get();
        
        HistoriqueEtatPaiementCargo historique = 
            new HistoriqueEtatPaiementCargo(paiement, etat);
        historique.setDateChangement(LocalDateTime.now());
        historiquePaiementCargoRepository.save(historique);
        
        paiement.getHistoriqueEtats().add(historique);
        paiementCargoRepository.save(paiement);
    }
}

