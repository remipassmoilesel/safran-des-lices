package org.remipassmoilesel.safranlices.entities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

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

    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Expense> expenses;

    public CommercialOrder() {
    }

    public CommercialOrder(Date date,
                           List<Product> products,
                           Basket basket,
                           String address, String postalcode,
                           String city, String shipmentAddress,
                           String shipmentPostalcode,
                           String shipmentCity, String phonenumber,
                           String firstName, String lastName,
                           PaymentType paymentType,
                           String comment, String email,
                           List<Expense> expenses) {
        this.date = date;
        this.products = products;
        this.quantities = basket.getProductMap();
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
        this.expenses = expenses;
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

    public String getRoundedTotalAsString(){
        return String.valueOf(Math.round(total * 100.0) / 100.0);
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

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommercialOrder that = (CommercialOrder) o;

        if (paid != that.paid) return false;
        if (processed != that.processed) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (products != null ? !products.equals(that.products) : that.products != null) return false;
        if (quantities != null ? !quantities.equals(that.quantities) : that.quantities != null) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (postalcode != null ? !postalcode.equals(that.postalcode) : that.postalcode != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        if (shipmentAddress != null ? !shipmentAddress.equals(that.shipmentAddress) : that.shipmentAddress != null)
            return false;
        if (shipmentPostalcode != null ? !shipmentPostalcode.equals(that.shipmentPostalcode) : that.shipmentPostalcode != null)
            return false;
        if (shipmentCity != null ? !shipmentCity.equals(that.shipmentCity) : that.shipmentCity != null) return false;
        if (phonenumber != null ? !phonenumber.equals(that.phonenumber) : that.phonenumber != null) return false;
        if (firstName != null ? !firstName.equals(that.firstName) : that.firstName != null) return false;
        if (lastName != null ? !lastName.equals(that.lastName) : that.lastName != null) return false;
        if (paymentType != that.paymentType) return false;
        if (total != null ? !total.equals(that.total) : that.total != null) return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (lastShipmentNotification != null ? !lastShipmentNotification.equals(that.lastShipmentNotification) : that.lastShipmentNotification != null)
            return false;
        return expenses != null ? expenses.equals(that.expenses) : that.expenses == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (products != null ? products.hashCode() : 0);
        result = 31 * result + (quantities != null ? quantities.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (postalcode != null ? postalcode.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (shipmentAddress != null ? shipmentAddress.hashCode() : 0);
        result = 31 * result + (shipmentPostalcode != null ? shipmentPostalcode.hashCode() : 0);
        result = 31 * result + (shipmentCity != null ? shipmentCity.hashCode() : 0);
        result = 31 * result + (phonenumber != null ? phonenumber.hashCode() : 0);
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
        result = 31 * result + (paymentType != null ? paymentType.hashCode() : 0);
        result = 31 * result + (total != null ? total.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (paid ? 1 : 0);
        result = 31 * result + (processed ? 1 : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (lastShipmentNotification != null ? lastShipmentNotification.hashCode() : 0);
        result = 31 * result + (expenses != null ? expenses.hashCode() : 0);
        return result;
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
                ", expenses=" + expenses +
                '}';
    }

    public String getFormattedDate(String pattern) {
        return DateTimeFormat.forPattern(pattern).print(new DateTime(date));
    }
}
