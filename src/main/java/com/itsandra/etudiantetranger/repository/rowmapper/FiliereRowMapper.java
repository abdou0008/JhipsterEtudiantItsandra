package com.itsandra.etudiantetranger.repository.rowmapper;

import com.itsandra.etudiantetranger.domain.Filiere;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Filiere}, with proper type conversions.
 */
@Service
public class FiliereRowMapper implements BiFunction<Row, String, Filiere> {

    private final ColumnConverter converter;

    public FiliereRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Filiere} stored in the database.
     */
    @Override
    public Filiere apply(Row row, String prefix) {
        Filiere entity = new Filiere();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNomFiliere(converter.fromRow(row, prefix + "_nom_filiere", String.class));
        return entity;
    }
}
