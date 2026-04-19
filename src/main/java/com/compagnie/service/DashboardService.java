package com.compagnie.service;

import com.compagnie.model.PaiementCargo;
import com.compagnie.model.PaiementReservation;
import com.compagnie.model.Vol;
import com.compagnie.repository.PaiementCargoRepository;
import com.compagnie.repository.PaiementReservationRepository;
import com.compagnie.repository.ReservationRepository;
import com.compagnie.repository.VolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private PaiementReservationRepository paiementReservationRepository;

    @Autowired
    private PaiementCargoRepository paiementCargoRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VolRepository volRepository;

    public Map<String, Object> buildDashboardData(int days) {
        int safeDays = Math.max(7, Math.min(days, 90));
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(safeDays - 1L);

        List<PaiementReservation> paiementReservations = paiementReservationRepository.findAll();
        List<PaiementCargo> paiementCargos = paiementCargoRepository.findAll();

        BigDecimal caTotal = BigDecimal.ZERO;
        BigDecimal caToday = BigDecimal.ZERO;
        BigDecimal caMonth = BigDecimal.ZERO;

        Map<LocalDate, BigDecimal> caByDay = new TreeMap<>();
        Map<LocalDate, Integer> countByDay = new TreeMap<>();

        Map<String, BigDecimal> caByDevise = new LinkedHashMap<>();
        Map<String, BigDecimal> caByVol = new LinkedHashMap<>();

        YearMonth currentMonth = YearMonth.from(today);

        for (PaiementReservation p : paiementReservations) {
            if (p == null) continue;

            BigDecimal mga = safeBigDecimal(p.getMontantReferenceMga());
            caTotal = caTotal.add(mga);

            LocalDateTime dt = p.getDateCreation();
            if (dt != null) {
                LocalDate d = dt.toLocalDate();

                if (d.equals(today)) {
                    caToday = caToday.add(mga);
                }
                if (YearMonth.from(d).equals(currentMonth)) {
                    caMonth = caMonth.add(mga);
                }

                if (!d.isBefore(startDate) && !d.isAfter(today)) {
                    caByDay.merge(d, mga, BigDecimal::add);
                    countByDay.merge(d, 1, Integer::sum);
                }
            }

            String deviseCode = (p.getDevise() != null && p.getDevise().getCodeDevise() != null)
                    ? p.getDevise().getCodeDevise().toUpperCase(Locale.ROOT)
                    : "N/A";
            caByDevise.merge(deviseCode, mga, BigDecimal::add);

            Vol vol = (p.getReservation() != null) ? p.getReservation().getVol() : null;
            if (vol != null) {
                String label = (vol.getCodeVol() != null && !vol.getCodeVol().isBlank())
                        ? vol.getCodeVol()
                        : "VOL #" + vol.getIdVol();
                caByVol.merge(label, mga, BigDecimal::add);
            }
        }

        for (PaiementCargo p : paiementCargos) {
            if (p == null) continue;

            BigDecimal mga = safeBigDecimal(p.getMontantReferenceMga());
            caTotal = caTotal.add(mga);

            LocalDateTime dt = p.getDateCreation();
            if (dt != null) {
                LocalDate d = dt.toLocalDate();

                if (d.equals(today)) {
                    caToday = caToday.add(mga);
                }
                if (YearMonth.from(d).equals(currentMonth)) {
                    caMonth = caMonth.add(mga);
                }

                if (!d.isBefore(startDate) && !d.isAfter(today)) {
                    caByDay.merge(d, mga, BigDecimal::add);
                    countByDay.merge(d, 1, Integer::sum);
                }
            }

            String deviseCode = (p.getDevise() != null && p.getDevise().getCodeDevise() != null)
                    ? p.getDevise().getCodeDevise().toUpperCase(Locale.ROOT)
                    : "N/A";
            caByDevise.merge(deviseCode, mga, BigDecimal::add);
        }

        List<String> labels = new ArrayList<>();
        List<BigDecimal> seriesCA = new ArrayList<>();
        List<Integer> seriesCount = new ArrayList<>();

        for (int i = 0; i < safeDays; i++) {
            LocalDate d = startDate.plusDays(i);
            labels.add(d.toString());
            seriesCA.add(caByDay.getOrDefault(d, BigDecimal.ZERO));
            seriesCount.add(countByDay.getOrDefault(d, 0));
        }

        Map<String, BigDecimal> topVols = caByVol.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.naturalOrder()).reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        List<Map<String, Object>> lastPayments = buildLastPayments(paiementReservations, paiementCargos);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("kpiCaTotal", caTotal);
        result.put("kpiCaToday", caToday);
        result.put("kpiCaMonth", caMonth);
        result.put("kpiPaymentsCount", paiementReservations.size() + paiementCargos.size());
        result.put("kpiReservationsCount", reservationRepository.count());
        result.put("kpiVolsCount", volRepository.count());

        result.put("labels", labels);
        result.put("seriesCA", seriesCA);
        result.put("seriesCount", seriesCount);

        result.put("deviseLabels", new ArrayList<>(caByDevise.keySet()));
        result.put("deviseSeries", new ArrayList<>(caByDevise.values()));

        result.put("topVolLabels", new ArrayList<>(topVols.keySet()));
        result.put("topVolSeries", new ArrayList<>(topVols.values()));

        result.put("lastPayments", lastPayments);
        return result;
    }

    private List<Map<String, Object>> buildLastPayments(List<PaiementReservation> prs, List<PaiementCargo> pcs) {
        List<Map<String, Object>> all = new ArrayList<>();

        for (PaiementReservation p : prs) {
            if (p == null) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("type", "Reservation");
            row.put("id", p.getIdPaiementReservation());
            row.put("ref", p.getReferenceTransaction());
            row.put("devise", (p.getDevise() != null) ? p.getDevise().getCodeDevise() : null);
            row.put("mga", safeBigDecimal(p.getMontantReferenceMga()));
            row.put("date", p.getDateCreation());
            all.add(row);
        }

        for (PaiementCargo p : pcs) {
            if (p == null) continue;
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("type", "Cargo");
            row.put("id", p.getIdPaiementCargo());
            row.put("ref", p.getReferenceFacture());
            row.put("devise", (p.getDevise() != null) ? p.getDevise().getCodeDevise() : null);
            row.put("mga", safeBigDecimal(p.getMontantReferenceMga()));
            row.put("date", p.getDateCreation());
            all.add(row);
        }

        return all.stream()
                .sorted((a, b) -> {
                    LocalDateTime da = (LocalDateTime) a.get("date");
                    LocalDateTime db = (LocalDateTime) b.get("date");
                    if (da == null && db == null) return 0;
                    if (da == null) return 1;
                    if (db == null) return -1;
                    return db.compareTo(da);
                })
                .limit(8)
                .collect(Collectors.toList());
    }

    private BigDecimal safeBigDecimal(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}
