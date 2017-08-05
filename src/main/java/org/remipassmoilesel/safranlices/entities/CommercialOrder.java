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
    private String postalcode;
    private String city;

    private String shipmentAddress;
    private String shipmentPostalcode;
    private String shipmentCity;

    private String phonenumber;

    private String firstName;

    private String lastName;

    private PaymentType paymentType;

    private Double total;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private boolean paid;

    private boolean processed;

    private String email;

    private Date lastShipmentNotification;

    public CommercialOrder() {
    }

    public CommercialOrder(Date date, List<Product> products, HashMap<Long, Integer> quantities,
                           String address, String postalcode, String city, String shipmentAddress,
                           String shipmentPostalcode, String shipmentCity, String phonenumber,
                           String firstName, String lastName, PaymentType paymentType,
                           String comment, String email) {
        this.date = date;
        this.products = products;
        this.quantities = quantities;
        this.address = address;
        this.postalcode = postalcode;
        this.city = city;
        this.shipmentAddress = shipmentAddress;
        this.shipmentPostalcode = shipmentPostalcode;
        this.shipmentCity = shipmentCity;
        this.phonenumber = phonenumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.paymentType = paymentType;
        this.total = total;
        this.comment = comment;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getLastShipmentNotification() {
        return lastShipmentNotification;
    }

    public void setLastShipmentNotification(Date lastShipmentNotification) {
        this.lastShipmentNotification = lastShipmentNotification;
    }

    public String getShipmentAddress() {
        return shipmentAddress;
    }

    public void setShipmentAddress(String shipmentAddress) {
        this.shipmentAddress = shipmentAddress;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getShipmentPostalcode() {
        return shipmentPostalcode;
    }

    public void setShipmentPostalcode(String shipmentPostalcode) {
        this.shipmentPostalcode = shipmentPostalcode;
    }

    public String getShipmentCity() {
        return shipmentCity;
    }

    public void setShipmentCity(String shipmentCity) {
        this.shipmentCity = shipmentCity;
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
                Objects.equals(postalcode, that.postalcode) &&
                Objects.equals(city, that.city) &&
                Objects.equals(shipmentAddress, that.shipmentAddress) &&
                Objects.equals(shipmentPostalcode, that.shipmentPostalcode) &&
                Objects.equals(shipmentCity, that.shipmentCity) &&
                Objects.equals(phonenumber, that.phonenumber) &&
                Objects.equals(firstName, that.firstName) &&
                Objects.equals(lastName, that.lastName) &&
                paymentType == that.paymentType &&
                Objects.equals(total, that.total) &&
                Objects.equals(comment, that.comment) &&
                Objects.equals(email, that.email) &&
                Objects.equals(lastShipmentNotification, that.lastShipmentNotification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, products, quantities, address, postalcode, city, shipmentAddress, shipmentPostalcode, shipmentCity, phonenumber, firstName, lastName, paymentType, total, comment, paid, processed, email, lastShipmentNotification);
    }

    @Override
    public String toString() {
        return "CommercialOrder{" +
                "id=" + id +
                ", date=" + date +
                ", products=" + products +
                ", quantities=" + quantities +
                ", address='" + address + '\'' +
                ", postalcode='" + postalcode + '\'' +
                ", city='" + city + '\'' +
                ", shipmentAddress='" + shipmentAddress + '\'' +
                ", shipmentPostalcode='" + shipmentPostalcode + '\'' +
                ", shipmentCity='" + shipmentCity + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", paymentType=" + paymentType +
                ", total=" + total +
                ", comment='" + comment + '\'' +
                ", paid=" + paid +
                ", processed=" + processed +
                ", email='" + email + '\'' +
                ", lastShipmentNotification=" + lastShipmentNotification +
                '}';
    }

}
