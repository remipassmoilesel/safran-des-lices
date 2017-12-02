package org.remipassmoilesel.safranlices.dataLoaders;

import org.remipassmoilesel.safranlices.entities.*;
import org.remipassmoilesel.safranlices.utils.Utils;

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

        Product product = new Product(name, description, pictures, price, quantityAvailable);
        product.setNetWeight(10.5);
        product.setGrossWeight(5.5);

        return product;
    }

    public static CommercialOrder createOrder(Date date,
                                              List<Product> products, Basket basket,
                                              String address, String phonenumber,
                                              String firstName, String lastName,
                                              PaymentType paymentType,
                                              String comment, String email,
                                              List<Expense> expenses,
                                              String pdfBillName) {

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

        if (expenses == null) {
            expenses = Arrays.asList(createExpense());
        }

        if (pdfBillName == null) {
            pdfBillName = "bill.pdf";
        }

        String postalCode = "35582";
        String city = "Montpellier";
        String shipmentAddress = Utils.generateLoremIpsum(20);
        String shipmentPostalcode = "6658558";
        String shipmentCity = "Carcassonne";

        return new CommercialOrder(date, products, basket, address, postalCode, city, shipmentAddress,
                shipmentPostalcode, shipmentCity, phonenumber, firstName, lastName,
                paymentType, comment, email, expenses, pdfBillName);
    }

    public static List<Product> createSampleProductList() {
        return Arrays.asList(
                DevDataFactory.createProduct("Safran 0.5g", "Pot de 1 gramme de safran", null, 15d, null),
                DevDataFactory.createProduct("Safran 1g", "Pot de 1 gramme de safran", null, 35d, null),
                DevDataFactory.createProduct("Safran 2g", "Pot de 1 gramme de safran", null, 60d, null),
                DevDataFactory.createProduct("Sel au safran 100g", "Sel parfumé au safran, peut être utilisé pour saler", null, 15d, null),
                DevDataFactory.createProduct("Sirop de safran 35ml", "Sirop de safran, peut être utilisé pour siroter", null, 15d, null)
        );
    }

    public static Expense createExpense() {
        return createExpense("Frais de port", 5d);
    }

    public static Expense createExpense(String name, double value) {
        return new Expense(name, value);
    }
}
