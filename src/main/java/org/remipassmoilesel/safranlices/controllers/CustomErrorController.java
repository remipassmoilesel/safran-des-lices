package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping(value = Mappings.ERROR)
    public String error(Model model) {
        Mappings.includeMappings(model);
        return Templates.ERROR;
    }

    @Override
    public String getErrorPath() {
        return Mappings.ERROR;
    }
}