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

import java.util.*;

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

        List<Product> products = Arrays.asList(
                DevDataFactory.createProduct("Safran 0.5g", "Pot de 1 gramme de safran", null, 15d, null),
                DevDataFactory.createProduct("Safran 1g", "Pot de 1 gramme de safran", null, 35d, null),
                DevDataFactory.createProduct("Safran 2g", "Pot de 1 gramme de safran", null, 60d, null),
                DevDataFactory.createProduct("Sel au safran 100g", "Sel parfumé au safran, peut être utilisé pour saler", null, 15d, null),
                DevDataFactory.createProduct("Sirop de safran 35ml", "Sirop de safran, peut être utilisé pour siroter", null, 15d, null)
        );

        for (Product p : products) {
            productRepository.save(p);
        }

    }

    private void populateOrderRepository() {

        Random rand = new Random();
        DateTime start = new DateTime();
        List<Product> products = productRepository.findAll();

        for (int i = 0; i < 50; i++) {

            ArrayList<Product> prds = new ArrayList<>();
            HashMap<Long, Integer> quantities = new HashMap<>();
            for (int j = 0; j < rand.ints(1, 10).iterator().next(); j++) {
                prds.add(products.get(rand.nextInt(products.size())));
            }

            CommercialOrder order = DevDataFactory.createOrder(start.minusDays(i).toDate(), prds, quantities, null,
                    null, null, null, null, null);

            orderRepository.save(order);
        }
    }


}
