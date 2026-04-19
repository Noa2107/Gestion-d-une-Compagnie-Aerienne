package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "devise")
public class Devise {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devise")
    private Long idDevise;
    
    @NotBlank(message = "Le code devise est obligatoire")
    @Column(name = "code_devise", unique = true, nullable = false, length = 10)
    private String codeDevise;
    
    @Column(name = "nom_devise", length = 50)
    private String nomDevise;
    
    @Column(name = "symbole", length = 10)
    private String symbole;
    
    @Column(name = "est_reference")
    private Boolean estReference = false;
    
    @OneToMany(mappedBy = "deviseSource", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TauxChange> tauxChangesSource = new ArrayList<>();
    
    @OneToMany(mappedBy = "deviseCible", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TauxChange> tauxChangesCible = new ArrayList<>();
    
    public Devise() {}
    
    public Devise(String codeDevise, String nomDevise, String symbole, Boolean estReference) {
        this.codeDevise = codeDevise;
        this.nomDevise = nomDevise;
        this.symbole = symbole;
        this.estReference = estReference;
    }
    
    // Getters et Setters
    public Long getIdDevise() {
        return idDevise;
    }
    
    public void setIdDevise(Long idDevise) {
        this.idDevise = idDevise;
    }
    
    public String getCodeDevise() {
        return codeDevise;
    }
    
    public void setCodeDevise(String codeDevise) {
        this.codeDevise = codeDevise;
    }
    
    public String getNomDevise() {
        return nomDevise;
    }
    
    public void setNomDevise(String nomDevise) {
        this.nomDevise = nomDevise;
    }
    
    public String getSymbole() {
        return symbole;
    }
    
    public void setSymbole(String symbole) {
        this.symbole = symbole;
    }
    
    public Boolean getEstReference() {
        return estReference;
    }
    
    public void setEstReference(Boolean estReference) {
        this.estReference = estReference;
    }
    
    public List<TauxChange> getTauxChangesSource() {
        return tauxChangesSource;
    }
    
    public void setTauxChangesSource(List<TauxChange> tauxChangesSource) {
        this.tauxChangesSource = tauxChangesSource;
    }
    
    public List<TauxChange> getTauxChangesCible() {
        return tauxChangesCible;
    }
    
    public void setTauxChangesCible(List<TauxChange> tauxChangesCible) {
        this.tauxChangesCible = tauxChangesCible;
    }
}

