package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class StaticController {

    private static final String TERMS_PDF_PATH = "/terms-of-sales/terms-of-sale.pdf";

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

    @RequestMapping(value = Mappings.TERMS_OF_SALES)
    public void showTermsOfSale(HttpServletResponse response) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(TERMS_PDF_PATH);
        Utils.pdfResponse(response, inputStream);
    }

}
