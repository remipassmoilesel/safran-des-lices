package org.remipassmoilesel.safranlices.utils;

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
            "/img/fakeImg1.jpg",
            "/img/fakeImg3.jpg",
            "/img/fakeImg2.jpg"
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

    public static CommercialOrder createOrder(Date date, List<Product> products, String address, String phonenumber,
                                              String firstName, String lastName, PaymentType paymentType, String comment) {

        if (date == null) {
            date = new Date();
        }

        if (products == null) {
            ArrayList<Product> prds = new ArrayList<>();
            int nbr = rand.ints(1, 6).iterator().next();
            for (int i = 0; i < nbr; i++) {
                prds.add(createProduct(null, null, null, null, null));
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

        return new CommercialOrder(date, products, address, phonenumber, firstName, lastName, paymentType, comment);
    }

}
