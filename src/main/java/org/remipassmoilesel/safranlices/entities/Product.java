package org.remipassmoilesel.safranlices.entities;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by remipassmoilesel on 07/07/17.
 */
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String pictures;

    @Column
    private Double price;

    @Column
    private int quantityAvailable;

    public Product() {
    }

    public Product(String name, String description, String pictures, Double price, int quantityAvailable) {
        this.name = name;
        this.description = description;
        this.pictures = pictures;
        this.price = price;
        this.quantityAvailable = quantityAvailable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictures() {
        return pictures;
    }

    public void setPictures(String pictures) {
        this.pictures = pictures;
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

    public List<String> getPicturesAsList(){
        return Arrays.asList(pictures.split(","));
    }

    public void setPicturesAsList(List<String> paths){
        pictures = String.join(",", paths);
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return quantityAvailable == product.quantityAvailable &&
                Objects.equals(id, product.id) &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) &&
                Objects.equals(pictures, product.pictures) &&
                Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, pictures, price, quantityAvailable);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", pictures='" + pictures + '\'' +
                ", price=" + price +
                ", quantityAvailable=" + quantityAvailable +
                '}';
    }

}
