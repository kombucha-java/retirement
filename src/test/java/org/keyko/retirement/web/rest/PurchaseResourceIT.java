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
import org.keyko.retirement.domain.Purchase;
import org.keyko.retirement.domain.enumeration.Currency;
import org.keyko.retirement.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link PurchaseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PurchaseResourceIT {

    private static final Float DEFAULT_PRICE = 1F;
    private static final Float UPDATED_PRICE = 2F;

    private static final Currency DEFAULT_CURRENCY = Currency.USD;
    private static final Currency UPDATED_CURRENCY = Currency.RUB;

    private static final Integer DEFAULT_NUMBER = 1;
    private static final Integer UPDATED_NUMBER = 2;

    private static final Float DEFAULT_COMISSION = 1F;
    private static final Float UPDATED_COMISSION = 2F;

    private static final String ENTITY_API_URL = "/api/purchases";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPurchaseMockMvc;

    private Purchase purchase;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Purchase createEntity(EntityManager em) {
        Purchase purchase = new Purchase()
            .price(DEFAULT_PRICE)
            .currency(DEFAULT_CURRENCY)
            .number(DEFAULT_NUMBER)
            .comission(DEFAULT_COMISSION);
        return purchase;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Purchase createUpdatedEntity(EntityManager em) {
        Purchase purchase = new Purchase()
            .price(UPDATED_PRICE)
            .currency(UPDATED_CURRENCY)
            .number(UPDATED_NUMBER)
            .comission(UPDATED_COMISSION);
        return purchase;
    }

    @BeforeEach
    public void initTest() {
        purchase = createEntity(em);
    }

    @Test
    @Transactional
    void createPurchase() throws Exception {
        int databaseSizeBeforeCreate = purchaseRepository.findAll().size();
        // Create the Purchase
        restPurchaseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchase)))
            .andExpect(status().isCreated());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeCreate + 1);
        Purchase testPurchase = purchaseList.get(purchaseList.size() - 1);
        assertThat(testPurchase.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testPurchase.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testPurchase.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testPurchase.getComission()).isEqualTo(DEFAULT_COMISSION);
    }

    @Test
    @Transactional
    void createPurchaseWithExistingId() throws Exception {
        // Create the Purchase with an existing ID
        purchase.setId(1L);

        int databaseSizeBeforeCreate = purchaseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPurchaseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchase)))
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPurchases() throws Exception {
        // Initialize the database
        purchaseRepository.saveAndFlush(purchase);

        // Get all the purchaseList
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(purchase.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].number").value(hasItem(DEFAULT_NUMBER)))
            .andExpect(jsonPath("$.[*].comission").value(hasItem(DEFAULT_COMISSION.doubleValue())));
    }

    @Test
    @Transactional
    void getPurchase() throws Exception {
        // Initialize the database
        purchaseRepository.saveAndFlush(purchase);

        // Get the purchase
        restPurchaseMockMvc
            .perform(get(ENTITY_API_URL_ID, purchase.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(purchase.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY.toString()))
            .andExpect(jsonPath("$.number").value(DEFAULT_NUMBER))
            .andExpect(jsonPath("$.comission").value(DEFAULT_COMISSION.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingPurchase() throws Exception {
        // Get the purchase
        restPurchaseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewPurchase() throws Exception {
        // Initialize the database
        purchaseRepository.saveAndFlush(purchase);

        int databaseSizeBeforeUpdate = purchaseRepository.findAll().size();

        // Update the purchase
        Purchase updatedPurchase = purchaseRepository.findById(purchase.getId()).get();
        // Disconnect from session so that the updates on updatedPurchase are not directly saved in db
        em.detach(updatedPurchase);
        updatedPurchase.price(UPDATED_PRICE).currency(UPDATED_CURRENCY).number(UPDATED_NUMBER).comission(UPDATED_COMISSION);

        restPurchaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedPurchase.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedPurchase))
            )
            .andExpect(status().isOk());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeUpdate);
        Purchase testPurchase = purchaseList.get(purchaseList.size() - 1);
        assertThat(testPurchase.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testPurchase.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testPurchase.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testPurchase.getComission()).isEqualTo(UPDATED_COMISSION);
    }

    @Test
    @Transactional
    void putNonExistingPurchase() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRepository.findAll().size();
        purchase.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, purchase.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchase))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPurchase() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRepository.findAll().size();
        purchase.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(purchase))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPurchase() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRepository.findAll().size();
        purchase.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(purchase)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePurchaseWithPatch() throws Exception {
        // Initialize the database
        purchaseRepository.saveAndFlush(purchase);

        int databaseSizeBeforeUpdate = purchaseRepository.findAll().size();

        // Update the purchase using partial update
        Purchase partialUpdatedPurchase = new Purchase();
        partialUpdatedPurchase.setId(purchase.getId());

        partialUpdatedPurchase.price(UPDATED_PRICE).currency(UPDATED_CURRENCY).comission(UPDATED_COMISSION);

        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchase.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchase))
            )
            .andExpect(status().isOk());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeUpdate);
        Purchase testPurchase = purchaseList.get(purchaseList.size() - 1);
        assertThat(testPurchase.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testPurchase.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testPurchase.getNumber()).isEqualTo(DEFAULT_NUMBER);
        assertThat(testPurchase.getComission()).isEqualTo(UPDATED_COMISSION);
    }

    @Test
    @Transactional
    void fullUpdatePurchaseWithPatch() throws Exception {
        // Initialize the database
        purchaseRepository.saveAndFlush(purchase);

        int databaseSizeBeforeUpdate = purchaseRepository.findAll().size();

        // Update the purchase using partial update
        Purchase partialUpdatedPurchase = new Purchase();
        partialUpdatedPurchase.setId(purchase.getId());

        partialUpdatedPurchase.price(UPDATED_PRICE).currency(UPDATED_CURRENCY).number(UPDATED_NUMBER).comission(UPDATED_COMISSION);

        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPurchase.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPurchase))
            )
            .andExpect(status().isOk());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeUpdate);
        Purchase testPurchase = purchaseList.get(purchaseList.size() - 1);
        assertThat(testPurchase.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testPurchase.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testPurchase.getNumber()).isEqualTo(UPDATED_NUMBER);
        assertThat(testPurchase.getComission()).isEqualTo(UPDATED_COMISSION);
    }

    @Test
    @Transactional
    void patchNonExistingPurchase() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRepository.findAll().size();
        purchase.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, purchase.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchase))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPurchase() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRepository.findAll().size();
        purchase.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(purchase))
            )
            .andExpect(status().isBadRequest());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPurchase() throws Exception {
        int databaseSizeBeforeUpdate = purchaseRepository.findAll().size();
        purchase.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPurchaseMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(purchase)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Purchase in the database
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePurchase() throws Exception {
        // Initialize the database
        purchaseRepository.saveAndFlush(purchase);

        int databaseSizeBeforeDelete = purchaseRepository.findAll().size();

        // Delete the purchase
        restPurchaseMockMvc
            .perform(delete(ENTITY_API_URL_ID, purchase.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Purchase> purchaseList = purchaseRepository.findAll();
        assertThat(purchaseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
