package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etat_avion")
public class EtatAvion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etat_avion")
    private Long idEtatAvion;
    
    @NotNull(message = "Le code etat est obligatoire")
    @Column(name = "code_etat", unique = true, nullable = false)
    private Integer codeEtat;
    
    @NotNull(message = "La description est obligatoire")
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    
    @OneToMany(mappedBy = "etatAvion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatAvion> historiqueEtats = new ArrayList<>();
    
    public EtatAvion() {}
    
    public EtatAvion(Integer codeEtat, String description) {
        this.codeEtat = codeEtat;
        this.description = description;
    }
    
    // Getters et Setters
    public Long getIdEtatAvion() {
        return idEtatAvion;
    }
    
    public void setIdEtatAvion(Long idEtatAvion) {
        this.idEtatAvion = idEtatAvion;
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
    
    public List<HistoriqueEtatAvion> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatAvion> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
}

