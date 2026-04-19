package com.compagnie.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "passager")
public class Passager {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_passager")
    private Long idPassager;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Column(name = "nom", nullable = false, length = 100)
    private String nom;
    
    @NotBlank(message = "Le prenom est obligatoire")
    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;
    
    @Column(name = "date_naissance")
    private LocalDate dateNaissance;
    
    @Column(name = "sexe", length = 10)
    private String sexe;
    
    @Column(name = "nationalite", length = 50)
    private String nationalite;
    
    @Column(name = "numero_passeport", length = 50)
    private String numeroPasseport;
    
    @Column(name = "contact", length = 50)
    private String contact;
    
    @Email(message = "L'email doit etre valide")
    @Column(name = "email", length = 100)
    private String email;
    
    @OneToMany(mappedBy = "passager", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();
    
    public Passager() {}
    
    public Passager(String nom, String prenom, String email, String contact) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.contact = contact;
    }
    
    // Getters et Setters
    public Long getIdPassager() {
        return idPassager;
    }
    
    public void setIdPassager(Long idPassager) {
        this.idPassager = idPassager;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getPrenom() {
        return prenom;
    }
    
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }
    
    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }
    
    public String getSexe() {
        return sexe;
    }
    
    public void setSexe(String sexe) {
        this.sexe = sexe;
    }
    
    public String getNationalite() {
        return nationalite;
    }
    
    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }
    
    public String getNumeroPasseport() {
        return numeroPasseport;
    }
    
    public void setNumeroPasseport(String numeroPasseport) {
        this.numeroPasseport = numeroPasseport;
    }
    
    public String getContact() {
        return contact;
    }
    
    public void setContact(String contact) {
        this.contact = contact;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public List<Reservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
}

