package com.compagnie.service;

import com.compagnie.model.PaiementCargo;
import com.compagnie.model.PaiementReservation;
import com.compagnie.model.Reservation;
import com.compagnie.model.ReservationPassager;
import com.compagnie.model.Vol;
import com.compagnie.repository.ReservationPassagerRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfExportService {

    @Autowired
    private ReservationPassagerRepository reservationPassagerRepository;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] exportReservationTicket(Reservation reservation) {
        String fileTitle = "Billet - Airline";
        return buildPdf(doc -> {
            String pnr = "AIR" + String.format("%06d", reservation.getIdReservation() != null ? reservation.getIdReservation() : 0);
            String defaultPassenger = reservation.getPassager() != null
                    ? safe(reservation.getPassager().getNom()) + " " + safe(reservation.getPassager().getPrenom())
                    : "";

            List<String> seats = extractSeats(reservation);
            if (seats.isEmpty()) {
                seats = List.of("");
            }

            for (int i = 0; i < seats.size(); i++) {
                if (i > 0) {
                    doc.newPage();
                }

                addHeader(doc, "AIRLINE", "BILLET D'AVION");

                String passenger = resolvePassengerForSeat(reservation.getIdReservation(), seats.get(i), defaultPassenger);

                PdfPTable top = new PdfPTable(2);
                top.setWidthPercentage(100);
                top.setWidths(new float[]{70, 30});
                top.addCell(kvCell("Reference Billet (PNR)", pnr));
                top.addCell(kvCell("Ticket", (i + 1) + "/" + seats.size()));
                doc.add(top);
                doc.add(spacer());

                Vol vol = reservation.getVol();
                String from = vol != null && vol.getAeroportDepart() != null ? safe(vol.getAeroportDepart().getCode()) : "";
                String to = vol != null && vol.getAeroportArrivee() != null ? safe(vol.getAeroportArrivee().getCode()) : "";
                String volCode = vol != null && vol.getCodeVol() != null && !vol.getCodeVol().isBlank() ? vol.getCodeVol() : (vol != null ? "VOL #" + vol.getIdVol() : "");
                String dateVol = vol != null && vol.getDateVol() != null ? DT.format(vol.getDateVol()) : "";
                String dateArrivee = vol != null && vol.getDateArrivee() != null ? DT.format(vol.getDateArrivee()) : "";

                PdfPTable flight = new PdfPTable(2);
                flight.setWidthPercentage(100);
                flight.setWidths(new float[]{50, 50});
                flight.addCell(kvCell("Passager", passenger));
                flight.addCell(kvCell("Siege", safe(seats.get(i))));
                flight.addCell(kvCell("Vol", volCode));
                flight.addCell(kvCell("Classe", safe(reservation.getClasse())));
                flight.addCell(kvCell("De", from));
                flight.addCell(kvCell("Vers", to));
                flight.addCell(kvCell("Depart", dateVol));
                flight.addCell(kvCell("Arrivee", dateArrivee));
                doc.add(flight);
                doc.add(spacer());

                PdfPTable info = new PdfPTable(3);
                info.setWidthPercentage(100);
                info.setWidths(new float[]{34, 33, 33});
                info.addCell(kvCell("Places", safe(reservation.getNombrePlaces())));
                info.addCell(kvCell("Bagage (kg)", safe(reservation.getBagageKg())));
                BigDecimal prixBillet = reservation.getPrixTotal();
                if (reservation.getPrixTotal() != null
                        && reservation.getNombrePlaces() != null
                        && reservation.getNombrePlaces() > 1) {
                    prixBillet = reservation.getPrixTotal()
                            .divide(BigDecimal.valueOf(reservation.getNombrePlaces()), 2, RoundingMode.HALF_UP);
                }
                info.addCell(kvCell("Prix Billet", formatMga(prixBillet)));
                doc.add(info);
                doc.add(spacer());

                Paragraph note = new Paragraph("Vol effectue par Airline Company.", new Font(Font.HELVETICA, 10));
                note.setAlignment(Element.ALIGN_CENTER);
                doc.add(note);

                Paragraph footer = new Paragraph("Document genere le " + DT.format(LocalDateTime.now()), new Font(Font.HELVETICA, 8));
                footer.setAlignment(Element.ALIGN_CENTER);
                doc.add(footer);
            }
        }, fileTitle);
    }

    private List<String> extractSeats(Reservation reservation) {
        List<String> seats = new ArrayList<>();
        if (reservation == null) return seats;

        if (reservation.getNombrePlaces() != null && reservation.getNombrePlaces() == 1) {
            if (reservation.getNumeroSiege() != null && !reservation.getNumeroSiege().isBlank()) {
                seats.add(reservation.getNumeroSiege().trim());
            }
            return seats;
        }

        if (reservation.getSiegesAttribues() == null || reservation.getSiegesAttribues().isBlank()) {
            return seats;
        }

        for (String s : reservation.getSiegesAttribues().split(",")) {
            if (s == null) continue;
            String t = s.trim();
            if (!t.isBlank()) seats.add(t);
        }
        return seats;
    }

    private String resolvePassengerForSeat(Long idReservation, String seat, String defaultPassenger) {
        if (idReservation == null || seat == null || seat.isBlank()) {
            return defaultPassenger;
        }

        try {
            ReservationPassager rp = reservationPassagerRepository
                    .findByReservationIdReservationAndSiege(idReservation, seat.trim())
                    .orElse(null);
            if (rp == null || rp.getPassager() == null) {
                return defaultPassenger;
            }
            return safe(rp.getPassager().getNom()) + " " + safe(rp.getPassager().getPrenom());
        } catch (Exception e) {
            return defaultPassenger;
        }
    }

    public byte[] exportPaiementReservationReceipt(PaiementReservation paiement) {
        String fileTitle = "Recu Paiement Reservation - Airline";
        return buildPdf(doc -> {
            addHeader(doc, "AIRLINE", "RECU DE PAIEMENT (RESERVATION)");

            PdfPTable top = new PdfPTable(2);
            top.setWidthPercentage(100);
            top.setWidths(new float[]{60, 40});
            top.addCell(kvCell("Reference Transaction", safe(paiement.getReferenceTransaction())));
            top.addCell(kvCell("Paiement ID", safe(paiement.getIdPaiementReservation())));
            top.addCell(kvCell("Date", paiement.getDateCreation() != null ? DT.format(paiement.getDateCreation()) : ""));
            top.addCell(kvCell("Mode Paiement", safe(paiement.getModePaiement())));
            doc.add(top);
            doc.add(spacer());

            String passenger = (paiement.getReservation() != null && paiement.getReservation().getPassager() != null)
                    ? safe(paiement.getReservation().getPassager().getNom()) + " " + safe(paiement.getReservation().getPassager().getPrenom())
                    : "";

            PdfPTable details = new PdfPTable(2);
            details.setWidthPercentage(100);
            details.setWidths(new float[]{50, 50});
            details.addCell(kvCell("Reservation", paiement.getReservation() != null ? safe(paiement.getReservation().getIdReservation()) : ""));
            details.addCell(kvCell("Passager", passenger));
            details.addCell(kvCell("Montant Paye", formatDevise(paiement.getMontantPaye(), paiement.getDevise() != null ? paiement.getDevise().getSymbole() : "")));
            details.addCell(kvCell("Montant (MGA)", formatMga(paiement.getMontantReferenceMga())));
            doc.add(details);
            doc.add(spacer());

            Paragraph note = new Paragraph("Ce document fait office de recu officiel Airline.", new Font(Font.HELVETICA, 10));
            note.setAlignment(Element.ALIGN_CENTER);
            doc.add(note);

            Paragraph footer = new Paragraph("Document genere le " + DT.format(LocalDateTime.now()), new Font(Font.HELVETICA, 8));
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);
        }, fileTitle);
    }

    public byte[] exportPaiementCargoReceipt(PaiementCargo paiement) {
        String fileTitle = "Recu Paiement Cargo - Airline";
        return buildPdf(doc -> {
            addHeader(doc, "AIRLINE", "RECU DE PAIEMENT (CARGO)");

            PdfPTable top = new PdfPTable(2);
            top.setWidthPercentage(100);
            top.setWidths(new float[]{60, 40});
            top.addCell(kvCell("Reference Facture", safe(paiement.getReferenceFacture())));
            top.addCell(kvCell("Paiement ID", safe(paiement.getIdPaiementCargo())));
            top.addCell(kvCell("Date", paiement.getDateCreation() != null ? DT.format(paiement.getDateCreation()) : ""));
            top.addCell(kvCell("Mode Paiement", safe(paiement.getModePaiement())));
            doc.add(top);
            doc.add(spacer());

            PdfPTable details = new PdfPTable(2);
            details.setWidthPercentage(100);
            details.setWidths(new float[]{50, 50});
            details.addCell(kvCell("Cargo", paiement.getCargo() != null ? safe(paiement.getCargo().getIdCargo()) : ""));
            details.addCell(kvCell("Type Facturation", safe(paiement.getTypeFacturation())));
            details.addCell(kvCell("Montant Paye", formatDevise(paiement.getMontantPaye(), paiement.getDevise() != null ? paiement.getDevise().getSymbole() : "")));
            details.addCell(kvCell("Montant (MGA)", formatMga(paiement.getMontantReferenceMga())));
            doc.add(details);
            doc.add(spacer());

            Paragraph note = new Paragraph("Ce document fait office de recu officiel Airline.", new Font(Font.HELVETICA, 10));
            note.setAlignment(Element.ALIGN_CENTER);
            doc.add(note);

            Paragraph footer = new Paragraph("Document genere le " + DT.format(LocalDateTime.now()), new Font(Font.HELVETICA, 8));
            footer.setAlignment(Element.ALIGN_CENTER);
            doc.add(footer);
        }, fileTitle);
    }

    private interface PdfBody {
        void render(Document doc) throws DocumentException;
    }

    private byte[] buildPdf(PdfBody body, String title) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 36, 36, 42, 42);
            PdfWriter.getInstance(document, out);
            document.addTitle(title);
            document.open();
            body.render(document);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la generation PDF", e);
        }
    }

    private void addHeader(Document doc, String brand, String subtitle) throws DocumentException {
        Font brandFont = new Font(Font.HELVETICA, 20, Font.BOLD);
        Font subFont = new Font(Font.HELVETICA, 12, Font.BOLD);

        Paragraph p1 = new Paragraph(brand, brandFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        doc.add(p1);

        Paragraph p2 = new Paragraph(subtitle, subFont);
        p2.setAlignment(Element.ALIGN_CENTER);
        doc.add(p2);

        doc.add(spacer());
    }

    private Paragraph spacer() {
        return new Paragraph(" ");
    }

    private PdfPCell kvCell(String k, Object v) {
        Font key = new Font(Font.HELVETICA, 9, Font.BOLD);
        Font val = new Font(Font.HELVETICA, 10);

        PdfPTable inner = new PdfPTable(1);
        inner.setWidthPercentage(100);
        PdfPCell c1 = new PdfPCell(new Phrase(k, key));
        c1.setBorder(Rectangle.NO_BORDER);
        PdfPCell c2 = new PdfPCell(new Phrase(v != null ? String.valueOf(v) : "", val));
        c2.setBorder(Rectangle.NO_BORDER);
        inner.addCell(c1);
        inner.addCell(c2);

        PdfPCell cell = new PdfPCell(inner);
        cell.setPadding(8);
        cell.setBorderColor(new Color(220, 220, 220));
        cell.setBorderWidth(1);
        return cell;
    }

    private String formatMga(BigDecimal amount) {
        BigDecimal a = amount != null ? amount : BigDecimal.ZERO;
        return a.toPlainString() + " MGA";
    }

    private String formatDevise(BigDecimal amount, String symbole) {
        BigDecimal a = amount != null ? amount : BigDecimal.ZERO;
        String s = symbole != null ? symbole : "";
        return a.toPlainString() + " " + s;
    }

    private String safe(Object v) {
        return v != null ? String.valueOf(v) : "";
    }
}
