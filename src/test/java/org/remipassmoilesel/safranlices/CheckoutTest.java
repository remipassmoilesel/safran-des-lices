package org.remipassmoilesel.safranlices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.controllers.MainController;
import org.remipassmoilesel.safranlices.entities.Expense;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ExpenseRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.utils.DevDataFactory;
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
public class CheckoutTest {

    private MockMvc mockMvc;

    @Autowired
    private MainController mainController;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();
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

        // create fake expenses
        Double ex1price = 24d;
        Double ex2price = 36d;

        expenseRepository.deleteAll();
        expenseRepository.save(new Expense("", ex1price));
        expenseRepository.save(new Expense("", ex2price));

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
        response = mockMvc.perform(get(Mappings.CHECKOUT)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME))
                .andReturn();

        Double total = (Double) response.getModelAndView().getModel().get("total");
        assertEquals(total.doubleValue(),
                p1qtty * p1price + p2qtty * p2price + ex1price + ex2price, 0);

        // try to set payment confirmed when basket is empty
        mockMvc.perform(get(Mappings.CHECKOUT_CONFIRMED))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get(Mappings.CHECKOUT_FAILED))
                .andExpect(status().is3xxRedirection());


    }

}
