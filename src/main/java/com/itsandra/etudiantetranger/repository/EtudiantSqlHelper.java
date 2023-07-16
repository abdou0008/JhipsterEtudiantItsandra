package com.itsandra.etudiantetranger.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class EtudiantSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nom", table, columnPrefix + "_nom"));
        columns.add(Column.aliased("prenom", table, columnPrefix + "_prenom"));
        columns.add(Column.aliased("matricule", table, columnPrefix + "_matricule"));
        columns.add(Column.aliased("date", table, columnPrefix + "_date"));

        columns.add(Column.aliased("nom_niveau_id", table, columnPrefix + "_nom_niveau_id"));
        columns.add(Column.aliased("nom_filiere_id", table, columnPrefix + "_nom_filiere_id"));
        columns.add(Column.aliased("nom_pays_id", table, columnPrefix + "_nom_pays_id"));
        return columns;
    }
}
