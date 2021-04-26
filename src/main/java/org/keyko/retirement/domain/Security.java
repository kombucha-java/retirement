package org.keyko.retirement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.keyko.retirement.domain.enumeration.Region;
import org.keyko.retirement.domain.enumeration.SecurityType;

/**
 * A Security.
 */
@Entity
@Table(name = "security")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Security implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "ticker")
    private String ticker;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private SecurityType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "region")
    private Region region;

    @OneToMany(mappedBy = "security")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "security" }, allowSetters = true)
    private Set<Purchase> purchases = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Security id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Security name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTicker() {
        return this.ticker;
    }

    public Security ticker(String ticker) {
        this.ticker = ticker;
        return this;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public SecurityType getType() {
        return this.type;
    }

    public Security type(SecurityType type) {
        this.type = type;
        return this;
    }

    public void setType(SecurityType type) {
        this.type = type;
    }

    public Region getRegion() {
        return this.region;
    }

    public Security region(Region region) {
        this.region = region;
        return this;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Set<Purchase> getPurchases() {
        return this.purchases;
    }

    public Security purchases(Set<Purchase> purchases) {
        this.setPurchases(purchases);
        return this;
    }

    public Security addPurchase(Purchase purchase) {
        this.purchases.add(purchase);
        purchase.setSecurity(this);
        return this;
    }

    public Security removePurchase(Purchase purchase) {
        this.purchases.remove(purchase);
        purchase.setSecurity(null);
        return this;
    }

    public void setPurchases(Set<Purchase> purchases) {
        if (this.purchases != null) {
            this.purchases.forEach(i -> i.setSecurity(null));
        }
        if (purchases != null) {
            purchases.forEach(i -> i.setSecurity(this));
        }
        this.purchases = purchases;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Security)) {
            return false;
        }
        return id != null && id.equals(((Security) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Security{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", ticker='" + getTicker() + "'" +
            ", type='" + getType() + "'" +
            ", region='" + getRegion() + "'" +
            "}";
    }
}
