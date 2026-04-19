package com.compagnie.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VolCa {
    private Long idVol;
    private String aeroportDepartCode;
    private String aeroportArriveeCode;
    private String avionImmatriculation;
    private LocalDateTime dateVol;
    private BigDecimal caBillets;
    private BigDecimal caPublicites;
    private BigDecimal caProduitsExtraTheorique;
    private BigDecimal montantPayePublicites;
    private BigDecimal resteAPayerPublicites;
    private BigDecimal caTotal;

    public VolCa(Long idVol, String aeroportDepartCode, String aeroportArriveeCode,
                 String avionImmatriculation, LocalDateTime dateVol,
                 BigDecimal caBillets, BigDecimal caPublicites,
                 BigDecimal caProduitsExtraTheorique,
                 BigDecimal montantPayePublicites, BigDecimal resteAPayerPublicites) {
        this.idVol = idVol;
        this.aeroportDepartCode = aeroportDepartCode;
        this.aeroportArriveeCode = aeroportArriveeCode;
        this.avionImmatriculation = avionImmatriculation;
        this.dateVol = dateVol;
        this.caBillets = caBillets == null ? BigDecimal.ZERO : caBillets;
        this.caPublicites = caPublicites == null ? BigDecimal.ZERO : caPublicites;
        this.caProduitsExtraTheorique = caProduitsExtraTheorique == null ? BigDecimal.ZERO : caProduitsExtraTheorique;
        this.montantPayePublicites = montantPayePublicites == null ? BigDecimal.ZERO : montantPayePublicites;
        this.resteAPayerPublicites = resteAPayerPublicites == null ? BigDecimal.ZERO : resteAPayerPublicites;
        this.caTotal = this.caBillets.add(this.caPublicites).add(this.caProduitsExtraTheorique);
    }

    public Long getIdVol() { return idVol; }
    public String getAeroportDepartCode() { return aeroportDepartCode; }
    public String getAeroportArriveeCode() { return aeroportArriveeCode; }
    public String getAvionImmatriculation() { return avionImmatriculation; }
    public LocalDateTime getDateVol() { return dateVol; }
    public BigDecimal getCaBillets() { return caBillets; }
    public BigDecimal getCaPublicites() { return caPublicites; }
    public BigDecimal getCaProduitsExtraTheorique() { return caProduitsExtraTheorique; }
    public BigDecimal getMontantPayePublicites() { return montantPayePublicites; }
    public BigDecimal getResteAPayerPublicites() { return resteAPayerPublicites; }
    public BigDecimal getCaTotal() { return caTotal; }
}
