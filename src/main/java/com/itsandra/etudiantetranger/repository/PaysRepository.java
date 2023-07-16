package com.itsandra.etudiantetranger.repository;

import com.itsandra.etudiantetranger.domain.Pays;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Pays entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaysRepository extends ReactiveCrudRepository<Pays, Long>, PaysRepositoryInternal {
    Flux<Pays> findAllBy(Pageable pageable);

    @Override
    <S extends Pays> Mono<S> save(S entity);

    @Override
    Flux<Pays> findAll();

    @Override
    Mono<Pays> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PaysRepositoryInternal {
    <S extends Pays> Mono<S> save(S entity);

    Flux<Pays> findAllBy(Pageable pageable);

    Flux<Pays> findAll();

    Mono<Pays> findById(Long id);

    Flux<Pays> findAllBy(Pageable pageable, Criteria criteria);
}
