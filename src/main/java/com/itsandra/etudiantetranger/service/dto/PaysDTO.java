package com.itsandra.etudiantetranger.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.itsandra.etudiantetranger.domain.Pays} entity.
 */
public class PaysDTO implements Serializable {

    private Long id;

    private String nomPays;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomPays() {
        return nomPays;
    }

    public void setNomPays(String nomPays) {
        this.nomPays = nomPays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaysDTO)) {
            return false;
        }

        PaysDTO paysDTO = (PaysDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, paysDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PaysDTO{" +
            "id=" + getId() +
            ", nomPays='" + getNomPays() + "'" +
            "}";
    }
}
