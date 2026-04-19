package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "type_passager")
public class TypePassager {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_type_passager")
    private Integer idTypePassager;

    @NotBlank(message = "Le nom du type passager est obligatoire")
    @Column(name = "nom_type", nullable = false, unique = true, length = 50)
    private String nomType;

    public TypePassager() {
    }

    public TypePassager(String nomType) {
        this.nomType = nomType;
    }

    public Integer getIdTypePassager() {
        return idTypePassager;
    }

    public void setIdTypePassager(Integer idTypePassager) {
        this.idTypePassager = idTypePassager;
    }

    public String getNomType() {
        return nomType;
    }

    public void setNomType(String nomType) {
        this.nomType = nomType;
    }
}
