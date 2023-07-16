package com.itsandra.etudiantetranger.repository.rowmapper;

import com.itsandra.etudiantetranger.domain.Pays;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pays}, with proper type conversions.
 */
@Service
public class PaysRowMapper implements BiFunction<Row, String, Pays> {

    private final ColumnConverter converter;

    public PaysRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pays} stored in the database.
     */
    @Override
    public Pays apply(Row row, String prefix) {
        Pays entity = new Pays();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNomPays(converter.fromRow(row, prefix + "_nom_pays", String.class));
        return entity;
    }
}
