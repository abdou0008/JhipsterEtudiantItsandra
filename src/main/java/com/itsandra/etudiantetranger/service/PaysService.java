package com.itsandra.etudiantetranger.service;

import com.itsandra.etudiantetranger.domain.Pays;
import com.itsandra.etudiantetranger.repository.PaysRepository;
import com.itsandra.etudiantetranger.service.dto.PaysDTO;
import com.itsandra.etudiantetranger.service.mapper.PaysMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Pays}.
 */
@Service
@Transactional
public class PaysService {

    private final Logger log = LoggerFactory.getLogger(PaysService.class);

    private final PaysRepository paysRepository;

    private final PaysMapper paysMapper;

    public PaysService(PaysRepository paysRepository, PaysMapper paysMapper) {
        this.paysRepository = paysRepository;
        this.paysMapper = paysMapper;
    }

    /**
     * Save a pays.
     *
     * @param paysDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaysDTO> save(PaysDTO paysDTO) {
        log.debug("Request to save Pays : {}", paysDTO);
        return paysRepository.save(paysMapper.toEntity(paysDTO)).map(paysMapper::toDto);
    }

    /**
     * Partially update a pays.
     *
     * @param paysDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PaysDTO> partialUpdate(PaysDTO paysDTO) {
        log.debug("Request to partially update Pays : {}", paysDTO);

        return paysRepository
            .findById(paysDTO.getId())
            .map(existingPays -> {
                paysMapper.partialUpdate(existingPays, paysDTO);

                return existingPays;
            })
            .flatMap(paysRepository::save)
            .map(paysMapper::toDto);
    }

    /**
     * Get all the pays.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaysDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Pays");
        return paysRepository.findAllBy(pageable).map(paysMapper::toDto);
    }

    /**
     * Returns the number of pays available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return paysRepository.count();
    }

    /**
     * Get one pays by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PaysDTO> findOne(Long id) {
        log.debug("Request to get Pays : {}", id);
        return paysRepository.findById(id).map(paysMapper::toDto);
    }

    /**
     * Delete the pays by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Pays : {}", id);
        return paysRepository.deleteById(id);
    }
}
