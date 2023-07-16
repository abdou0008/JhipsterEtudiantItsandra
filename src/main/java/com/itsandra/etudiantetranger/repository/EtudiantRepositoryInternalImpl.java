package com.itsandra.etudiantetranger.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import com.itsandra.etudiantetranger.domain.Etudiant;
import com.itsandra.etudiantetranger.repository.rowmapper.EtudiantRowMapper;
import com.itsandra.etudiantetranger.repository.rowmapper.FiliereRowMapper;
import com.itsandra.etudiantetranger.repository.rowmapper.NiveauRowMapper;
import com.itsandra.etudiantetranger.repository.rowmapper.PaysRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Etudiant entity.
 */
@SuppressWarnings("unused")
class EtudiantRepositoryInternalImpl extends SimpleR2dbcRepository<Etudiant, Long> implements EtudiantRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final NiveauRowMapper niveauMapper;
    private final FiliereRowMapper filiereMapper;
    private final PaysRowMapper paysMapper;
    private final EtudiantRowMapper etudiantMapper;

    private static final Table entityTable = Table.aliased("etudiant", EntityManager.ENTITY_ALIAS);
    private static final Table nomNiveauTable = Table.aliased("niveau", "nomNiveau");
    private static final Table nomFiliereTable = Table.aliased("filiere", "nomFiliere");
    private static final Table nomPaysTable = Table.aliased("pays", "nomPays");

    public EtudiantRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        NiveauRowMapper niveauMapper,
        FiliereRowMapper filiereMapper,
        PaysRowMapper paysMapper,
        EtudiantRowMapper etudiantMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Etudiant.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.niveauMapper = niveauMapper;
        this.filiereMapper = filiereMapper;
        this.paysMapper = paysMapper;
        this.etudiantMapper = etudiantMapper;
    }

    @Override
    public Flux<Etudiant> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Etudiant> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Etudiant> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = EtudiantSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(NiveauSqlHelper.getColumns(nomNiveauTable, "nomNiveau"));
        columns.addAll(FiliereSqlHelper.getColumns(nomFiliereTable, "nomFiliere"));
        columns.addAll(PaysSqlHelper.getColumns(nomPaysTable, "nomPays"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(nomNiveauTable)
            .on(Column.create("nom_niveau_id", entityTable))
            .equals(Column.create("id", nomNiveauTable))
            .leftOuterJoin(nomFiliereTable)
            .on(Column.create("nom_filiere_id", entityTable))
            .equals(Column.create("id", nomFiliereTable))
            .leftOuterJoin(nomPaysTable)
            .on(Column.create("nom_pays_id", entityTable))
            .equals(Column.create("id", nomPaysTable));

        String select = entityManager.createSelect(selectFrom, Etudiant.class, pageable, criteria);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Etudiant> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Etudiant> findById(Long id) {
        return createQuery(null, where(EntityManager.ENTITY_ALIAS + ".id").is(id)).one();
    }

    private Etudiant process(Row row, RowMetadata metadata) {
        Etudiant entity = etudiantMapper.apply(row, "e");
        entity.setNomNiveau(niveauMapper.apply(row, "nomNiveau"));
        entity.setNomFiliere(filiereMapper.apply(row, "nomFiliere"));
        entity.setNomPays(paysMapper.apply(row, "nomPays"));
        return entity;
    }

    @Override
    public <S extends Etudiant> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
