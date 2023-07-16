package com.itsandra.etudiantetranger.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.itsandra.etudiantetranger.IntegrationTest;
import com.itsandra.etudiantetranger.domain.Niveau;
import com.itsandra.etudiantetranger.repository.EntityManager;
import com.itsandra.etudiantetranger.repository.NiveauRepository;
import com.itsandra.etudiantetranger.service.dto.NiveauDTO;
import com.itsandra.etudiantetranger.service.mapper.NiveauMapper;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link NiveauResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class NiveauResourceIT {

    private static final String DEFAULT_NOM_NIVEAU = "AAAAAAAAAA";
    private static final String UPDATED_NOM_NIVEAU = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/niveaus";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NiveauRepository niveauRepository;

    @Autowired
    private NiveauMapper niveauMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Niveau niveau;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Niveau createEntity(EntityManager em) {
        Niveau niveau = new Niveau().nomNiveau(DEFAULT_NOM_NIVEAU);
        return niveau;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Niveau createUpdatedEntity(EntityManager em) {
        Niveau niveau = new Niveau().nomNiveau(UPDATED_NOM_NIVEAU);
        return niveau;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Niveau.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        niveau = createEntity(em);
    }

    @Test
    void createNiveau() throws Exception {
        int databaseSizeBeforeCreate = niveauRepository.findAll().collectList().block().size();
        // Create the Niveau
        NiveauDTO niveauDTO = niveauMapper.toDto(niveau);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(niveauDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeCreate + 1);
        Niveau testNiveau = niveauList.get(niveauList.size() - 1);
        assertThat(testNiveau.getNomNiveau()).isEqualTo(DEFAULT_NOM_NIVEAU);
    }

    @Test
    void createNiveauWithExistingId() throws Exception {
        // Create the Niveau with an existing ID
        niveau.setId(1L);
        NiveauDTO niveauDTO = niveauMapper.toDto(niveau);

        int databaseSizeBeforeCreate = niveauRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(niveauDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllNiveaus() {
        // Initialize the database
        niveauRepository.save(niveau).block();

        // Get all the niveauList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(niveau.getId().intValue()))
            .jsonPath("$.[*].nomNiveau")
            .value(hasItem(DEFAULT_NOM_NIVEAU));
    }

    @Test
    void getNiveau() {
        // Initialize the database
        niveauRepository.save(niveau).block();

        // Get the niveau
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, niveau.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(niveau.getId().intValue()))
            .jsonPath("$.nomNiveau")
            .value(is(DEFAULT_NOM_NIVEAU));
    }

    @Test
    void getNonExistingNiveau() {
        // Get the niveau
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewNiveau() throws Exception {
        // Initialize the database
        niveauRepository.save(niveau).block();

        int databaseSizeBeforeUpdate = niveauRepository.findAll().collectList().block().size();

        // Update the niveau
        Niveau updatedNiveau = niveauRepository.findById(niveau.getId()).block();
        updatedNiveau.nomNiveau(UPDATED_NOM_NIVEAU);
        NiveauDTO niveauDTO = niveauMapper.toDto(updatedNiveau);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, niveauDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(niveauDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
        Niveau testNiveau = niveauList.get(niveauList.size() - 1);
        assertThat(testNiveau.getNomNiveau()).isEqualTo(UPDATED_NOM_NIVEAU);
    }

    @Test
    void putNonExistingNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().collectList().block().size();
        niveau.setId(count.incrementAndGet());

        // Create the Niveau
        NiveauDTO niveauDTO = niveauMapper.toDto(niveau);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, niveauDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(niveauDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().collectList().block().size();
        niveau.setId(count.incrementAndGet());

        // Create the Niveau
        NiveauDTO niveauDTO = niveauMapper.toDto(niveau);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(niveauDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().collectList().block().size();
        niveau.setId(count.incrementAndGet());

        // Create the Niveau
        NiveauDTO niveauDTO = niveauMapper.toDto(niveau);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(niveauDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateNiveauWithPatch() throws Exception {
        // Initialize the database
        niveauRepository.save(niveau).block();

        int databaseSizeBeforeUpdate = niveauRepository.findAll().collectList().block().size();

        // Update the niveau using partial update
        Niveau partialUpdatedNiveau = new Niveau();
        partialUpdatedNiveau.setId(niveau.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNiveau.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNiveau))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
        Niveau testNiveau = niveauList.get(niveauList.size() - 1);
        assertThat(testNiveau.getNomNiveau()).isEqualTo(DEFAULT_NOM_NIVEAU);
    }

    @Test
    void fullUpdateNiveauWithPatch() throws Exception {
        // Initialize the database
        niveauRepository.save(niveau).block();

        int databaseSizeBeforeUpdate = niveauRepository.findAll().collectList().block().size();

        // Update the niveau using partial update
        Niveau partialUpdatedNiveau = new Niveau();
        partialUpdatedNiveau.setId(niveau.getId());

        partialUpdatedNiveau.nomNiveau(UPDATED_NOM_NIVEAU);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNiveau.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedNiveau))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
        Niveau testNiveau = niveauList.get(niveauList.size() - 1);
        assertThat(testNiveau.getNomNiveau()).isEqualTo(UPDATED_NOM_NIVEAU);
    }

    @Test
    void patchNonExistingNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().collectList().block().size();
        niveau.setId(count.incrementAndGet());

        // Create the Niveau
        NiveauDTO niveauDTO = niveauMapper.toDto(niveau);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, niveauDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(niveauDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().collectList().block().size();
        niveau.setId(count.incrementAndGet());

        // Create the Niveau
        NiveauDTO niveauDTO = niveauMapper.toDto(niveau);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(niveauDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamNiveau() throws Exception {
        int databaseSizeBeforeUpdate = niveauRepository.findAll().collectList().block().size();
        niveau.setId(count.incrementAndGet());

        // Create the Niveau
        NiveauDTO niveauDTO = niveauMapper.toDto(niveau);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(niveauDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Niveau in the database
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteNiveau() {
        // Initialize the database
        niveauRepository.save(niveau).block();

        int databaseSizeBeforeDelete = niveauRepository.findAll().collectList().block().size();

        // Delete the niveau
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, niveau.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Niveau> niveauList = niveauRepository.findAll().collectList().block();
        assertThat(niveauList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
