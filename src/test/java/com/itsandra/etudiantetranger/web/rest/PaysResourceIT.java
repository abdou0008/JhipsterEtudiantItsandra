package com.itsandra.etudiantetranger.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.itsandra.etudiantetranger.IntegrationTest;
import com.itsandra.etudiantetranger.domain.Pays;
import com.itsandra.etudiantetranger.repository.EntityManager;
import com.itsandra.etudiantetranger.repository.PaysRepository;
import com.itsandra.etudiantetranger.service.dto.PaysDTO;
import com.itsandra.etudiantetranger.service.mapper.PaysMapper;
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
 * Integration tests for the {@link PaysResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaysResourceIT {

    private static final String DEFAULT_NOM_PAYS = "AAAAAAAAAA";
    private static final String UPDATED_NOM_PAYS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pays";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaysRepository paysRepository;

    @Autowired
    private PaysMapper paysMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Pays pays;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pays createEntity(EntityManager em) {
        Pays pays = new Pays().nomPays(DEFAULT_NOM_PAYS);
        return pays;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pays createUpdatedEntity(EntityManager em) {
        Pays pays = new Pays().nomPays(UPDATED_NOM_PAYS);
        return pays;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Pays.class).block();
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
        pays = createEntity(em);
    }

    @Test
    void createPays() throws Exception {
        int databaseSizeBeforeCreate = paysRepository.findAll().collectList().block().size();
        // Create the Pays
        PaysDTO paysDTO = paysMapper.toDto(pays);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paysDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeCreate + 1);
        Pays testPays = paysList.get(paysList.size() - 1);
        assertThat(testPays.getNomPays()).isEqualTo(DEFAULT_NOM_PAYS);
    }

    @Test
    void createPaysWithExistingId() throws Exception {
        // Create the Pays with an existing ID
        pays.setId(1L);
        PaysDTO paysDTO = paysMapper.toDto(pays);

        int databaseSizeBeforeCreate = paysRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paysDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPays() {
        // Initialize the database
        paysRepository.save(pays).block();

        // Get all the paysList
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
            .value(hasItem(pays.getId().intValue()))
            .jsonPath("$.[*].nomPays")
            .value(hasItem(DEFAULT_NOM_PAYS));
    }

    @Test
    void getPays() {
        // Initialize the database
        paysRepository.save(pays).block();

        // Get the pays
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pays.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pays.getId().intValue()))
            .jsonPath("$.nomPays")
            .value(is(DEFAULT_NOM_PAYS));
    }

    @Test
    void getNonExistingPays() {
        // Get the pays
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPays() throws Exception {
        // Initialize the database
        paysRepository.save(pays).block();

        int databaseSizeBeforeUpdate = paysRepository.findAll().collectList().block().size();

        // Update the pays
        Pays updatedPays = paysRepository.findById(pays.getId()).block();
        updatedPays.nomPays(UPDATED_NOM_PAYS);
        PaysDTO paysDTO = paysMapper.toDto(updatedPays);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paysDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paysDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
        Pays testPays = paysList.get(paysList.size() - 1);
        assertThat(testPays.getNomPays()).isEqualTo(UPDATED_NOM_PAYS);
    }

    @Test
    void putNonExistingPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().collectList().block().size();
        pays.setId(count.incrementAndGet());

        // Create the Pays
        PaysDTO paysDTO = paysMapper.toDto(pays);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paysDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paysDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().collectList().block().size();
        pays.setId(count.incrementAndGet());

        // Create the Pays
        PaysDTO paysDTO = paysMapper.toDto(pays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paysDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().collectList().block().size();
        pays.setId(count.incrementAndGet());

        // Create the Pays
        PaysDTO paysDTO = paysMapper.toDto(pays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paysDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaysWithPatch() throws Exception {
        // Initialize the database
        paysRepository.save(pays).block();

        int databaseSizeBeforeUpdate = paysRepository.findAll().collectList().block().size();

        // Update the pays using partial update
        Pays partialUpdatedPays = new Pays();
        partialUpdatedPays.setId(pays.getId());

        partialUpdatedPays.nomPays(UPDATED_NOM_PAYS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPays.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPays))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
        Pays testPays = paysList.get(paysList.size() - 1);
        assertThat(testPays.getNomPays()).isEqualTo(UPDATED_NOM_PAYS);
    }

    @Test
    void fullUpdatePaysWithPatch() throws Exception {
        // Initialize the database
        paysRepository.save(pays).block();

        int databaseSizeBeforeUpdate = paysRepository.findAll().collectList().block().size();

        // Update the pays using partial update
        Pays partialUpdatedPays = new Pays();
        partialUpdatedPays.setId(pays.getId());

        partialUpdatedPays.nomPays(UPDATED_NOM_PAYS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPays.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPays))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
        Pays testPays = paysList.get(paysList.size() - 1);
        assertThat(testPays.getNomPays()).isEqualTo(UPDATED_NOM_PAYS);
    }

    @Test
    void patchNonExistingPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().collectList().block().size();
        pays.setId(count.incrementAndGet());

        // Create the Pays
        PaysDTO paysDTO = paysMapper.toDto(pays);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paysDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paysDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().collectList().block().size();
        pays.setId(count.incrementAndGet());

        // Create the Pays
        PaysDTO paysDTO = paysMapper.toDto(pays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paysDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPays() throws Exception {
        int databaseSizeBeforeUpdate = paysRepository.findAll().collectList().block().size();
        pays.setId(count.incrementAndGet());

        // Create the Pays
        PaysDTO paysDTO = paysMapper.toDto(pays);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paysDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pays in the database
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePays() {
        // Initialize the database
        paysRepository.save(pays).block();

        int databaseSizeBeforeDelete = paysRepository.findAll().collectList().block().size();

        // Delete the pays
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pays.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Pays> paysList = paysRepository.findAll().collectList().block();
        assertThat(paysList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
