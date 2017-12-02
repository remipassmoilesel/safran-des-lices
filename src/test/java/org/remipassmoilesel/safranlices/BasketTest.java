package org.remipassmoilesel.safranlices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.controllers.OrderController;
import org.remipassmoilesel.safranlices.dataLoaders.DevDataFactory;
import org.remipassmoilesel.safranlices.entities.Basket;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.entities.ShippingCost;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
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
import java.util.HashMap;
import java.util.List;

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
public class BasketTest {

    private MockMvc mockMvc;

    @Autowired
    private OrderController checkoutController;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShippingCostRepository shippingCostRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(checkoutController).build();
    }

    @Test
    public void testBasketModifications() throws Exception {

        // show basket
        MvcResult response = mockMvc.perform(get(Mappings.BASKET))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME)).andReturn();

        MockHttpSession session = (MockHttpSession) response.getRequest().getSession();

        // add product to basket
        Integer qtty = 6;
        List<Product> products = productRepository.findAll(false);

        mockMvc.perform(get(Mappings.BASKET)
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addToCart", "true")
                .param("qtty", String.valueOf(qtty))
                .param("id", String.valueOf(products.get(0).getId())))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get(Mappings.BASKET)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .session(session)
                .param("addToCart", "true")
                .param("qtty", String.valueOf(qtty))
                .param("id", String.valueOf(products.get(1).getId())))
                .andExpect(status().is3xxRedirection());

        response = mockMvc.perform(get(Mappings.BASKET)
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addToCart", "true")
                .param("qtty", String.valueOf(qtty))
                .param("id", String.valueOf(products.get(2).getId())))
                .andExpect(status().is3xxRedirection()).andReturn();

        HashMap<Long, Integer> basket = (HashMap<Long, Integer>) response.getRequest().getSession().getAttribute(Basket.BASKET_SATTR);
        Integer q = basket.values().iterator().next();
        Long pid = basket.keySet().iterator().next();
        assertTrue(basket.size() == 3);
        assertTrue(pid.equals(products.get(0).getId()));
        assertTrue(q.equals(qtty));

        // delete a product
        response = mockMvc.perform(get(Mappings.BASKET)
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("delete", "true")
                .param("id", String.valueOf(products.get(0).getId())))
                .andExpect(status().is3xxRedirection()).andReturn();

        basket = (HashMap<Long, Integer>) response.getRequest().getSession().getAttribute(Basket.BASKET_SATTR);
        assertTrue(basket.size() == 2);
        assertTrue(basket.keySet().iterator().next() == products.get(1).getId());

        // empty basket
        mockMvc.perform(get(Mappings.BASKET)
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("reset", "true"))
                .andExpect(status().is3xxRedirection());

        basket = (HashMap<Long, Integer>) response.getRequest().getSession().getAttribute(Basket.BASKET_SATTR);
        assertTrue(basket.size() == 0);

    }

    @Test
    public void testBasketTotal() throws Exception {

        // create fake products
        Integer p1qtty = 5;
        Double p1price = 25d;

        Integer p2qtty = 4;
        Double p2price = 55d;

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
        response = mockMvc.perform(get(Mappings.BASKET)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME))
                .andReturn();

        Double total = (Double) response.getModelAndView().getModel().get("total");
        assertEquals(total.doubleValue(),
                p1qtty * p1price + p2qtty * p2price, 0);
    }


    @Test
    public void computedWeightShouldBeExact() throws Exception {

        Basket basket = new Basket();
        List<Product> allProducts = new ArrayList<>();

        Product p1 = new Product();
        p1.setId(1l);
        p1.setGrossWeight(35d);
        basket.addProduct(p1, 2);
        allProducts.add(p1);


        Product p2 = new Product();
        p2.setId(1l);
        p2.setGrossWeight(65d);
        basket.addProduct(p2, 2);
        allProducts.add(p2);

        assertEquals(200d, basket.computeTotalWeight(allProducts), 0);
    }

    @Test
    public void computedShippingCostsShouldBeExact() throws Exception {

        // create a basket
        Basket basket = new Basket();

        // create a fake product
        List<Product> allProducts = new ArrayList<>();
        Product product1 = new Product();
        product1.setGrossWeight(100d);
        allProducts.add(product1);

        // create fake shipping costs
        List<ShippingCost> allShippingCosts = new ArrayList<>();

        ShippingCost s1 = new ShippingCost(0d, 200d, 5d);
        ShippingCost s2 = new ShippingCost(200d, 400d, 10d);
        ShippingCost s3 = new ShippingCost(400d, 9999999999d, 15d);
        allShippingCosts.add(s1);
        allShippingCosts.add(s2);
        allShippingCosts.add(s3);

        basket.addProduct(product1, 1);

        assertEquals(5d, basket.computeShippingCosts(allProducts, allShippingCosts), 0);

        basket.clear();
        basket.addProduct(product1, 2);

        assertEquals(10d, basket.computeShippingCosts(allProducts, allShippingCosts), 0);

        basket.clear();
        basket.addProduct(product1, 10);

        assertEquals(15d, basket.computeShippingCosts(allProducts, allShippingCosts), 0);

    }


}
