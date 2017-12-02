package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.csv.ProductsExporter;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Expense;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private Mailer mailer;

    @Autowired
    private PdfBillGenerator billGenerator;


    @RequestMapping(value = Mappings.ADMIN_LOGIN, method = RequestMethod.GET)
    public String showLoginPage(Model model) throws IOException {

        Mappings.includeMappings(model);
        return Templates.ADMIN_LOGIN;
    }

    @RequestMapping(Mappings.ADMIN_ROOT)
    public String showAdminPage(Model model) {

        int displayLimit = 20;

        // orders non processed
        List<CommercialOrder> lastOrdersToProcess = orderRepository.findLasts(
                new PageRequest(0, displayLimit), false).getContent();

        model.addAttribute("ordersToProcess", lastOrdersToProcess);

        // orders processed
        List<CommercialOrder> lastOrdersProcessed = orderRepository.findLasts(
                new PageRequest(0, displayLimit), true).getContent();

        model.addAttribute("ordersProcessed", lastOrdersProcessed);

        Mappings.includeMappings(model);
        return Templates.ADMIN_WELCOME;
    }

    @RequestMapping(Mappings.ADMIN_CONFIGURE_SALES)
    public String configureSales(Model model) {

        List<Product> products = productRepository.findAll(false);
        List<Expense> expenses = expenseRepository.findAll(false);

        // add products
        model.addAttribute("products", products);

        // add expenses
        model.addAttribute("expenses", expenses);

        Mappings.includeMappings(model);
        return Templates.ADMIN_CONFIGURE_SALES;
    }

    @RequestMapping(Mappings.ADMIN_SHOW_ORDER)
    public String displayOrder(
            @RequestParam(value = "id") Long id,
            Model model) {

        CommercialOrder order = orderRepository.getOne(id);
        List<Product> products = productRepository.findAll(false);

        HashMap<Product, Integer> basket = Utils.mapProductWithQuantities(products, order);

        // add products
        model.addAttribute("order", order);

        // add expenses
        model.addAttribute("basket", basket);


        Mappings.includeMappings(model);
        return Templates.ADMIN_SHOW_ORDER;
    }

    @RequestMapping(Mappings.ADMIN_SHOW_BILL)
    public void showBill(
            HttpServletResponse response,
            @RequestParam(value = "id") String id) throws IOException {

        // display bill
        Path path = billGenerator.getPdfAbsolutePath(id);

        Utils.pdfResponse(response, path);
    }

    @RequestMapping(Mappings.ADMIN_SHOW_ALL_BILLS)
    public String showAllBills(Model model) throws IOException {

        List<String> billNames = Files.list(billGenerator.getBillRootDirectory())
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        Collections.sort(billNames);
        Collections.reverse(billNames);

        model.addAttribute("billNames", billNames);

        Mappings.includeMappings(model);
        return Templates.ADMIN_SHOW_ALL_BILLS;
    }

    @RequestMapping(value = Mappings.ADMIN_DOWNLOAD_PRODUCTS)
    public void downloadProducts(HttpServletResponse response) throws IOException {

        // MANDATORY in order to keep utf-8 encoding
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition","attachment; filename=\"products.csv\"");

        List<Product> products = productRepository.findAll(false);

        ProductsExporter pe = new ProductsExporter();
        BufferedWriter writer = new BufferedWriter(response.getWriter());

        pe.export(products, writer);
    }

    @RequestMapping(Mappings.ADMIN_ACTION)
    public String modify(Model model,
                         HttpServletResponse response,
                         HttpServletRequest request,
                         @RequestParam(value = "action") String action,
                         @RequestParam(value = "id") Long id,
                         @RequestParam(value = "value", required = false) String value,
                         @RequestParam(value = "price", required = false) Double price,
                         @RequestParam(value = "name", required = false) String name,
                         @RequestParam(value = "quantity", required = false) Integer quantity) throws IOException, MessagingException {

        String redirection = request.getHeader("referer");

        // mark as paid or non paid
        if (action.equals("paid")) {
            CommercialOrder order = orderRepository.findOne(id);
            order.setPaid(Boolean.valueOf(value));
            orderRepository.save(order);

            return "redirect:" + redirection;
        }

        // mark as processed or non processed
        if (action.equals("processed")) {
            CommercialOrder order = orderRepository.findOne(id);
            order.setProcessed(Boolean.valueOf(value));
            orderRepository.save(order);

            return "redirect:" + redirection;
        }

        // modify a product
        if (action.equals("product")) {
            Product p = productRepository.getOne(id);
            p.setPrice(price);
            p.setQuantityAvailable(quantity);
            productRepository.save(p);

            return "redirect:" + redirection;
        }

        // modify an expense
        if (action.equals("expense")) {
            Expense e = expenseRepository.getOne(id);
            e.setName(name);
            e.setValue(Double.valueOf(value));
            expenseRepository.save(e);

            return "redirect:" + redirection;
        }

        // modify an expense
        if (action.equals("notify")) {

            CommercialOrder order = orderRepository.getOne(id);
            mailer.sendClientNotification(OrderNotificationType.ORDER_SENT, order);

            order.setLastShipmentNotification(new Date());
            orderRepository.save(order);

            return "redirect:" + redirection;
        }

        // nothing to do
        response.sendError(400, "Bad request");
        return null;
    }


}
