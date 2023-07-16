package com.itsandra.etudiantetranger.web.rest;

import com.itsandra.etudiantetranger.repository.NiveauRepository;
import com.itsandra.etudiantetranger.service.NiveauService;
import com.itsandra.etudiantetranger.service.dto.NiveauDTO;
import com.itsandra.etudiantetranger.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.itsandra.etudiantetranger.domain.Niveau}.
 */
@RestController
@RequestMapping("/api")
public class NiveauResource {

    private final Logger log = LoggerFactory.getLogger(NiveauResource.class);

    private static final String ENTITY_NAME = "niveau";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NiveauService niveauService;

    private final NiveauRepository niveauRepository;

    public NiveauResource(NiveauService niveauService, NiveauRepository niveauRepository) {
        this.niveauService = niveauService;
        this.niveauRepository = niveauRepository;
    }

    /**
     * {@code POST  /niveaus} : Create a new niveau.
     *
     * @param niveauDTO the niveauDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new niveauDTO, or with status {@code 400 (Bad Request)} if the niveau has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/niveaus")
    public Mono<ResponseEntity<NiveauDTO>> createNiveau(@RequestBody NiveauDTO niveauDTO) throws URISyntaxException {
        log.debug("REST request to save Niveau : {}", niveauDTO);
        if (niveauDTO.getId() != null) {
            throw new BadRequestAlertException("A new niveau cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return niveauService
            .save(niveauDTO)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/niveaus/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /niveaus/:id} : Updates an existing niveau.
     *
     * @param id the id of the niveauDTO to save.
     * @param niveauDTO the niveauDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated niveauDTO,
     * or with status {@code 400 (Bad Request)} if the niveauDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the niveauDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/niveaus/{id}")
    public Mono<ResponseEntity<NiveauDTO>> updateNiveau(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody NiveauDTO niveauDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Niveau : {}, {}", id, niveauDTO);
        if (niveauDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, niveauDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return niveauRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return niveauService
                    .save(niveauDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /niveaus/:id} : Partial updates given fields of an existing niveau, field will ignore if it is null
     *
     * @param id the id of the niveauDTO to save.
     * @param niveauDTO the niveauDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated niveauDTO,
     * or with status {@code 400 (Bad Request)} if the niveauDTO is not valid,
     * or with status {@code 404 (Not Found)} if the niveauDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the niveauDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/niveaus/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<NiveauDTO>> partialUpdateNiveau(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody NiveauDTO niveauDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Niveau partially : {}, {}", id, niveauDTO);
        if (niveauDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, niveauDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return niveauRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<NiveauDTO> result = niveauService.partialUpdate(niveauDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /niveaus} : get all the niveaus.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of niveaus in body.
     */
    @GetMapping("/niveaus")
    public Mono<ResponseEntity<List<NiveauDTO>>> getAllNiveaus(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of Niveaus");
        return niveauService
            .countAll()
            .zipWith(niveauService.findAll(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /niveaus/:id} : get the "id" niveau.
     *
     * @param id the id of the niveauDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the niveauDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/niveaus/{id}")
    public Mono<ResponseEntity<NiveauDTO>> getNiveau(@PathVariable Long id) {
        log.debug("REST request to get Niveau : {}", id);
        Mono<NiveauDTO> niveauDTO = niveauService.findOne(id);
        return ResponseUtil.wrapOrNotFound(niveauDTO);
    }

    /**
     * {@code DELETE  /niveaus/:id} : delete the "id" niveau.
     *
     * @param id the id of the niveauDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/niveaus/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteNiveau(@PathVariable Long id) {
        log.debug("REST request to delete Niveau : {}", id);
        return niveauService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
