package com.compagnie.service;

import com.compagnie.model.PaiementReservation;
import com.compagnie.model.Reservation;
import com.compagnie.model.ReservationTypePassager;
import com.compagnie.model.Vol;
import com.compagnie.model.TypePassager;
import com.compagnie.repository.PaiementReservationRepository;
import com.compagnie.repository.ReservationTypePassagerRepository;
import com.compagnie.repository.TypePassagerRepository;
import com.compagnie.repository.VolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ChiffreAffaireService {

    @Autowired
    private VolRepository volRepository;

    @Autowired
    private PaiementReservationRepository paiementReservationRepository;

    @Autowired
    private ReservationTypePassagerRepository reservationTypePassagerRepository;

    @Autowired
    private TypePassagerRepository typePassagerRepository;

    @Autowired
    private TarificationService tarificationService;

    public Map<String, Object> calculerCaParVol(Long idVol, LocalDateTime now) {
        if (idVol == null) {
            throw new IllegalArgumentException("Le vol est obligatoire");
        }

        Optional<Vol> volOpt = volRepository.findById(idVol);
        if (volOpt.isEmpty()) {
            throw new IllegalArgumentException("Vol introuvable");
        }

        LocalDateTime date = now == null ? LocalDateTime.now() : now;

        List<PaiementReservation> paiements = paiementReservationRepository.findByReservationVolIdVol(idVol);

        BigDecimal total = BigDecimal.ZERO;
        Map<String, BigDecimal> caParClasse = new HashMap<>();
        Map<String, Map<String, BigDecimal>> caParClasseEtType = new HashMap<>();

        for (PaiementReservation p : paiements) {
            if (p == null) continue;
            BigDecimal montant = p.getMontantReferenceMga();
            if (montant == null) {
                continue;
            }
            if (montant.compareTo(BigDecimal.ZERO) < 0) {
                continue;
            }

            Reservation r = p.getReservation();
            if (r == null) continue;

            String classe = r.getClasse() == null ? "" : r.getClasse().trim();
            if (classe.isEmpty()) {
                classe = "(Non defini)";
            }

            total = total.add(montant);
            caParClasse.put(classe, caParClasse.getOrDefault(classe, BigDecimal.ZERO).add(montant));

            Map<String, Integer> qtyByType = new HashMap<>();
            List<ReservationTypePassager> details = reservationTypePassagerRepository.findByReservationIdReservation(r.getIdReservation());
            if (details != null && !details.isEmpty()) {
                for (ReservationTypePassager d : details) {
                    if (d == null || d.getTypePassager() == null || d.getTypePassager().getNomType() == null) continue;
                    String nomType = d.getTypePassager().getNomType().trim();
                    int q = d.getNombre() == null ? 0 : d.getNombre();
                    qtyByType.put(nomType, qtyByType.getOrDefault(nomType, 0) + q);
                }
            } else {
                int q = r.getNombrePlaces() == null ? 0 : r.getNombrePlaces();
                if (q > 0) {
                    qtyByType.put("Adulte", q);
                }
            }

            BigDecimal totalRecalc = BigDecimal.ZERO;
            Map<String, BigDecimal> sousTotaux = new HashMap<>();

            for (Map.Entry<String, Integer> entry : qtyByType.entrySet()) {
                String nomType = entry.getKey();
                int q = entry.getValue() == null ? 0 : entry.getValue();
                if (q <= 0) continue;

                BigDecimal st;
                try {
                    Integer idTypePassager = resolveIdTypePassager(nomType);
                    st = tarificationService.calculerSousTotalParType(idVol, classe, idTypePassager, q, date);
                } catch (IllegalArgumentException ex) {
                    st = BigDecimal.ZERO;
                }

                sousTotaux.put(nomType, st);
                totalRecalc = totalRecalc.add(st);
            }

            Map<String, BigDecimal> perType = caParClasseEtType.computeIfAbsent(classe, k -> new HashMap<>());

            if (totalRecalc.compareTo(BigDecimal.ZERO) > 0) {
                for (Map.Entry<String, BigDecimal> stEntry : sousTotaux.entrySet()) {
                    String nomType = stEntry.getKey();
                    BigDecimal st = stEntry.getValue() == null ? BigDecimal.ZERO : stEntry.getValue();
                    BigDecimal part = montant.multiply(st).divide(totalRecalc, 2, RoundingMode.HALF_UP);
                    perType.put(nomType, perType.getOrDefault(nomType, BigDecimal.ZERO).add(part));
                }
            } else {
                for (Map.Entry<String, Integer> entry : qtyByType.entrySet()) {
                    String nomType = entry.getKey();
                    int q = entry.getValue() == null ? 0 : entry.getValue();
                    if (q <= 0) continue;
                    perType.put(nomType, perType.getOrDefault(nomType, BigDecimal.ZERO).add(BigDecimal.ZERO));
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("vol", volOpt.get());
        result.put("total", total.setScale(2, RoundingMode.HALF_UP));
        result.put("caParClasse", caParClasse);
        result.put("caParClasseEtType", caParClasseEtType);
        return result;
    }

    private Integer resolveIdTypePassager(String nomType) {
        if (nomType == null || nomType.trim().isEmpty()) {
            throw new IllegalArgumentException("Type passager obligatoire");
        }

        TypePassager tp = typePassagerRepository.findByNomTypeIgnoreCase(nomType.trim())
                .orElseThrow(() -> new IllegalArgumentException("Type passager introuvable: " + nomType));
        return tp.getIdTypePassager();
    }
}
