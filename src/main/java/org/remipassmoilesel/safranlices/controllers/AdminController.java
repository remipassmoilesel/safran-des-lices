package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @RequestMapping(Mappings.ADMIN_PAGE)
    public String showTemplate(Model model) {

        Mappings.includeMappings(model);
        return Templates.ADMIN_TEMPLATE;
    }

    @RequestMapping(value = Mappings.ADMIN_LOGIN, method = RequestMethod.GET)
    public String showLoginPage(Model model) throws IOException {

        Mappings.includeMappings(model);
        return Templates.ADMIN_LOGIN;
    }

}
