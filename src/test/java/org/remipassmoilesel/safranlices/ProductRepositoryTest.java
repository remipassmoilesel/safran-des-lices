package org.remipassmoilesel.safranlices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.dataLoaders.DevDataFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by remipassmoilesel on 07/07/17.
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(SafranLicesApplication.DEV_PROFILE)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setup() throws IOException {
        //productRepository.deleteAll();
    }

    @Test
    public void testInsert() throws Exception {

        int nbr = 10;
        for (int i = 0; i < nbr; i++) {
            Product product = DevDataFactory.createProduct(null, null,
                    null, null, null);
            productRepository.save(product);
        }

        assertTrue(productRepository.findAll(false).size() >= nbr);

    }


}
