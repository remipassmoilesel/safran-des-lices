package org.remipassmoilesel.safranlices.controllers;

import org.joda.time.DateTime;
import org.remipassmoilesel.safranlices.SafranLicesApplication;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.utils.DevDataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by remipassmoilesel on 12/06/17.
 */

@Component
public class DevDataLoader implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(DevDataLoader.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private Environment env;

    @Value("${app.db-name}")
    private String dbName;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        logger.warn("Database name: " + dbName);

        if (Arrays.asList(env.getActiveProfiles()).contains(SafranLicesApplication.DEV_PROFILE) == false) {
            return;
        }

        if (productRepository.count() < 1) {
            populateProductRepository();
            logger.warn("-- Fake products added");
        }

        if (orderRepository.count() < 1) {
            populateOrderRepository();
            logger.warn("-- Fake orders added");
        }

    }

    private void populateProductRepository() {

        for (int i = 0; i < 5; i++) {
            productRepository.save(DevDataFactory.createProduct(null, null, null, null, null));
        }

    }

    private void populateOrderRepository() {

        Random rand = new Random();
        DateTime start = new DateTime();
        List<Product> products = productRepository.findAll();

        for (int i = 0; i < 50; i++) {

            ArrayList<Product> prds = new ArrayList<>();
            for (int j = 0; j < rand.ints(1, 10).iterator().next(); j++) {
                prds.add(products.get(rand.nextInt(products.size())));
            }

            CommercialOrder order = DevDataFactory.createOrder(start.minusDays(i).toDate(), prds, null,
                    null, null, null, null, null);

            orderRepository.save(order);
        }
    }


}
