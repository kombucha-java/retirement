package org.keyko.retirement.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.keyko.retirement.domain.Security;
import org.keyko.retirement.repository.SecurityRepository;
import org.keyko.retirement.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link org.keyko.retirement.domain.Security}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class SecurityResource {

    private final Logger log = LoggerFactory.getLogger(SecurityResource.class);

    private static final String ENTITY_NAME = "security";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SecurityRepository securityRepository;

    public SecurityResource(SecurityRepository securityRepository) {
        this.securityRepository = securityRepository;
    }

    /**
     * {@code POST  /securities} : Create a new security.
     *
     * @param security the security to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new security, or with status {@code 400 (Bad Request)} if the security has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/securities")
    public ResponseEntity<Security> createSecurity(@RequestBody Security security) throws URISyntaxException {
        log.debug("REST request to save Security : {}", security);
        if (security.getId() != null) {
            throw new BadRequestAlertException("A new security cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Security result = securityRepository.save(security);
        return ResponseEntity
            .created(new URI("/api/securities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /securities/:id} : Updates an existing security.
     *
     * @param id the id of the security to save.
     * @param security the security to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated security,
     * or with status {@code 400 (Bad Request)} if the security is not valid,
     * or with status {@code 500 (Internal Server Error)} if the security couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/securities/{id}")
    public ResponseEntity<Security> updateSecurity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Security security
    ) throws URISyntaxException {
        log.debug("REST request to update Security : {}, {}", id, security);
        if (security.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, security.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!securityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Security result = securityRepository.save(security);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, security.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /securities/:id} : Partial updates given fields of an existing security, field will ignore if it is null
     *
     * @param id the id of the security to save.
     * @param security the security to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated security,
     * or with status {@code 400 (Bad Request)} if the security is not valid,
     * or with status {@code 404 (Not Found)} if the security is not found,
     * or with status {@code 500 (Internal Server Error)} if the security couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/securities/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<Security> partialUpdateSecurity(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Security security
    ) throws URISyntaxException {
        log.debug("REST request to partial update Security partially : {}, {}", id, security);
        if (security.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, security.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!securityRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Security> result = securityRepository
            .findById(security.getId())
            .map(
                existingSecurity -> {
                    if (security.getName() != null) {
                        existingSecurity.setName(security.getName());
                    }
                    if (security.getTicker() != null) {
                        existingSecurity.setTicker(security.getTicker());
                    }
                    if (security.getType() != null) {
                        existingSecurity.setType(security.getType());
                    }
                    if (security.getRegion() != null) {
                        existingSecurity.setRegion(security.getRegion());
                    }

                    return existingSecurity;
                }
            )
            .map(securityRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, security.getId().toString())
        );
    }

    /**
     * {@code GET  /securities} : get all the securities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of securities in body.
     */
    @GetMapping("/securities")
    public List<Security> getAllSecurities() {
        log.debug("REST request to get all Securities");
        return securityRepository.findAll();
    }

    /**
     * {@code GET  /securities/:id} : get the "id" security.
     *
     * @param id the id of the security to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the security, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/securities/{id}")
    public ResponseEntity<Security> getSecurity(@PathVariable Long id) {
        log.debug("REST request to get Security : {}", id);
        Optional<Security> security = securityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(security);
    }

    /**
     * {@code DELETE  /securities/:id} : delete the "id" security.
     *
     * @param id the id of the security to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/securities/{id}")
    public ResponseEntity<Void> deleteSecurity(@PathVariable Long id) {
        log.debug("REST request to delete Security : {}", id);
        securityRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
