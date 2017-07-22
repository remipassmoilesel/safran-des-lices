package org.remipassmoilesel.safranlices.forms;

import javax.validation.constraints.*;
import java.util.Objects;

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

    @NotNull
    @Size(min = 2)
    @Pattern(regexp = "\\+?[0-9]{2,}")
    private String phonenumber;

    @NotNull
    @Size(min = 2)
    private String paymentType;


    private String comment;
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

    @Override
    public String toString() {
        return "CheckoutForm{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", postalcode='" + postalcode + '\'' +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", phonenumber='" + phonenumber + '\'' +
                ", paymentType='" + paymentType + '\'' +
                ", comment='" + comment + '\'' +
                ", email='" + email + '\'' +
                '}';
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
                Objects.equals(phonenumber, that.phonenumber) &&
                Objects.equals(paymentType, that.paymentType) &&
                Objects.equals(comment, that.comment) &&
                Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstname, lastname, postalcode, city, address, phonenumber, paymentType, comment, email);
    }

}
