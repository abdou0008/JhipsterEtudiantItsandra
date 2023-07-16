package com.itsandra.etudiantetranger.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.itsandra.etudiantetranger.domain.Niveau} entity.
 */
public class NiveauDTO implements Serializable {

    private Long id;

    private String nomNiveau;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomNiveau() {
        return nomNiveau;
    }

    public void setNomNiveau(String nomNiveau) {
        this.nomNiveau = nomNiveau;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NiveauDTO)) {
            return false;
        }

        NiveauDTO niveauDTO = (NiveauDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, niveauDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "NiveauDTO{" +
            "id=" + getId() +
            ", nomNiveau='" + getNomNiveau() + "'" +
            "}";
    }
}
