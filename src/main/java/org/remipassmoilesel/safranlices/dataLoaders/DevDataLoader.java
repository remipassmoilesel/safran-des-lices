package org.remipassmoilesel.safranlices.dataLoaders;

import org.joda.time.DateTime;
import org.remipassmoilesel.safranlices.entities.Basket;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.repositories.ShippingCostRepository;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Iterator;
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
    private ShippingCostRepository shippingCostRepository;

    @Autowired
    private Environment env;

    @Value("${app.db-name}")
    private String dbName;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        logger.warn("Database name: " + dbName);

        if (Utils.isDevProfileEnabled(env) == false) {
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

        if (orderRepository.count() < 1) {
            populateOrderRepository();
            logger.warn("-- Fake orders added");
        }

    }

    private void populateProductRepository() {

        List<Product> sampleProducts = DevDataFactory.createSampleProductList();

        for (Product p : sampleProducts) {
            productRepository.save(p);
        }

    }

    private void populateOrderRepository() {

        Random rand = new Random();
        DateTime start = new DateTime();
        List<Product> products = productRepository.findAll(false);

        for (int i = 0; i < 50; i++) {

            Basket basket = new Basket();

            Integer numberOfProductsOrdered = rand.ints(1, 10).iterator().next();
            Iterator<Integer> randomQuantities = rand.ints(1, 6).iterator();

            for (int j = 0; j < numberOfProductsOrdered; j++) {
                Long randomProductId = products.get(rand.nextInt(products.size())).getId();
                Integer randomQuantity = randomQuantities.next();
                basket.addProduct(randomProductId, randomQuantity);
            }

            CommercialOrder order = DevDataFactory.createOrder(
                    start.minusDays(i).minusHours(i).toDate(),
                    products, basket, null,
                    null, null,
                    null, null,
                    null, null, null);

            order.setTotal(basket.computeTotalForBasket(products));
            order.setProcessed(i % 2 == 0);
            order.setPaid(rand.nextBoolean());

            orderRepository.save(order);
        }
    }


}
