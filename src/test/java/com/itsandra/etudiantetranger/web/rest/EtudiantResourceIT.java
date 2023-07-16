package com.itsandra.etudiantetranger.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.itsandra.etudiantetranger.IntegrationTest;
import com.itsandra.etudiantetranger.domain.Etudiant;
import com.itsandra.etudiantetranger.repository.EntityManager;
import com.itsandra.etudiantetranger.repository.EtudiantRepository;
import com.itsandra.etudiantetranger.service.dto.EtudiantDTO;
import com.itsandra.etudiantetranger.service.mapper.EtudiantMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link EtudiantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EtudiantResourceIT {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    private static final String DEFAULT_PRENOM = "AAAAAAAAAA";
    private static final String UPDATED_PRENOM = "BBBBBBBBBB";

    private static final Long DEFAULT_MATRICULE = 1L;
    private static final Long UPDATED_MATRICULE = 2L;

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/etudiants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private EtudiantMapper etudiantMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Etudiant etudiant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etudiant createEntity(EntityManager em) {
        Etudiant etudiant = new Etudiant().nom(DEFAULT_NOM).prenom(DEFAULT_PRENOM).matricule(DEFAULT_MATRICULE).date(DEFAULT_DATE);
        return etudiant;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Etudiant createUpdatedEntity(EntityManager em) {
        Etudiant etudiant = new Etudiant().nom(UPDATED_NOM).prenom(UPDATED_PRENOM).matricule(UPDATED_MATRICULE).date(UPDATED_DATE);
        return etudiant;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Etudiant.class).block();
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
        etudiant = createEntity(em);
    }

    @Test
    void createEtudiant() throws Exception {
        int databaseSizeBeforeCreate = etudiantRepository.findAll().collectList().block().size();
        // Create the Etudiant
        EtudiantDTO etudiantDTO = etudiantMapper.toDto(etudiant);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(etudiantDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeCreate + 1);
        Etudiant testEtudiant = etudiantList.get(etudiantList.size() - 1);
        assertThat(testEtudiant.getNom()).isEqualTo(DEFAULT_NOM);
        assertThat(testEtudiant.getPrenom()).isEqualTo(DEFAULT_PRENOM);
        assertThat(testEtudiant.getMatricule()).isEqualTo(DEFAULT_MATRICULE);
        assertThat(testEtudiant.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void createEtudiantWithExistingId() throws Exception {
        // Create the Etudiant with an existing ID
        etudiant.setId(1L);
        EtudiantDTO etudiantDTO = etudiantMapper.toDto(etudiant);

        int databaseSizeBeforeCreate = etudiantRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(etudiantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEtudiants() {
        // Initialize the database
        etudiantRepository.save(etudiant).block();

        // Get all the etudiantList
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
            .value(hasItem(etudiant.getId().intValue()))
            .jsonPath("$.[*].nom")
            .value(hasItem(DEFAULT_NOM))
            .jsonPath("$.[*].prenom")
            .value(hasItem(DEFAULT_PRENOM))
            .jsonPath("$.[*].matricule")
            .value(hasItem(DEFAULT_MATRICULE.intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));
    }

    @Test
    void getEtudiant() {
        // Initialize the database
        etudiantRepository.save(etudiant).block();

        // Get the etudiant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, etudiant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(etudiant.getId().intValue()))
            .jsonPath("$.nom")
            .value(is(DEFAULT_NOM))
            .jsonPath("$.prenom")
            .value(is(DEFAULT_PRENOM))
            .jsonPath("$.matricule")
            .value(is(DEFAULT_MATRICULE.intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()));
    }

    @Test
    void getNonExistingEtudiant() {
        // Get the etudiant
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEtudiant() throws Exception {
        // Initialize the database
        etudiantRepository.save(etudiant).block();

        int databaseSizeBeforeUpdate = etudiantRepository.findAll().collectList().block().size();

        // Update the etudiant
        Etudiant updatedEtudiant = etudiantRepository.findById(etudiant.getId()).block();
        updatedEtudiant.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).matricule(UPDATED_MATRICULE).date(UPDATED_DATE);
        EtudiantDTO etudiantDTO = etudiantMapper.toDto(updatedEtudiant);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, etudiantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(etudiantDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeUpdate);
        Etudiant testEtudiant = etudiantList.get(etudiantList.size() - 1);
        assertThat(testEtudiant.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEtudiant.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testEtudiant.getMatricule()).isEqualTo(UPDATED_MATRICULE);
        assertThat(testEtudiant.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void putNonExistingEtudiant() throws Exception {
        int databaseSizeBeforeUpdate = etudiantRepository.findAll().collectList().block().size();
        etudiant.setId(count.incrementAndGet());

        // Create the Etudiant
        EtudiantDTO etudiantDTO = etudiantMapper.toDto(etudiant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, etudiantDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(etudiantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEtudiant() throws Exception {
        int databaseSizeBeforeUpdate = etudiantRepository.findAll().collectList().block().size();
        etudiant.setId(count.incrementAndGet());

        // Create the Etudiant
        EtudiantDTO etudiantDTO = etudiantMapper.toDto(etudiant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(etudiantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEtudiant() throws Exception {
        int databaseSizeBeforeUpdate = etudiantRepository.findAll().collectList().block().size();
        etudiant.setId(count.incrementAndGet());

        // Create the Etudiant
        EtudiantDTO etudiantDTO = etudiantMapper.toDto(etudiant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(etudiantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEtudiantWithPatch() throws Exception {
        // Initialize the database
        etudiantRepository.save(etudiant).block();

        int databaseSizeBeforeUpdate = etudiantRepository.findAll().collectList().block().size();

        // Update the etudiant using partial update
        Etudiant partialUpdatedEtudiant = new Etudiant();
        partialUpdatedEtudiant.setId(etudiant.getId());

        partialUpdatedEtudiant.nom(UPDATED_NOM).prenom(UPDATED_PRENOM);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEtudiant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEtudiant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeUpdate);
        Etudiant testEtudiant = etudiantList.get(etudiantList.size() - 1);
        assertThat(testEtudiant.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEtudiant.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testEtudiant.getMatricule()).isEqualTo(DEFAULT_MATRICULE);
        assertThat(testEtudiant.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void fullUpdateEtudiantWithPatch() throws Exception {
        // Initialize the database
        etudiantRepository.save(etudiant).block();

        int databaseSizeBeforeUpdate = etudiantRepository.findAll().collectList().block().size();

        // Update the etudiant using partial update
        Etudiant partialUpdatedEtudiant = new Etudiant();
        partialUpdatedEtudiant.setId(etudiant.getId());

        partialUpdatedEtudiant.nom(UPDATED_NOM).prenom(UPDATED_PRENOM).matricule(UPDATED_MATRICULE).date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEtudiant.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEtudiant))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeUpdate);
        Etudiant testEtudiant = etudiantList.get(etudiantList.size() - 1);
        assertThat(testEtudiant.getNom()).isEqualTo(UPDATED_NOM);
        assertThat(testEtudiant.getPrenom()).isEqualTo(UPDATED_PRENOM);
        assertThat(testEtudiant.getMatricule()).isEqualTo(UPDATED_MATRICULE);
        assertThat(testEtudiant.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void patchNonExistingEtudiant() throws Exception {
        int databaseSizeBeforeUpdate = etudiantRepository.findAll().collectList().block().size();
        etudiant.setId(count.incrementAndGet());

        // Create the Etudiant
        EtudiantDTO etudiantDTO = etudiantMapper.toDto(etudiant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, etudiantDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(etudiantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEtudiant() throws Exception {
        int databaseSizeBeforeUpdate = etudiantRepository.findAll().collectList().block().size();
        etudiant.setId(count.incrementAndGet());

        // Create the Etudiant
        EtudiantDTO etudiantDTO = etudiantMapper.toDto(etudiant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(etudiantDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEtudiant() throws Exception {
        int databaseSizeBeforeUpdate = etudiantRepository.findAll().collectList().block().size();
        etudiant.setId(count.incrementAndGet());

        // Create the Etudiant
        EtudiantDTO etudiantDTO = etudiantMapper.toDto(etudiant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(etudiantDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Etudiant in the database
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEtudiant() {
        // Initialize the database
        etudiantRepository.save(etudiant).block();

        int databaseSizeBeforeDelete = etudiantRepository.findAll().collectList().block().size();

        // Delete the etudiant
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, etudiant.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Etudiant> etudiantList = etudiantRepository.findAll().collectList().block();
        assertThat(etudiantList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
