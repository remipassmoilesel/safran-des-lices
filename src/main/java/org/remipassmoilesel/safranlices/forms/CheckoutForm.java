package org.remipassmoilesel.safranlices.forms;

import javax.validation.constraints.*;
import javax.validation.constraints.Pattern;
import java.util.Objects;
import java.util.regex.*;

/**
 * Created by remipassmoilesel on 09/07/17.
 */
public class CheckoutForm {

    @NotNull
    @Size(min = 2)
    private String firstname;

    @NotNull
    @Size(min = 2)
    private String lastname;

    @NotNull
    @Size(min = 2)
    @Pattern(regexp = "[0-9]{2,}")
    private String postalcode;

    @NotNull
    @Size(min = 2)
    private String city;

    @NotNull
    @Size(min = 2)
    private String address;

    private String shipmentPostalcode;

    private String shipmentCity;

    private String shipmentAddress;

    @Size(min = 2)
    @Pattern(regexp = "\\+?[0-9]{2,}")
    private String phonenumber;

    @NotNull
    @Size(min = 2)
    private String paymentType;

    private String comment;

    @NotNull
    @Pattern(regexp = "^[A-Za-z0-9._+-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,6}$")
    private String email;

    public CheckoutForm() {
    }

    public CheckoutForm(String firstname, String lastname, String postalcode, String city, String address, String phonenumber, String paymentType, String comment, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.postalcode = postalcode;
        this.city = city;
        this.address = address;
        this.phonenumber = phonenumber;
        this.paymentType = paymentType;
        this.comment = comment;
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
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

    public String getShipmentAddress() {
        return shipmentAddress;
    }

    public void setShipmentAddress(String shipmentAddress) {
        this.shipmentAddress = shipmentAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckoutForm that = (CheckoutForm) o;
        return Objects.equals(firstname, that.firstname) &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(postalcode, that.postalcode) &&
                Objects.equals(city, that.city) &&
                Objects.equals(address, that.address) &&
                Objects.equals(shipmentPostalcode, that.shipmentPostalcode) &&
                Objects.equals(shipmentCity, that.shipmentCity) &&
                Objects.equals(shipmentAddress, that.shipmentAddress) &&
                Objects.equals(phonenumber, that.phonenumber) &&
                Objects.equals(paymentType, that.paymentType) &&
                Objects.equals(comment, that.comment) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, postalcode, city, address, shipmentPostalcode, shipmentCity, shipmentAddress, phonenumber, paymentType, comment, email);
    }

    @Override
    public String toString() {
        return "CheckoutForm{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", postalcode='" + postalcode + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", shipmentPostalcode='" + shipmentPostalcode + '\'' +
                ", shipmentCity='" + shipmentCity + '\'' +
                ", shipmentAddress='" + shipmentAddress + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", comment='" + comment + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

}
