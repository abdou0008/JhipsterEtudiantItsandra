package com.itsandra.etudiantetranger.repository;

import com.itsandra.etudiantetranger.domain.Etudiant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Etudiant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EtudiantRepository extends ReactiveCrudRepository<Etudiant, Long>, EtudiantRepositoryInternal {
    Flux<Etudiant> findAllBy(Pageable pageable);

    @Query("SELECT * FROM etudiant entity WHERE entity.nom_niveau_id = :id")
    Flux<Etudiant> findByNomNiveau(Long id);

    @Query("SELECT * FROM etudiant entity WHERE entity.nom_niveau_id IS NULL")
    Flux<Etudiant> findAllWhereNomNiveauIsNull();

    @Query("SELECT * FROM etudiant entity WHERE entity.nom_filiere_id = :id")
    Flux<Etudiant> findByNomFiliere(Long id);

    @Query("SELECT * FROM etudiant entity WHERE entity.nom_filiere_id IS NULL")
    Flux<Etudiant> findAllWhereNomFiliereIsNull();

    @Query("SELECT * FROM etudiant entity WHERE entity.nom_pays_id = :id")
    Flux<Etudiant> findByNomPays(Long id);

    @Query("SELECT * FROM etudiant entity WHERE entity.nom_pays_id IS NULL")
    Flux<Etudiant> findAllWhereNomPaysIsNull();

    @Override
    <S extends Etudiant> Mono<S> save(S entity);

    @Override
    Flux<Etudiant> findAll();

    @Override
    Mono<Etudiant> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EtudiantRepositoryInternal {
    <S extends Etudiant> Mono<S> save(S entity);

    Flux<Etudiant> findAllBy(Pageable pageable);

    Flux<Etudiant> findAll();

    Mono<Etudiant> findById(Long id);

    Flux<Etudiant> findAllBy(Pageable pageable, Criteria criteria);
}
