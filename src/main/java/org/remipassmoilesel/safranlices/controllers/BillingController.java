package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.entities.*;
import org.remipassmoilesel.safranlices.forms.CheckoutForm;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.repositories.ShippingCostRepository;
import org.remipassmoilesel.safranlices.utils.Mailer;
import org.remipassmoilesel.safranlices.utils.PdfBillGenerator;
import org.remipassmoilesel.safranlices.utils.ThreadExecutor;
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class BillingController {

    private String AUTHORIZED_BILL_ID_SATTR = "authorized_bill";

    private static final Logger logger = LoggerFactory.getLogger(BillingController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingCostRepository shippingCostRepository;

    @Autowired
    private ThreadExecutor executor;

    @Autowired
    private Mailer mailer;

    @Autowired
    private Environment env;

    @Autowired
    private PdfBillGenerator billGenerator;

    @Value("${app.mail.mainAdress}")
    private String mainMailAdress;


    /**
     * Show billing form with current total or order,
     * where customer can enter his personal informations
     *
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = Mappings.BILLING_FORM, method = RequestMethod.GET)
    public String showCheckout(
            HttpSession session,
            Model model) {

        Basket basket = Basket.getBasketOrCreate(session);

        // check if basket is not empty
        if (basket.getNumberOfProducts() < 1) {
            return "redirect:" + Mappings.BASKET;
        }

        List<Product> products = productRepository.findAll(false);

        // total
        double total = basket.computeTotal(products);
        model.addAttribute("total", total);

        Mappings.includeMappings(model);
        return Templates.BILLING_FORM;
    }

    /**
     * Save order and redirect client
     *
     * @param checkoutForm
     * @param results
     * @param session
     * @param model
     * @return
     * @throws NoSuchAlgorithmException
     */
    @RequestMapping(value = Mappings.BILLING_FORM, method = RequestMethod.POST)
    public String processBilling(
            @Valid CheckoutForm checkoutForm,
            BindingResult results,
            HttpSession session,
            Model model) throws NoSuchAlgorithmException {

        Basket basket = Basket.getBasketOrCreate(session);

        // check if basket is not empty
        if (basket.getNumberOfProducts() < 1) {
            return "redirect:" + Mappings.BASKET;
        }

        // check form
        if (results.hasErrors()) {

            // TODO: show errors
            logger.error("Form errors: " + results.getAllErrors(), new Exception());
            model.addAttribute("errors", results.getAllErrors());

            Mappings.includeMappings(model);
            return "redirect:" + Mappings.BILLING_FORM;
        }

        // save order
        ArrayList<Product> products = new ArrayList<>();
        for (Long pid : basket.getProductIds()) {
            products.add(productRepository.findOne(pid));
        }

        PaymentType paymentType = PaymentType.valueOf(checkoutForm.getPaymentType());

        Date orderDate = new Date();
        String billId = billGenerator.getPdfName(orderDate,
                checkoutForm.getLastname(),
                checkoutForm.getFirstname());

        CommercialOrder order = new CommercialOrder(
                orderDate,
                products,
                basket,
                checkoutForm.getAddress(),
                checkoutForm.getPostalcode(),
                checkoutForm.getCity(),
                checkoutForm.getShipmentAddress(),
                checkoutForm.getShipmentPostalcode(),
                checkoutForm.getShipmentCity(),
                checkoutForm.getPhonenumber(),
                checkoutForm.getFirstname(),
                checkoutForm.getLastname(),
                paymentType,
                checkoutForm.getComment(),
                checkoutForm.getEmail(),
                billId);

        double total = basket.computeTotal(products);

//        double shippingCosts = Utils.computeShippingCosts(shippingCostRepository, productsWithQuantities);
//        double totalWeight = Utils.computeTotalWeight(productsWithQuantities);
//        model.addAttribute("shippingCosts", shippingCosts);
//        model.addAttribute("totalWeight", shippingCosts);


        order.setTotal(total);
        order.setProcessed(false);
        order.setPaid(false);
        orderRepository.save(order);

        // include order in session
        session.setAttribute("order", order);

        executor.execute(() -> {
            this.generatePdfBillThenNotify(order, products, total);
        });

        return "redirect:" + Mappings.CHECKOUT_END;

    }

    @RequestMapping(value = Mappings.CHECKOUT_END, method = RequestMethod.GET)
    public String checkoutEnd(HttpSession session, Model model) {

        CommercialOrder order = (CommercialOrder) session.getAttribute(Basket.ORDER_SATTR);
        if (order == null) {
            return "redirect:" + Mappings.BASKET;
        }

        String billId = billGenerator.getPdfName(order);
        session.setAttribute(AUTHORIZED_BILL_ID_SATTR, billId);

        List<Product> products = productRepository.findAll(false);

        Basket basket = Basket.getBasketOrCreate(session);
        double total = basket.computeTotal(products);

        model.addAttribute("order", order);
        model.addAttribute("total", total);
        model.addAttribute("billId", billId);

        Mappings.includeMappings(model);
        return Templates.CHECKOUT_END;
    }

    @RequestMapping(value = Mappings.SHOW_BILL, method = RequestMethod.GET)
    public String showBill(@RequestParam(value = "id") String billId,
                           HttpSession session,
                           HttpServletResponse response) throws IOException {

        // check if user authorization
        String authorizedBill = (String) session.getAttribute(AUTHORIZED_BILL_ID_SATTR);

        if (authorizedBill == null || billId.equals(authorizedBill) == false) {
            logger.error("Access refused on bill display: " + billId);
            return "redirect:" + Mappings.ROOT;
        }

        // display bill
        Path path = billGenerator.getPdfAbsolutePath(authorizedBill);

        Utils.pdfResponse(response, path);
        return null;
    }

    private void generatePdfBillThenNotify(
            CommercialOrder order,
            List<Product> products,
            double total) {

        try {
            billGenerator.generateBill(order, products, total);
        } catch (Exception e) {
            logger.error("Unable to generate bill", e);
        }

        String billId = billGenerator.getPdfName(order);

        // send notification to client
        try {
            mailer.sendClientNotification(OrderNotificationType.ORDER_CONFIRMED, order, billId);
        } catch (Exception e) {
            logger.error("Unable to send mail notification", e);
        }

        // send admin notification
        try {
            mailer.sendAdminNotification(order, billId);
        } catch (Exception e) {
            logger.error("Unable to send mail notification", e);
        }

    }

}
