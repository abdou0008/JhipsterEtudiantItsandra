package com.itsandra.etudiantetranger.repository;

import com.itsandra.etudiantetranger.domain.Niveau;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Niveau entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NiveauRepository extends ReactiveCrudRepository<Niveau, Long>, NiveauRepositoryInternal {
    Flux<Niveau> findAllBy(Pageable pageable);

    @Override
    <S extends Niveau> Mono<S> save(S entity);

    @Override
    Flux<Niveau> findAll();

    @Override
    Mono<Niveau> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface NiveauRepositoryInternal {
    <S extends Niveau> Mono<S> save(S entity);

    Flux<Niveau> findAllBy(Pageable pageable);

    Flux<Niveau> findAll();

    Mono<Niveau> findById(Long id);

    Flux<Niveau> findAllBy(Pageable pageable, Criteria criteria);
}
