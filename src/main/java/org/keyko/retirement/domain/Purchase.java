package org.keyko.retirement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.keyko.retirement.domain.enumeration.Currency;

/**
 * A Purchase.
 */
@Entity
@Table(name = "purchase")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Purchase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "price")
    private Float price;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "number")
    private Integer number;

    @Column(name = "comission")
    private Float comission;

    @ManyToOne
    @JsonIgnoreProperties(value = { "purchases" }, allowSetters = true)
    private Security security;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Purchase id(Long id) {
        this.id = id;
        return this;
    }

    public Float getPrice() {
        return this.price;
    }

    public Purchase price(Float price) {
        this.price = price;
        return this;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public Purchase currency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Integer getNumber() {
        return this.number;
    }

    public Purchase number(Integer number) {
        this.number = number;
        return this;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Float getComission() {
        return this.comission;
    }

    public Purchase comission(Float comission) {
        this.comission = comission;
        return this;
    }

    public void setComission(Float comission) {
        this.comission = comission;
    }

    public Security getSecurity() {
        return this.security;
    }

    public Purchase security(Security security) {
        this.setSecurity(security);
        return this;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Purchase)) {
            return false;
        }
        return id != null && id.equals(((Purchase) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Purchase{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            ", currency='" + getCurrency() + "'" +
            ", number=" + getNumber() +
            ", comission=" + getComission() +
            "}";
    }
}
