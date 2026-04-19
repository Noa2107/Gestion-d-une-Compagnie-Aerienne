package com.compagnie.service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SocietePaiementVolLigne {
    private Long idVol;
    private String aeroportDepartCode;
    private String aeroportArriveeCode;
    private String avionImmatriculation;
    private LocalDateTime dateVol;
    private BigDecimal montantDiffusionVol;
    private BigDecimal montantPayeVol;
    private BigDecimal montantResteVol;
    private BigDecimal pourcentagePaye; // 0..1

    public SocietePaiementVolLigne(Long idVol, String aeroportDepartCode, String aeroportArriveeCode,
                                   String avionImmatriculation, LocalDateTime dateVol,
                                   BigDecimal montantDiffusionVol, BigDecimal pourcentagePaye) {
        this.idVol = idVol;
        this.aeroportDepartCode = aeroportDepartCode;
        this.aeroportArriveeCode = aeroportArriveeCode;
        this.avionImmatriculation = avionImmatriculation;
        this.dateVol = dateVol;
        this.montantDiffusionVol = montantDiffusionVol == null ? BigDecimal.ZERO : montantDiffusionVol;
        this.pourcentagePaye = pourcentagePaye == null ? BigDecimal.ZERO : pourcentagePaye;
        this.montantPayeVol = this.montantDiffusionVol.multiply(this.pourcentagePaye);
        this.montantResteVol = this.montantDiffusionVol.subtract(this.montantPayeVol);
    }

    public Long getIdVol() { return idVol; }
    public String getAeroportDepartCode() { return aeroportDepartCode; }
    public String getAeroportArriveeCode() { return aeroportArriveeCode; }
    public String getAvionImmatriculation() { return avionImmatriculation; }
    public LocalDateTime getDateVol() { return dateVol; }
    public BigDecimal getMontantDiffusionVol() { return montantDiffusionVol; }
    public BigDecimal getMontantPayeVol() { return montantPayeVol; }
    public BigDecimal getMontantResteVol() { return montantResteVol; }
    public BigDecimal getPourcentagePaye() { return pourcentagePaye; }
}
