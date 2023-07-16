package com.itsandra.etudiantetranger.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.itsandra.etudiantetranger.IntegrationTest;
import com.itsandra.etudiantetranger.domain.Filiere;
import com.itsandra.etudiantetranger.repository.EntityManager;
import com.itsandra.etudiantetranger.repository.FiliereRepository;
import com.itsandra.etudiantetranger.service.dto.FiliereDTO;
import com.itsandra.etudiantetranger.service.mapper.FiliereMapper;
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
 * Integration tests for the {@link FiliereResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class FiliereResourceIT {

    private static final String DEFAULT_NOM_FILIERE = "AAAAAAAAAA";
    private static final String UPDATED_NOM_FILIERE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/filieres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FiliereRepository filiereRepository;

    @Autowired
    private FiliereMapper filiereMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Filiere filiere;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Filiere createEntity(EntityManager em) {
        Filiere filiere = new Filiere().nomFiliere(DEFAULT_NOM_FILIERE);
        return filiere;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Filiere createUpdatedEntity(EntityManager em) {
        Filiere filiere = new Filiere().nomFiliere(UPDATED_NOM_FILIERE);
        return filiere;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Filiere.class).block();
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
        filiere = createEntity(em);
    }

    @Test
    void createFiliere() throws Exception {
        int databaseSizeBeforeCreate = filiereRepository.findAll().collectList().block().size();
        // Create the Filiere
        FiliereDTO filiereDTO = filiereMapper.toDto(filiere);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filiereDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeCreate + 1);
        Filiere testFiliere = filiereList.get(filiereList.size() - 1);
        assertThat(testFiliere.getNomFiliere()).isEqualTo(DEFAULT_NOM_FILIERE);
    }

    @Test
    void createFiliereWithExistingId() throws Exception {
        // Create the Filiere with an existing ID
        filiere.setId(1L);
        FiliereDTO filiereDTO = filiereMapper.toDto(filiere);

        int databaseSizeBeforeCreate = filiereRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filiereDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllFilieres() {
        // Initialize the database
        filiereRepository.save(filiere).block();

        // Get all the filiereList
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
            .value(hasItem(filiere.getId().intValue()))
            .jsonPath("$.[*].nomFiliere")
            .value(hasItem(DEFAULT_NOM_FILIERE));
    }

    @Test
    void getFiliere() {
        // Initialize the database
        filiereRepository.save(filiere).block();

        // Get the filiere
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, filiere.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(filiere.getId().intValue()))
            .jsonPath("$.nomFiliere")
            .value(is(DEFAULT_NOM_FILIERE));
    }

    @Test
    void getNonExistingFiliere() {
        // Get the filiere
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewFiliere() throws Exception {
        // Initialize the database
        filiereRepository.save(filiere).block();

        int databaseSizeBeforeUpdate = filiereRepository.findAll().collectList().block().size();

        // Update the filiere
        Filiere updatedFiliere = filiereRepository.findById(filiere.getId()).block();
        updatedFiliere.nomFiliere(UPDATED_NOM_FILIERE);
        FiliereDTO filiereDTO = filiereMapper.toDto(updatedFiliere);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, filiereDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filiereDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
        Filiere testFiliere = filiereList.get(filiereList.size() - 1);
        assertThat(testFiliere.getNomFiliere()).isEqualTo(UPDATED_NOM_FILIERE);
    }

    @Test
    void putNonExistingFiliere() throws Exception {
        int databaseSizeBeforeUpdate = filiereRepository.findAll().collectList().block().size();
        filiere.setId(count.incrementAndGet());

        // Create the Filiere
        FiliereDTO filiereDTO = filiereMapper.toDto(filiere);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, filiereDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filiereDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchFiliere() throws Exception {
        int databaseSizeBeforeUpdate = filiereRepository.findAll().collectList().block().size();
        filiere.setId(count.incrementAndGet());

        // Create the Filiere
        FiliereDTO filiereDTO = filiereMapper.toDto(filiere);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filiereDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamFiliere() throws Exception {
        int databaseSizeBeforeUpdate = filiereRepository.findAll().collectList().block().size();
        filiere.setId(count.incrementAndGet());

        // Create the Filiere
        FiliereDTO filiereDTO = filiereMapper.toDto(filiere);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(filiereDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateFiliereWithPatch() throws Exception {
        // Initialize the database
        filiereRepository.save(filiere).block();

        int databaseSizeBeforeUpdate = filiereRepository.findAll().collectList().block().size();

        // Update the filiere using partial update
        Filiere partialUpdatedFiliere = new Filiere();
        partialUpdatedFiliere.setId(filiere.getId());

        partialUpdatedFiliere.nomFiliere(UPDATED_NOM_FILIERE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFiliere.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFiliere))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
        Filiere testFiliere = filiereList.get(filiereList.size() - 1);
        assertThat(testFiliere.getNomFiliere()).isEqualTo(UPDATED_NOM_FILIERE);
    }

    @Test
    void fullUpdateFiliereWithPatch() throws Exception {
        // Initialize the database
        filiereRepository.save(filiere).block();

        int databaseSizeBeforeUpdate = filiereRepository.findAll().collectList().block().size();

        // Update the filiere using partial update
        Filiere partialUpdatedFiliere = new Filiere();
        partialUpdatedFiliere.setId(filiere.getId());

        partialUpdatedFiliere.nomFiliere(UPDATED_NOM_FILIERE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedFiliere.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedFiliere))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
        Filiere testFiliere = filiereList.get(filiereList.size() - 1);
        assertThat(testFiliere.getNomFiliere()).isEqualTo(UPDATED_NOM_FILIERE);
    }

    @Test
    void patchNonExistingFiliere() throws Exception {
        int databaseSizeBeforeUpdate = filiereRepository.findAll().collectList().block().size();
        filiere.setId(count.incrementAndGet());

        // Create the Filiere
        FiliereDTO filiereDTO = filiereMapper.toDto(filiere);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, filiereDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filiereDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchFiliere() throws Exception {
        int databaseSizeBeforeUpdate = filiereRepository.findAll().collectList().block().size();
        filiere.setId(count.incrementAndGet());

        // Create the Filiere
        FiliereDTO filiereDTO = filiereMapper.toDto(filiere);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filiereDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamFiliere() throws Exception {
        int databaseSizeBeforeUpdate = filiereRepository.findAll().collectList().block().size();
        filiere.setId(count.incrementAndGet());

        // Create the Filiere
        FiliereDTO filiereDTO = filiereMapper.toDto(filiere);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(filiereDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Filiere in the database
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteFiliere() {
        // Initialize the database
        filiereRepository.save(filiere).block();

        int databaseSizeBeforeDelete = filiereRepository.findAll().collectList().block().size();

        // Delete the filiere
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, filiere.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Filiere> filiereList = filiereRepository.findAll().collectList().block();
        assertThat(filiereList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
