package org.remipassmoilesel.safranlices.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by remipassmoilesel on 07/07/17.
 */
@Entity
public class CommercialOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date date;

    @ManyToMany
    private List<Product> products;

    private String address;

    private String phonenumber;

    private String firstName;

    private String lastName;

    private PaymentType paymentType;

    @Column(columnDefinition = "TEXT")
    private String comment;

    public CommercialOrder() {
    }

    public CommercialOrder(Date date, List<Product> products, String address, String phonenumber, String firstName, String lastName, PaymentType paymentType, String comment) {
        this.date = date;
        this.products = products;
        this.address = address;
        this.phonenumber = phonenumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.paymentType = paymentType;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommercialOrder order = (CommercialOrder) o;
        return Objects.equals(id, order.id) &&
                Objects.equals(date, order.date) &&
                Objects.equals(products, order.products) &&
                Objects.equals(address, order.address) &&
                Objects.equals(phonenumber, order.phonenumber) &&
                Objects.equals(firstName, order.firstName) &&
                Objects.equals(lastName, order.lastName) &&
                paymentType == order.paymentType &&
                Objects.equals(comment, order.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, products, address, phonenumber, firstName, lastName, paymentType, comment);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", date=" + date +
                ", product=" + products +
                ", address='" + address + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", paymentType=" + paymentType +
                ", comment='" + comment + '\'' +
                '}';
    }
}
