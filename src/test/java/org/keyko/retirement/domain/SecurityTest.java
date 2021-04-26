package org.keyko.retirement.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.keyko.retirement.web.rest.TestUtil;

class SecurityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Security.class);
        Security security1 = new Security();
        security1.setId(1L);
        Security security2 = new Security();
        security2.setId(security1.getId());
        assertThat(security1).isEqualTo(security2);
        security2.setId(2L);
        assertThat(security1).isNotEqualTo(security2);
        security1.setId(null);
        assertThat(security1).isNotEqualTo(security2);
    }
}
