package org.remipassmoilesel.safranlices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.dataLoaders.DevDataFactory;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by remipassmoilesel on 07/07/17.
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(SafranLicesApplication.DEV_PROFILE)
public class CommercialOrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setup() throws IOException {
        orderRepository.deleteAll();
    }

    @Test
    public void testInsert() throws Exception {

        // get products
        List<Product> products = productRepository.findAll(false);

        int nbr = 10;
        for (int i = 0; i < nbr; i++) {
            CommercialOrder order = DevDataFactory.createOrder(null, products, null,
                    null, null, null,
                    null, null, null, null, null);
            orderRepository.save(order);
        }

        assertTrue(orderRepository.findAll().size() == nbr);

    }


}
