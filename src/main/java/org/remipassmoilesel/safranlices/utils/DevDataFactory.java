package org.remipassmoilesel.safranlices.utils;

import org.remipassmoilesel.safranlices.entities.Basket;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.PaymentType;
import org.remipassmoilesel.safranlices.entities.Product;

import java.util.*;

/**
 * Created by remipassmoilesel on 07/07/17.
 */
public class DevDataFactory {

    private static Random rand = new Random();
    private static List<String> fakePictures = Arrays.asList(
            "/img/DSC_0040.jpg",
            "/img/DSC_0038.jpg",
            "/img/DSC_0039.jpg",
            "/img/DSC_0040.jpg",
            "/img/DSC_0076.jpg",
            "/img/DSC_0077.jpg"
    );

    public static Product createProduct(String name, String description, String pictures, Double price, Integer quantityAvailable) {

        if (name == null) {
            name = Utils.generateLoremIpsum(20);
        }

        if (description == null) {
            description = Utils.generateLoremIpsum(200);
        }

        if (pictures == null) {
            ArrayList<String> pics = new ArrayList<>();
            int nbr = rand.ints(1, 6).iterator().next();
            for (int i = 0; i < nbr; i++) {
                pics.add(fakePictures.get(rand.nextInt(fakePictures.size())));
            }

            pictures = String.join(",", pics);
        }

        if (price == null) {
            price = rand.doubles(2, 139).iterator().next();
        }

        if (quantityAvailable == null) {
            quantityAvailable = rand.nextInt(200);
        }

        return new Product(name, description, pictures, price, quantityAvailable);
    }

    public static CommercialOrder createOrder(Date date,
                                              List<Product> products, Basket basket,
                                              String address, String phonenumber,
                                              String firstName, String lastName,
                                              PaymentType paymentType,
                                              String comment, String email) {

        if (date == null) {
            date = new Date();
        }

        if (products == null) {
            products = new ArrayList<>();
            int nbr = rand.ints(1, 6).iterator().next();
            for (int i = 0; i < nbr; i++) {
                products.add(createProduct(null, null, null, null, null));
            }
        }

        if (basket == null) {
            basket = new Basket();
            for (Product p : products) {
                basket.addProduct(p.getId(), rand.nextInt(25));
            }
        }

        if (address == null) {
            address = Utils.generateLoremIpsum(50);
        }

        if (phonenumber == null) {
            phonenumber = Utils.generateLoremIpsum(20);
        }

        if (firstName == null) {
            firstName = Utils.generateLoremIpsum(20);
        }

        if (lastName == null) {
            lastName = Utils.generateLoremIpsum(20);
        }

        if (paymentType == null) {
            paymentType = PaymentType.values()[rand.nextInt(PaymentType.values().length)];
        }

        if (lastName == null) {
            lastName = Utils.generateLoremIpsum(200);
        }

        if (email == null) {
            email = "jeanEdouard" + System.currentTimeMillis() + "@mail.com";
        }

        String postalCode = "35582";
        String city = "Montpellier";
        String shipmentAddress = Utils.generateLoremIpsum(20);
        String shipmentPostalcode = "6658558";
        String shipmentCity = "Carcassonne";

        return new CommercialOrder(date, products, basket, address, postalCode, city, shipmentAddress,
                shipmentPostalcode, shipmentCity, phonenumber, firstName, lastName,
                paymentType, comment, email);
    }

}
