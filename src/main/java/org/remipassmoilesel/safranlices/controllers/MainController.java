package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @RequestMapping(Mappings.TEMPLATE)
    public String showTemplate() {
        return Templates.MAIN_TEMPLATE;
    }

    @RequestMapping(Mappings.ROOT)
    public String showWelcome() {
        return Templates.WELCOME;
    }

    @RequestMapping(Mappings.PRODUCTS)
    public String showProducts() {
        return Templates.PRODUCTS;
    }

    @RequestMapping(Mappings.EXPLOITATION)
    public String showExploitation() {
        return Templates.EXPLOITATION;
    }

    @RequestMapping(Mappings.ORDER)
    public String showOrder() {
        return Templates.ORDER;
    }

}
