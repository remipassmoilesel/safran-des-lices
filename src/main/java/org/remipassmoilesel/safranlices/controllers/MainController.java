package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
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

    @RequestMapping(Mappings.ROOT)
    public void showIndex() {

    }

}
