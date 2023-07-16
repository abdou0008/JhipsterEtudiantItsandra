package com.itsandra.etudiantetranger.repository;

import com.itsandra.etudiantetranger.domain.Filiere;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Filiere entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FiliereRepository extends ReactiveCrudRepository<Filiere, Long>, FiliereRepositoryInternal {
    Flux<Filiere> findAllBy(Pageable pageable);

    @Override
    <S extends Filiere> Mono<S> save(S entity);

    @Override
    Flux<Filiere> findAll();

    @Override
    Mono<Filiere> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface FiliereRepositoryInternal {
    <S extends Filiere> Mono<S> save(S entity);

    Flux<Filiere> findAllBy(Pageable pageable);

    Flux<Filiere> findAll();

    Mono<Filiere> findById(Long id);

    Flux<Filiere> findAllBy(Pageable pageable, Criteria criteria);
}
