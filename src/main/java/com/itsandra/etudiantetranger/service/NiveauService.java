package com.itsandra.etudiantetranger.service;

import com.itsandra.etudiantetranger.domain.Niveau;
import com.itsandra.etudiantetranger.repository.NiveauRepository;
import com.itsandra.etudiantetranger.service.dto.NiveauDTO;
import com.itsandra.etudiantetranger.service.mapper.NiveauMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Niveau}.
 */
@Service
@Transactional
public class NiveauService {

    private final Logger log = LoggerFactory.getLogger(NiveauService.class);

    private final NiveauRepository niveauRepository;

    private final NiveauMapper niveauMapper;

    public NiveauService(NiveauRepository niveauRepository, NiveauMapper niveauMapper) {
        this.niveauRepository = niveauRepository;
        this.niveauMapper = niveauMapper;
    }

    /**
     * Save a niveau.
     *
     * @param niveauDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<NiveauDTO> save(NiveauDTO niveauDTO) {
        log.debug("Request to save Niveau : {}", niveauDTO);
        return niveauRepository.save(niveauMapper.toEntity(niveauDTO)).map(niveauMapper::toDto);
    }

    /**
     * Partially update a niveau.
     *
     * @param niveauDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<NiveauDTO> partialUpdate(NiveauDTO niveauDTO) {
        log.debug("Request to partially update Niveau : {}", niveauDTO);

        return niveauRepository
            .findById(niveauDTO.getId())
            .map(existingNiveau -> {
                niveauMapper.partialUpdate(existingNiveau, niveauDTO);

                return existingNiveau;
            })
            .flatMap(niveauRepository::save)
            .map(niveauMapper::toDto);
    }

    /**
     * Get all the niveaus.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<NiveauDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Niveaus");
        return niveauRepository.findAllBy(pageable).map(niveauMapper::toDto);
    }

    /**
     * Returns the number of niveaus available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return niveauRepository.count();
    }

    /**
     * Get one niveau by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<NiveauDTO> findOne(Long id) {
        log.debug("Request to get Niveau : {}", id);
        return niveauRepository.findById(id).map(niveauMapper::toDto);
    }

    /**
     * Delete the niveau by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Niveau : {}", id);
        return niveauRepository.deleteById(id);
    }
}
