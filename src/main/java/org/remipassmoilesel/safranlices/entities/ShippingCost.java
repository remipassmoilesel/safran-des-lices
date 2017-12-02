package org.remipassmoilesel.safranlices.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class ShippingCost {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Boolean deleted;
    private Double minWeight;
    private Double maxWeight;
    private Double price;

    public ShippingCost() {
        this.deleted = false;
    }

    public ShippingCost(Double minWeight, Double maxWeight, Double price) {
        this();
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.price = price;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Double getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(Double minWeight) {
        this.minWeight = minWeight;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShippingCost that = (ShippingCost) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(deleted, that.deleted) &&
                Objects.equals(minWeight, that.minWeight) &&
                Objects.equals(maxWeight, that.maxWeight) &&
                Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deleted, minWeight, maxWeight, price);
    }

    @Override
    public String toString() {
        return "ShippingCost{" +
                "id=" + id +
                ", deleted=" + deleted +
                ", minWeight=" + minWeight +
                ", maxWeight=" + maxWeight +
                ", price=" + price +
                '}';
    }
}
