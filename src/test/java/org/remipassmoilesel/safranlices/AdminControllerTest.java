package org.remipassmoilesel.safranlices;

import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.remipassmoilesel.safranlices.controllers.AdminController;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Expense;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ExpenseRepository;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by remipassmoilesel on 07/07/17.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(SafranLicesApplication.DEV_PROFILE)
public class AdminControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private AdminController adminController;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Before
    public void setup() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
    }

    @Value("${app.admin-login}")
    private String adminLogin;

    @Value("${app.admin-password}")
    private String adminPassword;

    @Test
    public void testPages() throws Exception {

        mockMvc.perform(get(Mappings.ADMIN_LOGIN))
                .andExpect(status().isOk());

        mockMvc.perform(get(Mappings.ADMIN_PAGE))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("ordersToProcess"))
                .andExpect(model().attributeExists("basketsToProcess"))
                .andExpect(model().attributeExists("ordersProcessed"))
                .andExpect(model().attributeExists("basketsProcessed"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("expenses"))
                .andExpect(model().attributeExists(Mappings.MODEL_ARGUMENT_NAME));

        mockMvc.perform(get(Mappings.ADMIN_MODIFICATION))
                .andExpect(status().is4xxClientError());

    }

    @Test
    @Transactional
    public void testModifications() throws Exception {

        // mark order as paid
        CommercialOrder o1 = orderRepository.findAll().get(0);
        o1.setPaid(false);
        o1.setProcessed(false);
        orderRepository.save(o1);

        mockMvc.perform(get(Mappings.ADMIN_MODIFICATION)
                .param("action", "paid")
                .param("value", "true")
                .param("id", o1.getId().toString()))
                .andExpect(status().is3xxRedirection());

        CommercialOrder no = orderRepository.getOne(o1.getId());
        assertTrue(no.isPaid() == true);

        // mark order as processed
        mockMvc.perform(get(Mappings.ADMIN_MODIFICATION)
                .param("action", "processed")
                .param("value", "true")
                .param("id", o1.getId().toString()))
                .andExpect(status().is3xxRedirection());

        no = orderRepository.getOne(o1.getId());
        assertTrue(no.isProcessed() == true);

        // modify product
        Double newPrice = 15.5d;
        Integer newQuantity = 55;
        Product p1 = productRepository.findAll(false).get(0);
        p1.setPrice(0d);
        p1.setQuantityAvailable(0);
        productRepository.save(p1);

        mockMvc.perform(get(Mappings.ADMIN_MODIFICATION)
                .param("action", "product")
                .param("price", newPrice.toString())
                .param("quantity", newQuantity.toString())
                .param("id", p1.getId().toString()))
                .andExpect(status().is3xxRedirection());

        Product np1 = productRepository.getOne(p1.getId());
        assertTrue(np1.getPrice().equals(newPrice));
        assertTrue(np1.getQuantityAvailable().equals(newQuantity));

        // modify expense
        String newName = "What an expense";
        Double newValue = 522d;
        Expense e1 = expenseRepository.findAll().get(0);
        e1.setName("");
        e1.setValue(0d);
        expenseRepository.save(e1);

        mockMvc.perform(get(Mappings.ADMIN_MODIFICATION)
                .param("action", "expense")
                .param("name", newName)
                .param("value", newValue.toString())
                .param("id", e1.getId().toString()))
                .andExpect(status().is3xxRedirection());

        Expense ne1 = expenseRepository.getOne(e1.getId());
        assertTrue(ne1.getName().equals(newName));
        assertTrue(ne1.getValue().equals(newValue));

        // bad modification
        mockMvc.perform(get(Mappings.ADMIN_MODIFICATION)
                .param("action", "expense")
                .param("name", newName)
                .param("value", newValue.toString()))
                .andExpect(status().is4xxClientError());

    }

}
