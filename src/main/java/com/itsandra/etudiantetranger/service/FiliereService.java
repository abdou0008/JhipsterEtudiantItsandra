package com.itsandra.etudiantetranger.service;

import com.itsandra.etudiantetranger.domain.Filiere;
import com.itsandra.etudiantetranger.repository.FiliereRepository;
import com.itsandra.etudiantetranger.service.dto.FiliereDTO;
import com.itsandra.etudiantetranger.service.mapper.FiliereMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Filiere}.
 */
@Service
@Transactional
public class FiliereService {

    private final Logger log = LoggerFactory.getLogger(FiliereService.class);

    private final FiliereRepository filiereRepository;

    private final FiliereMapper filiereMapper;

    public FiliereService(FiliereRepository filiereRepository, FiliereMapper filiereMapper) {
        this.filiereRepository = filiereRepository;
        this.filiereMapper = filiereMapper;
    }

    /**
     * Save a filiere.
     *
     * @param filiereDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<FiliereDTO> save(FiliereDTO filiereDTO) {
        log.debug("Request to save Filiere : {}", filiereDTO);
        return filiereRepository.save(filiereMapper.toEntity(filiereDTO)).map(filiereMapper::toDto);
    }

    /**
     * Partially update a filiere.
     *
     * @param filiereDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<FiliereDTO> partialUpdate(FiliereDTO filiereDTO) {
        log.debug("Request to partially update Filiere : {}", filiereDTO);

        return filiereRepository
            .findById(filiereDTO.getId())
            .map(existingFiliere -> {
                filiereMapper.partialUpdate(existingFiliere, filiereDTO);

                return existingFiliere;
            })
            .flatMap(filiereRepository::save)
            .map(filiereMapper::toDto);
    }

    /**
     * Get all the filieres.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<FiliereDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Filieres");
        return filiereRepository.findAllBy(pageable).map(filiereMapper::toDto);
    }

    /**
     * Returns the number of filieres available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return filiereRepository.count();
    }

    /**
     * Get one filiere by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<FiliereDTO> findOne(Long id) {
        log.debug("Request to get Filiere : {}", id);
        return filiereRepository.findById(id).map(filiereMapper::toDto);
    }

    /**
     * Delete the filiere by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Filiere : {}", id);
        return filiereRepository.deleteById(id);
    }
}
