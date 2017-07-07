package org.remipassmoilesel.safranlices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.utils.DevDataFactory;
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
public class CommercialOrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Before
    public void setup() throws IOException {
        orderRepository.deleteAll();
    }

    @Test
    public void testInsert() throws Exception {

        int nbr = 10;
        for (int i = 0; i < nbr; i++) {
            orderRepository.save(DevDataFactory.createOrder(null, null, null,
                    null, null, null, null, null, null));
        }

        assertTrue(orderRepository.findAll().size() == nbr);

    }


}
