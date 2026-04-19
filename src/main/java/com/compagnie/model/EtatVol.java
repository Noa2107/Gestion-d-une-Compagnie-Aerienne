package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etat_vol")
public class EtatVol {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etat_vol")
    private Long idEtatVol;
    
    @NotNull(message = "Le code etat est obligatoire")
    @Column(name = "code_etat", unique = true, nullable = false)
    private Integer codeEtat;
    
    @NotNull(message = "La description est obligatoire")
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    
    @OneToMany(mappedBy = "etatVol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatVol> historiqueEtats = new ArrayList<>();
    
    public EtatVol() {}
    
    public EtatVol(Integer codeEtat, String description) {
        this.codeEtat = codeEtat;
        this.description = description;
    }
    
    // Getters et Setters
    public Long getIdEtatVol() {
        return idEtatVol;
    }
    
    public void setIdEtatVol(Long idEtatVol) {
        this.idEtatVol = idEtatVol;
    }
    
    public Integer getCodeEtat() {
        return codeEtat;
    }
    
    public void setCodeEtat(Integer codeEtat) {
        this.codeEtat = codeEtat;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<HistoriqueEtatVol> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatVol> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
}

