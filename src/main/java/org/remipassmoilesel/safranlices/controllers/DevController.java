package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.dataLoaders.DevDataFactory;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.OrderNotificationType;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ExpenseRepository;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.utils.Mailer;
import org.remipassmoilesel.safranlices.utils.PdfBillGenerator;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

/**
 * Controller used for development purposes
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class DevController {

    private static final Logger logger = LoggerFactory.getLogger(DevController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private Mailer mailer;

    @Autowired
    private Environment env;

    @Autowired
    private PdfBillGenerator billGenerator;

    @RequestMapping("/generate-bill")
    @ResponseBody
    public void generateBill(HttpServletResponse response)
            throws Exception {

        if (Utils.isDevProfileEnabled(env) == false) {
            return;
        }

        // create a fake order
        List<Product> products = productRepository.findAll(false);

        CommercialOrder order = DevDataFactory.createOrder(null, products, null, null,
                null, null, null, null, null, null, null);

        // generate pdf
        Path pdfPath = billGenerator.generateBill(order, products, 88.8);

        Utils.pdfResponse(response, pdfPath);
    }


    @RequestMapping("/send-mail-example")
    @ResponseBody
    public void sendAdminMailExample(@RequestParam(value = "type", required = true) String type)
            throws Exception {

        if (Utils.isDevProfileEnabled(env) == false) {
            return;
        }

        List<Product> products = productRepository.findAll(false);
        CommercialOrder order = DevDataFactory.createOrder(null, products, null, null, null,
                null, null, null, null, null, null);

        // generate pdf
        Path pdfPath = billGenerator.generateBill(order, products, 88.8);

        if ("admin".equals(type)) {
            mailer.sendAdminNotification(order, pdfPath.getFileName().toString());
        } else if ("client-order-confirmed".equals(type)) {
            mailer.sendClientNotification(OrderNotificationType.ORDER_CONFIRMED,
                    order, pdfPath.getFileName().toString());
        } else if ("client-order-sent".equals(type)) {
            mailer.sendClientNotification(OrderNotificationType.ORDER_SENT, order);
        } else {
            throw new Exception("Unknown type: " + type);
        }

    }

    @RequestMapping("/show-mail-example")
    public String showAdminMailExample(Model model, @RequestParam(value = "type", required = true) String type) throws Exception {

        if (Utils.isDevProfileEnabled(env) == false) {
            return "redirect:" + Mappings.ROOT;
        }

        List<Product> products = productRepository.findAll(false);

        CommercialOrder order = DevDataFactory.createOrder(null, products, null, null,
                null, null, null, null, null, null, null);
        model.addAttribute("order", order);

        HashMap<Product, Integer> productsWithQuantities = Utils.mapProductWithQuantities(products, order);
        model.addAttribute("productsWithQuantities", productsWithQuantities);

        if ("admin".equals(type)) {
            return "mail/admin";
        } else if ("order-confirmed".equals(type)) {
            return "mail/orderConfirmed";
        } else if ("order-failed".equals(type)) {
            return "mail/orderFailed";
        } else if ("order-sent".equals(type)) {
            return "mail/orderSent";
        } else {
            throw new Exception("Unknown type: " + type);
        }

    }


}
