package org.remipassmoilesel.safranlices;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.entities.ShippingCost;
import org.remipassmoilesel.safranlices.repositories.ShippingCostRepository;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(SafranLicesApplication.DEV_PROFILE)
public class UtilsTest {

    @Autowired
    private ShippingCostRepository shippingCostRepository;

    @Test
    public void computedWeightShouldBeExact() throws Exception {
        List<Product> products = new ArrayList<>();

        Product p1 = new Product();
        p1.setGrossWeight(35d);
        products.add(p1);

        Product p2 = new Product();
        p2.setGrossWeight(65d);
        products.add(p2);

        assertEquals(100d, Utils.getTotalWeight(products), 0);
    }

    @Test
    public void computedShippingCostsShouldBeExact() throws Exception {

        shippingCostRepository.deleteAll();

        ShippingCost s1 = new ShippingCost(0d, 200d, 5d);
        ShippingCost s2 = new ShippingCost(200d, 400d, 10d);
        ShippingCost s3 = new ShippingCost(400d, 9999999999d, 15d);

        shippingCostRepository.save(s1);
        shippingCostRepository.save(s2);
        shippingCostRepository.save(s3);

        Product product1 = new Product();
        product1.setGrossWeight(100d);

        assertEquals(5d, Utils.computeShippingCosts(shippingCostRepository,
                Arrays.asList(product1)), 0);

        assertEquals(10d, Utils.computeShippingCosts(shippingCostRepository,
                Arrays.asList(product1, product1)), 0);

        assertEquals(15d, Utils.computeShippingCosts(shippingCostRepository,
                Arrays.asList(product1, product1,
                        product1, product1, product1, product1)), 0);
    }


}
