package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.entities.*;
import org.remipassmoilesel.safranlices.forms.CheckoutForm;
import org.remipassmoilesel.safranlices.repositories.ExpenseRepository;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.utils.Mailer;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class StaticController {

    @Value("${app.mail.mainAdress}")
    private String mainMailAdress;

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

    @RequestMapping(Mappings.OUR_SAFRAN)
    public String showProducts(Model model) {
        Mappings.includeMappings(model);
        return Templates.OUR_SAFRAN;
    }

    @RequestMapping(Mappings.OUR_EXPLOITATION)
    public String showExploitation(Model model) {
        Mappings.includeMappings(model);
        return Templates.EXPLOITATION;
    }

    @RequestMapping(value = Mappings.LEGAL_MENTIONS)
    public String showLegalMentions(Model model) {
        Mappings.includeMappings(model);
        return Templates.LEGAL_MENTIONS;
    }

}
