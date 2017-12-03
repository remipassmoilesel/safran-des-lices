package org.remipassmoilesel.safranlices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.controllers.BillingController;
import org.remipassmoilesel.safranlices.controllers.OrderController;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.entities.ShippingCost;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.dataLoaders.DevDataFactory;
import org.remipassmoilesel.safranlices.repositories.ShippingCostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by remipassmoilesel on 09/07/17.
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(SafranLicesApplication.DEV_PROFILE)
public class BillingTest {

    private MockMvc mockMvc;

    @Autowired
    private OrderController orderController;

    @Autowired
    private BillingController checkoutController;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingCostRepository shippingCostRepository;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController, checkoutController).build();
    }

    @Test
    public void testCheckout() throws Exception {

        // create fake products
        Integer p1qtty = 5;
        Double p1price = 25d;

        Integer p2qtty = 4;
        Double p2price = 55d;

        double shipCost = 5;
        shippingCostRepository.deleteAll();
        shippingCostRepository.save(new ShippingCost(0d,50000d, 5d));


        Product p1 = DevDataFactory.createProduct(null, null, null, p1price, null);
        Product p2 = DevDataFactory.createProduct(null, null, null, p2price, null);
        productRepository.save(p1);
        productRepository.save(p2);

        orderRepository.deleteAll();

        // create a session
        MvcResult response = mockMvc.perform(get(Mappings.BASKET))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) response.getRequest().getSession();

        // add products to cart
        mockMvc.perform(get(Mappings.BASKET)
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addToCart", "true")
                .param("qtty", p1qtty.toString())
                .param("id", p1.getId().toString()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get(Mappings.BASKET)
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addToCart", "true")
                .param("qtty", p2qtty.toString())
                .param("id", p2.getId().toString()))
                .andExpect(status().is3xxRedirection());

        // show basket
        response = mockMvc.perform(get(Mappings.BILLING_FORM)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME))
                .andReturn();

        Double total = (Double) response.getModelAndView().getModel().get("total");
        assertEquals(total.doubleValue(),
                p1qtty * p1price + p2qtty * p2price + shipCost, 0);

    }


    @Test
    public void testProducts() throws Exception {

        // show products
        MvcResult result = mockMvc.perform(get(Mappings.ORDER))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME))
                .andReturn();

        ArrayList<Product> sortedProducts = (ArrayList<Product>) result.getModelAndView().getModel().get("products");
        assertTrue(sortedProducts.size() > 0);
    }


}
