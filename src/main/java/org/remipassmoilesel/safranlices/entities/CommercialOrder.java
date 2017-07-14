package org.remipassmoilesel.safranlices.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
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

    @Column(columnDefinition = "BLOB")
    private HashMap<Long, Integer> quantities;

    private String address;

    private String phonenumber;

    private String firstName;

    private String lastName;

    private PaymentType paymentType;

    private Double total;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private boolean paid;

    private boolean processed;

    public CommercialOrder() {
    }

    public CommercialOrder(Date date, List<Product> products, HashMap<Long, Integer> quantities, String address, String phonenumber, String firstName, String lastName, PaymentType paymentType, String comment) {
        this.date = date;
        this.products = products;
        this.quantities = quantities;
        this.address = address;
        this.phonenumber = phonenumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.paymentType = paymentType;
        this.comment = comment;
    }

    public HashMap<Long, Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(HashMap<Long, Integer> quantities) {
        this.quantities = quantities;
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

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommercialOrder that = (CommercialOrder) o;
        return paid == that.paid &&
                processed == that.processed &&
                Objects.equals(id, that.id) &&
                Objects.equals(date, that.date) &&
                Objects.equals(products, that.products) &&
                Objects.equals(quantities, that.quantities) &&
                Objects.equals(address, that.address) &&
                Objects.equals(phonenumber, that.phonenumber) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                paymentType == that.paymentType &&
                Objects.equals(total, that.total) &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, products, quantities, address, phonenumber, firstName, lastName, paymentType, total, comment, paid, processed);
    }

    @Override
    public String toString() {
        return "CommercialOrder{" +
                "id=" + id +
                ", date=" + date +
                ", products=" + products +
                ", quantities=" + quantities +
                ", address='" + address + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", paymentType=" + paymentType +
                ", comment='" + comment + '\'' +
                '}';
    }
}
