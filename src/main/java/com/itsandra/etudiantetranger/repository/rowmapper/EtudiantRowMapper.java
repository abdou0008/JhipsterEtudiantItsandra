package com.itsandra.etudiantetranger.repository.rowmapper;

import com.itsandra.etudiantetranger.domain.Etudiant;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Etudiant}, with proper type conversions.
 */
@Service
public class EtudiantRowMapper implements BiFunction<Row, String, Etudiant> {

    private final ColumnConverter converter;

    public EtudiantRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Etudiant} stored in the database.
     */
    @Override
    public Etudiant apply(Row row, String prefix) {
        Etudiant entity = new Etudiant();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNom(converter.fromRow(row, prefix + "_nom", String.class));
        entity.setPrenom(converter.fromRow(row, prefix + "_prenom", String.class));
        entity.setMatricule(converter.fromRow(row, prefix + "_matricule", Long.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", LocalDate.class));
        entity.setNomNiveauId(converter.fromRow(row, prefix + "_nom_niveau_id", Long.class));
        entity.setNomFiliereId(converter.fromRow(row, prefix + "_nom_filiere_id", Long.class));
        entity.setNomPaysId(converter.fromRow(row, prefix + "_nom_pays_id", Long.class));
        return entity;
    }
}
