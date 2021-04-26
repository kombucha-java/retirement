package org.keyko.retirement.repository;

import org.keyko.retirement.domain.Security;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Security entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SecurityRepository extends JpaRepository<Security, Long> {}
