package org.remipassmoilesel.safranlices;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.controllers.MainController;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
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
    private MainController mainController;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();
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
        List<Product> products = productRepository.findAll();

        mockMvc.perform(get(Mappings.BASKET)
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("addToCart", "true")
                .param("qtty", String.valueOf(qtty))
                .param("id", String.valueOf(products.get(0).getId())));

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

        HashMap<Long, Integer> basket = (HashMap<Long, Integer>) response.getRequest().getSession().getAttribute(MainController.BASKET_SATTR);
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

        basket = (HashMap<Long, Integer>) response.getRequest().getSession().getAttribute(MainController.BASKET_SATTR);
        assertTrue(basket.size() == 2);
        assertTrue(basket.keySet().iterator().next() == products.get(1).getId());

        // empty basket
        mockMvc.perform(get(Mappings.BASKET)
                .session(session)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("reset", "true"))
                .andExpect(status().is3xxRedirection());

        basket = (HashMap<Long, Integer>) response.getRequest().getSession().getAttribute(MainController.BASKET_SATTR);
        assertTrue(basket.size() == 0);


    }

}
