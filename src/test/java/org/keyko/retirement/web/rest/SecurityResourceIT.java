package org.keyko.retirement.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keyko.retirement.IntegrationTest;
import org.keyko.retirement.domain.Security;
import org.keyko.retirement.domain.enumeration.Region;
import org.keyko.retirement.domain.enumeration.SecurityType;
import org.keyko.retirement.repository.SecurityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SecurityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SecurityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TICKER = "AAAAAAAAAA";
    private static final String UPDATED_TICKER = "BBBBBBBBBB";

    private static final SecurityType DEFAULT_TYPE = SecurityType.SHARE;
    private static final SecurityType UPDATED_TYPE = SecurityType.BOND;

    private static final Region DEFAULT_REGION = Region.RUS;
    private static final Region UPDATED_REGION = Region.USA;

    private static final String ENTITY_API_URL = "/api/securities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SecurityRepository securityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSecurityMockMvc;

    private Security security;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Security createEntity(EntityManager em) {
        Security security = new Security().name(DEFAULT_NAME).ticker(DEFAULT_TICKER).type(DEFAULT_TYPE).region(DEFAULT_REGION);
        return security;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Security createUpdatedEntity(EntityManager em) {
        Security security = new Security().name(UPDATED_NAME).ticker(UPDATED_TICKER).type(UPDATED_TYPE).region(UPDATED_REGION);
        return security;
    }

    @BeforeEach
    public void initTest() {
        security = createEntity(em);
    }

    @Test
    @Transactional
    void createSecurity() throws Exception {
        int databaseSizeBeforeCreate = securityRepository.findAll().size();
        // Create the Security
        restSecurityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(security)))
            .andExpect(status().isCreated());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeCreate + 1);
        Security testSecurity = securityList.get(securityList.size() - 1);
        assertThat(testSecurity.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSecurity.getTicker()).isEqualTo(DEFAULT_TICKER);
        assertThat(testSecurity.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSecurity.getRegion()).isEqualTo(DEFAULT_REGION);
    }

    @Test
    @Transactional
    void createSecurityWithExistingId() throws Exception {
        // Create the Security with an existing ID
        security.setId(1L);

        int databaseSizeBeforeCreate = securityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSecurityMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(security)))
            .andExpect(status().isBadRequest());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSecurities() throws Exception {
        // Initialize the database
        securityRepository.saveAndFlush(security);

        // Get all the securityList
        restSecurityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(security.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].ticker").value(hasItem(DEFAULT_TICKER)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION.toString())));
    }

    @Test
    @Transactional
    void getSecurity() throws Exception {
        // Initialize the database
        securityRepository.saveAndFlush(security);

        // Get the security
        restSecurityMockMvc
            .perform(get(ENTITY_API_URL_ID, security.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(security.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.ticker").value(DEFAULT_TICKER))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.region").value(DEFAULT_REGION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSecurity() throws Exception {
        // Get the security
        restSecurityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSecurity() throws Exception {
        // Initialize the database
        securityRepository.saveAndFlush(security);

        int databaseSizeBeforeUpdate = securityRepository.findAll().size();

        // Update the security
        Security updatedSecurity = securityRepository.findById(security.getId()).get();
        // Disconnect from session so that the updates on updatedSecurity are not directly saved in db
        em.detach(updatedSecurity);
        updatedSecurity.name(UPDATED_NAME).ticker(UPDATED_TICKER).type(UPDATED_TYPE).region(UPDATED_REGION);

        restSecurityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSecurity.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSecurity))
            )
            .andExpect(status().isOk());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeUpdate);
        Security testSecurity = securityList.get(securityList.size() - 1);
        assertThat(testSecurity.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSecurity.getTicker()).isEqualTo(UPDATED_TICKER);
        assertThat(testSecurity.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSecurity.getRegion()).isEqualTo(UPDATED_REGION);
    }

    @Test
    @Transactional
    void putNonExistingSecurity() throws Exception {
        int databaseSizeBeforeUpdate = securityRepository.findAll().size();
        security.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSecurityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, security.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(security))
            )
            .andExpect(status().isBadRequest());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSecurity() throws Exception {
        int databaseSizeBeforeUpdate = securityRepository.findAll().size();
        security.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(security))
            )
            .andExpect(status().isBadRequest());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSecurity() throws Exception {
        int databaseSizeBeforeUpdate = securityRepository.findAll().size();
        security.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(security)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSecurityWithPatch() throws Exception {
        // Initialize the database
        securityRepository.saveAndFlush(security);

        int databaseSizeBeforeUpdate = securityRepository.findAll().size();

        // Update the security using partial update
        Security partialUpdatedSecurity = new Security();
        partialUpdatedSecurity.setId(security.getId());

        partialUpdatedSecurity.region(UPDATED_REGION);

        restSecurityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSecurity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSecurity))
            )
            .andExpect(status().isOk());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeUpdate);
        Security testSecurity = securityList.get(securityList.size() - 1);
        assertThat(testSecurity.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSecurity.getTicker()).isEqualTo(DEFAULT_TICKER);
        assertThat(testSecurity.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testSecurity.getRegion()).isEqualTo(UPDATED_REGION);
    }

    @Test
    @Transactional
    void fullUpdateSecurityWithPatch() throws Exception {
        // Initialize the database
        securityRepository.saveAndFlush(security);

        int databaseSizeBeforeUpdate = securityRepository.findAll().size();

        // Update the security using partial update
        Security partialUpdatedSecurity = new Security();
        partialUpdatedSecurity.setId(security.getId());

        partialUpdatedSecurity.name(UPDATED_NAME).ticker(UPDATED_TICKER).type(UPDATED_TYPE).region(UPDATED_REGION);

        restSecurityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSecurity.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSecurity))
            )
            .andExpect(status().isOk());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeUpdate);
        Security testSecurity = securityList.get(securityList.size() - 1);
        assertThat(testSecurity.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSecurity.getTicker()).isEqualTo(UPDATED_TICKER);
        assertThat(testSecurity.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testSecurity.getRegion()).isEqualTo(UPDATED_REGION);
    }

    @Test
    @Transactional
    void patchNonExistingSecurity() throws Exception {
        int databaseSizeBeforeUpdate = securityRepository.findAll().size();
        security.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSecurityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, security.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(security))
            )
            .andExpect(status().isBadRequest());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSecurity() throws Exception {
        int databaseSizeBeforeUpdate = securityRepository.findAll().size();
        security.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(security))
            )
            .andExpect(status().isBadRequest());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSecurity() throws Exception {
        int databaseSizeBeforeUpdate = securityRepository.findAll().size();
        security.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSecurityMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(security)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Security in the database
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSecurity() throws Exception {
        // Initialize the database
        securityRepository.saveAndFlush(security);

        int databaseSizeBeforeDelete = securityRepository.findAll().size();

        // Delete the security
        restSecurityMockMvc
            .perform(delete(ENTITY_API_URL_ID, security.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Security> securityList = securityRepository.findAll();
        assertThat(securityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
