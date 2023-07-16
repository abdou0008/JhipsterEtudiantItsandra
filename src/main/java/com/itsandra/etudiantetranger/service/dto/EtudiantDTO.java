package com.itsandra.etudiantetranger.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.itsandra.etudiantetranger.domain.Etudiant} entity.
 */
public class EtudiantDTO implements Serializable {

    private Long id;

    private String nom;

    private String prenom;

    private Long matricule;

    private LocalDate date;

    private NiveauDTO nomNiveau;

    private FiliereDTO nomFiliere;

    private PaysDTO nomPays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getMatricule() {
        return matricule;
    }

    public void setMatricule(Long matricule) {
        this.matricule = matricule;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public NiveauDTO getNomNiveau() {
        return nomNiveau;
    }

    public void setNomNiveau(NiveauDTO nomNiveau) {
        this.nomNiveau = nomNiveau;
    }

    public FiliereDTO getNomFiliere() {
        return nomFiliere;
    }

    public void setNomFiliere(FiliereDTO nomFiliere) {
        this.nomFiliere = nomFiliere;
    }

    public PaysDTO getNomPays() {
        return nomPays;
    }

    public void setNomPays(PaysDTO nomPays) {
        this.nomPays = nomPays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EtudiantDTO)) {
            return false;
        }

        EtudiantDTO etudiantDTO = (EtudiantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, etudiantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EtudiantDTO{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", matricule=" + getMatricule() +
            ", date='" + getDate() + "'" +
            ", nomNiveau=" + getNomNiveau() +
            ", nomFiliere=" + getNomFiliere() +
            ", nomPays=" + getNomPays() +
            "}";
    }
}
