package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.csv.ProductsExporter;
import org.remipassmoilesel.safranlices.csv.ProductsImporter;
import org.remipassmoilesel.safranlices.csv.ShippingCostsExporter;
import org.remipassmoilesel.safranlices.csv.ShippingCostsImporter;
import org.remipassmoilesel.safranlices.entities.Basket;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.entities.ShippingCost;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.repositories.ShippingCostRepository;
import org.remipassmoilesel.safranlices.utils.Mailer;
import org.remipassmoilesel.safranlices.utils.PdfBillGenerator;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
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
    private ShippingCostRepository shippingCostRepository;

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

        // add products
        model.addAttribute("products", products);

        Mappings.includeMappings(model);
        return Templates.ADMIN_CONFIGURE_SALES;
    }

    @RequestMapping(Mappings.ADMIN_SHOW_ORDER)
    public String displayOrder(
            @RequestParam(value = "id") Long id,
            Model model) {

        CommercialOrder order = orderRepository.getOne(id);
        List<Product> allProducts = productRepository.findAll(false);

        Basket basket = Basket.fromOrder(order);

        // add order and products
        model.addAttribute("order", order);
        model.addAttribute("basket", basket.mapProductWithQuantities(allProducts));

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
        response.setHeader("Content-Disposition", "attachment; filename=\"products.csv\"");

        List<Product> products = productRepository.findAll(false);

        ProductsExporter pexporter = new ProductsExporter();
        BufferedWriter writer = new BufferedWriter(response.getWriter());

        pexporter.export(products, writer);
    }

    @RequestMapping(value = Mappings.ADMIN_UPLOAD_PRODUCTS)
    public String uploadProducts(
            @RequestParam("productsList") MultipartFile productsList,
            HttpServletRequest request) throws IOException {

        if (productsList.getOriginalFilename().toLowerCase().endsWith(".csv") == false) {
            throw new Error("Invalid extension: " + productsList.getOriginalFilename());
        }

        ProductsImporter pimporter = new ProductsImporter();
        pimporter.importProducts(productsList.getInputStream(), productRepository);

        String redirection = request.getHeader("referer");
        return "redirect:" + redirection;
    }

    @RequestMapping(value = Mappings.ADMIN_DOWNLOAD_SHIPPING_COSTS)
    public void downloadShippingCosts(HttpServletResponse response) throws IOException {

        // MANDATORY in order to keep utf-8 encoding
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"shipping-costs.csv\"");

        List<ShippingCost> shippingCosts = shippingCostRepository.findAll(false);

        ShippingCostsExporter exporter = new ShippingCostsExporter();
        BufferedWriter writer = new BufferedWriter(response.getWriter());

        exporter.export(shippingCosts, writer);
    }

    @RequestMapping(value = Mappings.ADMIN_UPLOAD_SHIPPING_COSTS)
    public String uploadShippingCosts(
            @RequestParam("shippingCostsList") MultipartFile shippingCostsList,
            HttpServletRequest request) throws IOException {

        if (shippingCostsList.getOriginalFilename().toLowerCase().endsWith(".csv") == false) {
            throw new Error("Invalid extension: " + shippingCostsList.getOriginalFilename());
        }

        ShippingCostsImporter pimporter = new ShippingCostsImporter();
        pimporter.importShippingCosts(shippingCostsList.getInputStream(), shippingCostRepository);

        String redirection = request.getHeader("referer");
        return "redirect:" + redirection;
    }

}
