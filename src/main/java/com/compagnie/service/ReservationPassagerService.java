package com.compagnie.service;

import com.compagnie.model.Passager;
import com.compagnie.model.Reservation;
import com.compagnie.model.ReservationPassager;
import com.compagnie.repository.PassagerRepository;
import com.compagnie.repository.ReservationPassagerRepository;
import com.compagnie.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReservationPassagerService {

    @Autowired
    private ReservationPassagerRepository reservationPassagerRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PassagerRepository passagerRepository;

    public List<ReservationPassager> getByReservation(Long idReservation) {
        return reservationPassagerRepository.findByReservationIdReservationOrderBySiegeAsc(idReservation);
    }

    public void assignPassagerToSeat(Long idReservation, Long idPassager, String siege, List<String> allowedSeats) {
        if (siege == null || siege.isBlank()) {
            throw new IllegalArgumentException("Siege invalide");
        }
        String seat = siege.trim();

        if (allowedSeats != null && !allowedSeats.isEmpty() && !allowedSeats.contains(seat)) {
            throw new IllegalArgumentException("Ce siege ne correspond pas a cette reservation");
        }

        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new IllegalArgumentException("Reservation introuvable"));

        Passager passager = passagerRepository.findById(idPassager)
                .orElseThrow(() -> new IllegalArgumentException("Passager introuvable"));

        ReservationPassager rp = reservationPassagerRepository.findByReservationIdReservationAndSiege(idReservation, seat)
                .orElseGet(() -> new ReservationPassager(reservation, passager, seat));

        rp.setReservation(reservation);
        rp.setPassager(passager);
        rp.setSiege(seat);

        reservationPassagerRepository.save(rp);
    }
}
