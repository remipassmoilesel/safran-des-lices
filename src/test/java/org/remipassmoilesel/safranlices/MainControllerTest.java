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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by remipassmoilesel on 07/07/17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(SafranLicesApplication.DEV_PROFILE)
public class MainControllerTest {

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
    public void getPages() throws Exception {

        mockMvc.perform(get(Mappings.WELCOME))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME));

        mockMvc.perform(get(Mappings.PRODUCTS))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME));

        mockMvc.perform(get(Mappings.EXPLOITATION))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME));

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
