package com.itsandra.etudiantetranger.repository.rowmapper;

import com.itsandra.etudiantetranger.domain.Niveau;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Niveau}, with proper type conversions.
 */
@Service
public class NiveauRowMapper implements BiFunction<Row, String, Niveau> {

    private final ColumnConverter converter;

    public NiveauRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Niveau} stored in the database.
     */
    @Override
    public Niveau apply(Row row, String prefix) {
        Niveau entity = new Niveau();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNomNiveau(converter.fromRow(row, prefix + "_nom_niveau", String.class));
        return entity;
    }
}
