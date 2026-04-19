package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "etat_cargo")
public class EtatCargo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_etat_cargo")
    private Long idEtatCargo;
    
    @NotNull(message = "Le code etat est obligatoire")
    @Column(name = "code_etat", unique = true, nullable = false)
    private Integer codeEtat;
    
    @NotNull(message = "La description est obligatoire")
    @Column(name = "description", nullable = false, length = 100)
    private String description;
    
    @OneToMany(mappedBy = "etatCargo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoriqueEtatCargo> historiqueEtats = new ArrayList<>();
    
    public EtatCargo() {}
    
    public EtatCargo(Integer codeEtat, String description) {
        this.codeEtat = codeEtat;
        this.description = description;
    }
    
    // Getters et Setters
    public Long getIdEtatCargo() {
        return idEtatCargo;
    }
    
    public void setIdEtatCargo(Long idEtatCargo) {
        this.idEtatCargo = idEtatCargo;
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
    
    public List<HistoriqueEtatCargo> getHistoriqueEtats() {
        return historiqueEtats;
    }
    
    public void setHistoriqueEtats(List<HistoriqueEtatCargo> historiqueEtats) {
        this.historiqueEtats = historiqueEtats;
    }
}

