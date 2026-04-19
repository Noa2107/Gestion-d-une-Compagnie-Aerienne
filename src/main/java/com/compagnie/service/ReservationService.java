package com.compagnie.service;

import com.compagnie.model.*;
import com.compagnie.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@Service
@Transactional
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private PassagerRepository passagerRepository;
    
    @Autowired
    private VolRepository volRepository;
    
    @Autowired
    private EtatReservationRepository etatReservationRepository;
    
    @Autowired
    private HistoriqueEtatReservationRepository historiqueEtatReservationRepository;
    
    @Autowired
    private TarificationService tarificationService;

    @Autowired
    private ReservationTypePassagerRepository reservationTypePassagerRepository;

    @Autowired
    private TypePassagerRepository typePassagerRepository;

    @Autowired
    private PaiementReservationRepository paiementReservationRepository;
    
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id).map(this::populateMixFromDb);
    }

    private Reservation populateMixFromDb(Reservation reservation) {
        if (reservation == null || reservation.getIdReservation() == null) {
            return reservation;
        }

        List<ReservationTypePassager> details = reservationTypePassagerRepository.findByReservationIdReservation(reservation.getIdReservation());
        if (details == null || details.isEmpty()) {
            reservation.setNbAdultes(reservation.getNombrePlaces());
            reservation.setNbEnfants(0);
            reservation.setNbBebes(0);
            return reservation;
        }

        int nbAdultes = 0;
        int nbEnfants = 0;
        int nbBebes = 0;
        for (ReservationTypePassager d : details) {
            if (d == null || d.getTypePassager() == null || d.getTypePassager().getNomType() == null) continue;
            if ("ADULTE".equalsIgnoreCase(d.getTypePassager().getNomType())) {
                nbAdultes += d.getNombre() == null ? 0 : d.getNombre();
            } else if ("ENFANT".equalsIgnoreCase(d.getTypePassager().getNomType())) {
                nbEnfants += d.getNombre() == null ? 0 : d.getNombre();
            } else if ("BEBE".equalsIgnoreCase(d.getTypePassager().getNomType())) {
                nbBebes += d.getNombre() == null ? 0 : d.getNombre();
            }
        }
        reservation.setNbAdultes(nbAdultes);
        reservation.setNbEnfants(nbEnfants);
        reservation.setNbBebes(nbBebes);
        return reservation;
    }
    
    public BigDecimal getChiffreAffaireParVolEtDateHeure(Long idVol, LocalDateTime dateHeureVol) {
        if (idVol == null || dateHeureVol == null) {
            throw new IllegalArgumentException("Le vol et la date/heure sont obligatoires");
        }
        BigDecimal total = reservationRepository.sumChiffreAffaireByVolIdAndDateHeureVol(idVol, dateHeureVol);
        return total == null ? BigDecimal.ZERO : total;
    }
    
    public Reservation createReservation(Reservation reservation) {
        if (reservation.getPassager() == null || reservation.getPassager().getIdPassager() == null) {
            throw new IllegalArgumentException("Un passager doit etre selectionne");
        }
        
        if (reservation.getVol() == null || reservation.getVol().getIdVol() == null) {
            throw new IllegalArgumentException("Un vol doit etre selectionne");
        }

        if (reservation.getClasse() == null || reservation.getClasse().trim().isEmpty()) {
            throw new IllegalArgumentException("La classe est obligatoire");
        }

        Integer nbAdultes = reservation.getNbAdultes();
        Integer nbEnfants = reservation.getNbEnfants();
        Integer nbBebes = reservation.getNbBebes();
        if (nbAdultes != null || nbEnfants != null || nbBebes != null) {
            int a = nbAdultes == null ? 0 : nbAdultes;
            int e = nbEnfants == null ? 0 : nbEnfants;
            int b = nbBebes == null ? 0 : nbBebes;
            if (a < 0 || e < 0 || b < 0) {
                throw new IllegalArgumentException("Le nombre de passagers doit etre positif");
            }
            int totalPlaces = a + e + b;
            if (totalPlaces <= 0) {
                throw new IllegalArgumentException("Le nombre total de places doit etre strictement positif");
            }
            reservation.setNombrePlaces(totalPlaces);
        } else {
            if (reservation.getNombrePlaces() == null) {
                reservation.setNombrePlaces(1);
            }
            if (reservation.getNombrePlaces() <= 0) {
                throw new IllegalArgumentException("Le nombre de places doit etre strictement positif");
            }
            nbAdultes = reservation.getNombrePlaces();
            nbEnfants = 0;
            nbBebes = 0;
        }
        
        Optional<Passager> passagerOpt = passagerRepository.findById(reservation.getPassager().getIdPassager());
        if (passagerOpt.isEmpty()) {
            throw new IllegalArgumentException("Passager introuvable");
        }
        
        Optional<Vol> volOpt = volRepository.findById(reservation.getVol().getIdVol());
        if (volOpt.isEmpty()) {
            throw new IllegalArgumentException("Vol introuvable");
        }
        
        Vol vol = volOpt.get();

        String siegeSaisi = reservation.getNumeroSiege();
        if (siegeSaisi != null) {
            siegeSaisi = siegeSaisi.trim().toUpperCase(Locale.ROOT);
            if (siegeSaisi.isEmpty()) {
                siegeSaisi = null;
            }
        }

        if (reservation.getNombrePlaces() == 1) {
            if (siegeSaisi != null) {
                if (reservationRepository.existsByVolIdVolAndNumeroSiege(vol.getIdVol(), siegeSaisi)) {
                    throw new IllegalArgumentException("Le numero de siege est deja occupe pour ce vol");
                }
                reservation.setNumeroSiege(siegeSaisi);
            } else {
                reservation.setNumeroSiege(null);
            }
            reservation.setSiegesAttribues(null);
        } else {
            reservation.setNumeroSiege(null);
            reservation.setSiegesAttribues(String.join(", ", genererSiegesDisponibles(vol, reservation.getNombrePlaces())));
        }
        
        if ("PASSAGER".equals(vol.getTypeVol()) || "MIXTE".equals(vol.getTypeVol())) {
            Integer placesReservees = reservationRepository.sumNombrePlacesByVolId(vol.getIdVol());
            int total = placesReservees == null ? 0 : placesReservees;
            int placesRestantes = vol.getAvion().getCapacitePassagers() - total;
            if (placesRestantes < reservation.getNombrePlaces()) {
                throw new IllegalArgumentException("La capacite passagers de l'avion est insuffisante (places restantes: " + Math.max(0, placesRestantes) + ")");
            }
        }
        
        reservation.setPassager(passagerOpt.get());
        reservation.setVol(vol);
        reservation.setDateReservation(LocalDateTime.now());

        reservation.setPrixTotal(
                tarificationService.calculerPrixTotalMix(
                        vol.getIdVol(),
                        reservation.getClasse(),
                        nbAdultes,
                        nbEnfants,
                        nbBebes,
                        LocalDateTime.now())
        );

        Reservation saved = reservationRepository.save(reservation);

        List<ReservationTypePassager> details = new ArrayList<>();
        if (nbAdultes != null && nbAdultes > 0) {
            TypePassager adulte = typePassagerRepository.findByNomTypeIgnoreCase("Adulte")
                    .orElseThrow(() -> new IllegalArgumentException("Type passager introuvable: Adulte"));
            ReservationTypePassager rtp = new ReservationTypePassager();
            rtp.setReservation(saved);
            rtp.setTypePassager(adulte);
            rtp.setNombre(nbAdultes);
            rtp.setId(new ReservationTypePassagerId(saved.getIdReservation(), adulte.getIdTypePassager()));
            details.add(rtp);
        }
        if (nbEnfants != null && nbEnfants > 0) {
            TypePassager enfant = typePassagerRepository.findByNomTypeIgnoreCase("Enfant")
                    .orElseThrow(() -> new IllegalArgumentException("Type passager introuvable: Enfant"));
            ReservationTypePassager rtp = new ReservationTypePassager();
            rtp.setReservation(saved);
            rtp.setTypePassager(enfant);
            rtp.setNombre(nbEnfants);
            rtp.setId(new ReservationTypePassagerId(saved.getIdReservation(), enfant.getIdTypePassager()));
            details.add(rtp);
        }
        if (nbBebes != null && nbBebes > 0) {
            TypePassager bebe = typePassagerRepository.findByNomTypeIgnoreCase("Bebe")
                    .orElseThrow(() -> new IllegalArgumentException("Type passager introuvable: Bebe"));
            ReservationTypePassager rtp = new ReservationTypePassager();
            rtp.setReservation(saved);
            rtp.setTypePassager(bebe);
            rtp.setNombre(nbBebes);
            rtp.setId(new ReservationTypePassagerId(saved.getIdReservation(), bebe.getIdTypePassager()));
            details.add(rtp);
        }
        if (!details.isEmpty()) {
            reservationTypePassagerRepository.saveAll(details);
        }

        return saved;
    }

    private List<String> genererSiegesDisponibles(Vol vol, int nombrePlaces) {
        if (nombrePlaces <= 0) {
            throw new IllegalArgumentException("Le nombre de places doit etre strictement positif");
        }
        if (vol == null || vol.getAvion() == null || vol.getAvion().getCapacitePassagers() == null) {
            throw new IllegalArgumentException("Vol ou avion invalide");
        }

        Set<String> siegesOccupes = new HashSet<>();
        List<Reservation> reservations = reservationRepository.findByVolIdVol(vol.getIdVol());
        for (Reservation r : reservations) {
            if (r.getNumeroSiege() != null && !r.getNumeroSiege().trim().isEmpty()) {
                siegesOccupes.add(r.getNumeroSiege().trim().toUpperCase(Locale.ROOT));
            }
            if (r.getSiegesAttribues() != null && !r.getSiegesAttribues().trim().isEmpty()) {
                String[] parts = r.getSiegesAttribues().split(",");
                for (String p : parts) {
                    String s = p.trim().toUpperCase(Locale.ROOT);
                    if (!s.isEmpty()) {
                        siegesOccupes.add(s);
                    }
                }
            }
        }

        int capacite = vol.getAvion().getCapacitePassagers();
        char[] colonnes = new char[] {'A', 'B', 'C', 'D', 'E', 'F'};
        int siegesParLigne = colonnes.length;
        int lignesMax = (int) Math.ceil((double) capacite / siegesParLigne);

        List<String> result = new ArrayList<>();
        for (int ligne = 1; ligne <= lignesMax && result.size() < nombrePlaces; ligne++) {
            for (char col : colonnes) {
                if (result.size() >= nombrePlaces) {
                    break;
                }
                String siege = ligne + String.valueOf(col);
                if (!siegesOccupes.contains(siege)) {
                    result.add(siege);
                }
            }
        }

        if (result.size() < nombrePlaces) {
            throw new IllegalArgumentException("Places insuffisantes pour attribuer automatiquement les sieges");
        }
        return result;
    }
    
    public Reservation updateReservation(Long id, Reservation reservation) {
        Optional<Reservation> existing = reservationRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Reservation introuvable");
        }

        Reservation reservationToUpdate = existing.get();

        if (reservation.getClasse() == null || reservation.getClasse().trim().isEmpty()) {
            throw new IllegalArgumentException("La classe est obligatoire");
        }

        if (reservationToUpdate.getNombrePlaces() != null && reservationToUpdate.getNombrePlaces() > 1) {
            reservationToUpdate.setNumeroSiege(null);
        } else {
            String nouveauSiege = reservation.getNumeroSiege();
            if (nouveauSiege != null) {
                nouveauSiege = nouveauSiege.trim().toUpperCase(Locale.ROOT);
                if (nouveauSiege.isEmpty()) {
                    nouveauSiege = null;
                }
            }

            String siegeActuel = reservationToUpdate.getNumeroSiege();
            boolean siegeChange = (siegeActuel == null && nouveauSiege != null)
                    || (siegeActuel != null && !siegeActuel.equals(nouveauSiege));

            if (siegeChange && nouveauSiege != null) {
                if (reservationRepository.existsByVolIdVolAndNumeroSiege(
                        reservationToUpdate.getVol().getIdVol(), nouveauSiege)) {
                    throw new IllegalArgumentException("Le numero de siege est deja occupe");
                }
            }

            reservationToUpdate.setNumeroSiege(nouveauSiege);
        }

        reservationToUpdate.setClasse(reservation.getClasse().trim());
        reservationToUpdate.setBagageKg(reservation.getBagageKg());

        Integer nbAdultes = reservation.getNbAdultes();
        Integer nbEnfants = reservation.getNbEnfants();
        Integer nbBebes = reservation.getNbBebes();
        if (nbAdultes != null || nbEnfants != null || nbBebes != null) {
            int a = nbAdultes == null ? 0 : nbAdultes;
            int e = nbEnfants == null ? 0 : nbEnfants;
            int b = nbBebes == null ? 0 : nbBebes;
            if (a < 0 || e < 0 || b < 0) {
                throw new IllegalArgumentException("Le nombre de passagers doit etre positif");
            }
            int total = a + e + b;
            if (total <= 0) {
                throw new IllegalArgumentException("Le nombre total de places doit etre strictement positif");
            }

            if (reservationToUpdate.getNombrePlaces() != null && reservationToUpdate.getNombrePlaces() != total) {
                throw new IllegalArgumentException("Impossible de modifier le nombre total de places sur une reservation existante");
            }

            reservationTypePassagerRepository.deleteByReservationIdReservation(reservationToUpdate.getIdReservation());

            List<ReservationTypePassager> details = new ArrayList<>();
            if (a > 0) {
                TypePassager adulte = typePassagerRepository.findByNomTypeIgnoreCase("Adulte")
                        .orElseThrow(() -> new IllegalArgumentException("Type passager introuvable: Adulte"));
                ReservationTypePassager rtp = new ReservationTypePassager();
                rtp.setReservation(reservationToUpdate);
                rtp.setTypePassager(adulte);
                rtp.setNombre(a);
                rtp.setId(new ReservationTypePassagerId(reservationToUpdate.getIdReservation(), adulte.getIdTypePassager()));
                details.add(rtp);
            }
            if (e > 0) {
                TypePassager enfant = typePassagerRepository.findByNomTypeIgnoreCase("Enfant")
                        .orElseThrow(() -> new IllegalArgumentException("Type passager introuvable: Enfant"));
                ReservationTypePassager rtp = new ReservationTypePassager();
                rtp.setReservation(reservationToUpdate);
                rtp.setTypePassager(enfant);
                rtp.setNombre(e);
                rtp.setId(new ReservationTypePassagerId(reservationToUpdate.getIdReservation(), enfant.getIdTypePassager()));
                details.add(rtp);
            }
            if (b > 0) {
                TypePassager bebe = typePassagerRepository.findByNomTypeIgnoreCase("Bebe")
                        .orElseThrow(() -> new IllegalArgumentException("Type passager introuvable: Bebe"));
                ReservationTypePassager rtp = new ReservationTypePassager();
                rtp.setReservation(reservationToUpdate);
                rtp.setTypePassager(bebe);
                rtp.setNombre(b);
                rtp.setId(new ReservationTypePassagerId(reservationToUpdate.getIdReservation(), bebe.getIdTypePassager()));
                details.add(rtp);
            }
            if (!details.isEmpty()) {
                reservationTypePassagerRepository.saveAll(details);
            }
        } else {
            nbAdultes = reservationToUpdate.getNombrePlaces();
            nbEnfants = 0;
            nbBebes = 0;
        }

        reservationToUpdate.setPrixTotal(
                tarificationService.calculerPrixTotalMix(
                        reservationToUpdate.getVol().getIdVol(),
                        reservationToUpdate.getClasse(),
                        nbAdultes,
                        nbEnfants,
                        nbBebes,
                        LocalDateTime.now())
        );

        return reservationRepository.save(reservationToUpdate);
    }

    public void changerEtatReservation(Long idReservation, Integer codeEtat) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(idReservation);
        if (reservationOpt.isEmpty()) {
            throw new IllegalArgumentException("Reservation introuvable");
        }

        Optional<EtatReservation> etatOpt = etatReservationRepository.findByCodeEtat(codeEtat);
        if (etatOpt.isEmpty()) {
            throw new IllegalArgumentException("Code etat invalide");
        }

        Reservation reservation = reservationOpt.get();
        EtatReservation etat = etatOpt.get();

        HistoriqueEtatReservation historique = new HistoriqueEtatReservation(reservation, etat);
        historique.setDateChangement(LocalDateTime.now());
        historiqueEtatReservationRepository.save(historique);

        reservation.getHistoriqueEtats().add(historique);
        reservationRepository.save(reservation);
    }

    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new IllegalArgumentException("Reservation introuvable");
        }
        if (paiementReservationRepository.existsByReservationIdReservation(id)) {
            throw new IllegalArgumentException("Impossible de supprimer: la reservation a deja un paiement");
        }
        reservationRepository.deleteById(id);
    }
}

