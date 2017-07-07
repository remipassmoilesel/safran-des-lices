package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @RequestMapping(Mappings.TEMPLATE)
    public String showTemplate(Model model) {
        Mappings.includeMappings(model);
        return Templates.MAIN_TEMPLATE;
    }

    @RequestMapping(Mappings.ROOT)
    public String showWelcome(Model model) {
        Mappings.includeMappings(model);
        return Templates.WELCOME;
    }

    @RequestMapping(Mappings.PRODUCTS)
    public String showProducts(Model model) {
        Mappings.includeMappings(model);
        return Templates.PRODUCTS;
    }

    @RequestMapping(Mappings.EXPLOITATION)
    public String showExploitation(Model model) {
        Mappings.includeMappings(model);
        return Templates.EXPLOITATION;
    }

    @RequestMapping(Mappings.ORDER)
    public String showOrder(Model model) {
        Mappings.includeMappings(model);
        return Templates.ORDER;
    }

}
