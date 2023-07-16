package com.itsandra.etudiantetranger.domain;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Etudiant.
 */
@Table("etudiant")
public class Etudiant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nom")
    private String nom;

    @Column("prenom")
    private String prenom;

    @Column("matricule")
    private Long matricule;

    @Column("date")
    private LocalDate date;

    @Transient
    private Niveau nomNiveau;

    @Transient
    private Filiere nomFiliere;

    @Transient
    private Pays nomPays;

    @Column("nom_niveau_id")
    private Long nomNiveauId;

    @Column("nom_filiere_id")
    private Long nomFiliereId;

    @Column("nom_pays_id")
    private Long nomPaysId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Etudiant id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public Etudiant nom(String nom) {
        this.setNom(nom);
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public Etudiant prenom(String prenom) {
        this.setPrenom(prenom);
        return this;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Long getMatricule() {
        return this.matricule;
    }

    public Etudiant matricule(Long matricule) {
        this.setMatricule(matricule);
        return this;
    }

    public void setMatricule(Long matricule) {
        this.matricule = matricule;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public Etudiant date(LocalDate date) {
        this.setDate(date);
        return this;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Niveau getNomNiveau() {
        return this.nomNiveau;
    }

    public void setNomNiveau(Niveau niveau) {
        this.nomNiveau = niveau;
        this.nomNiveauId = niveau != null ? niveau.getId() : null;
    }

    public Etudiant nomNiveau(Niveau niveau) {
        this.setNomNiveau(niveau);
        return this;
    }

    public Filiere getNomFiliere() {
        return this.nomFiliere;
    }

    public void setNomFiliere(Filiere filiere) {
        this.nomFiliere = filiere;
        this.nomFiliereId = filiere != null ? filiere.getId() : null;
    }

    public Etudiant nomFiliere(Filiere filiere) {
        this.setNomFiliere(filiere);
        return this;
    }

    public Pays getNomPays() {
        return this.nomPays;
    }

    public void setNomPays(Pays pays) {
        this.nomPays = pays;
        this.nomPaysId = pays != null ? pays.getId() : null;
    }

    public Etudiant nomPays(Pays pays) {
        this.setNomPays(pays);
        return this;
    }

    public Long getNomNiveauId() {
        return this.nomNiveauId;
    }

    public void setNomNiveauId(Long niveau) {
        this.nomNiveauId = niveau;
    }

    public Long getNomFiliereId() {
        return this.nomFiliereId;
    }

    public void setNomFiliereId(Long filiere) {
        this.nomFiliereId = filiere;
    }

    public Long getNomPaysId() {
        return this.nomPaysId;
    }

    public void setNomPaysId(Long pays) {
        this.nomPaysId = pays;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Etudiant)) {
            return false;
        }
        return id != null && id.equals(((Etudiant) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Etudiant{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            ", prenom='" + getPrenom() + "'" +
            ", matricule=" + getMatricule() +
            ", date='" + getDate() + "'" +
            "}";
    }
}
