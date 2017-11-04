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
public class MainController {

    public static final String PAYMENT_TOKEN_SATTR = "paymentToken";

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

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

    @RequestMapping(Mappings.ORDER)
    public String showOrder(Model model) {

        List<Product> products = productRepository.findAll(false);
        ArrayList<ArrayList<Product>> sorted = new ArrayList<>();

        int i = 0;
        int row = 3;
        ArrayList<Product> currentList = new ArrayList<>();
        for (Product p : products) {

            if (i >= row) {
                sorted.add(currentList);
                currentList = new ArrayList<>();
                i = 0;
            }

            currentList.add(p);
            i++;
        }

        sorted.add(currentList);

        model.addAttribute("products", sorted);

        Mappings.includeMappings(model);
        return Templates.ORDER;
    }

    @RequestMapping(value = Mappings.BASKET)
    public String showBasket(
            @RequestParam(required = false, name = "id") Long productId,
            @RequestParam(required = false, name = "addToCart") Boolean addToCart,
            @RequestParam(required = false, name = "qtty") Integer productQuantity,
            @RequestParam(required = false, name = "reset") Boolean reset,
            @RequestParam(required = false, name = "delete") Boolean delete,
            HttpSession session,
            Model model) {

        // current user basket: Article ID / Quantity
        Basket basket = Basket.getBasketOrCreate(session);

        // empty basket
        if (reset != null && reset == true) {
            basket.resetBasket(session);
            return "redirect:" + Mappings.BASKET;
        }

        // delete article
        if (delete != null && delete == true && productId != null) {
            basket.remove(productId);
            session.setAttribute("basket", basket);

            return "redirect:" + Mappings.BASKET;
        }

        // add something to cart
        if (addToCart != null && addToCart == true && productId != null && productQuantity != null) {

            basket.addProduct(productId, productQuantity);
            session.setAttribute(Basket.BASKET_SATTR, basket);

            return "redirect:" + Mappings.BASKET;
        }

        // map quantities and products
        List<Product> products = productRepository.findAll(false);
        HashMap<Product, Integer> productsWithQuantities = Utils.mapProductWithQuantities(products, basket);

        model.addAttribute("basket", productsWithQuantities);

        // expenses
        List<Expense> expenses = expenseRepository.findAll();
        model.addAttribute("expenses", expenses);

        // total
        double total = basket.computeTotalWithExpenses(products, expenses);
        model.addAttribute("total", total);

        Mappings.includeMappings(model);
        return Templates.BASKET;
    }


    @RequestMapping(value = Mappings.BILLING_FORM, method = RequestMethod.GET)
    public String showCheckout(
            HttpSession session,
            Model model) {

        Basket basket = Basket.getBasketOrCreate(session);

        // check if basket is not empty
        if (basket.size() < 1) {
            return "redirect:" + Mappings.BASKET;
        }

        List<Expense> expenses = expenseRepository.findAll();
        List<Product> products = productRepository.findAll(false);

        // total
        double total = basket.computeTotalWithExpenses(products, expenses);
        model.addAttribute("total", total);

        Mappings.includeMappings(model);
        return Templates.BILLING_FORM;
    }

    @RequestMapping(value = Mappings.BILLING_FORM, method = RequestMethod.POST)
    public String processCheckout(
            @Valid CheckoutForm checkoutForm,
            BindingResult results,
            HttpSession session,
            Model model) throws NoSuchAlgorithmException {

        Basket basket = Basket.getBasketOrCreate(session);

        // check if basket is not empty
        if (basket.size() < 1) {
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

        CommercialOrder order = new CommercialOrder(
                new Date(),
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
                checkoutForm.getEmail());

        List<Expense> expenses = expenseRepository.findAll();
        double total = basket.computeTotalWithExpenses(products, expenses);

        order.setTotal(total);
        order.setProcessed(false);
        order.setPaid(false);
        orderRepository.save(order);

        // include order in session
        session.setAttribute("order", order);

        // send admin notification
        try {
            mailer.sendAdminNotification(order);
        } catch (Exception e) {
            logger.error("Unable to send mail notification", e);
        }

        // payment is differed (check or transfer)
        if (PaymentType.BANK_CHECK == order.getPaymentType() || PaymentType.BANK_TRANSFER == order.getPaymentType()) {

            // send notification to client
            try {
                mailer.sendClientNotification(OrderNotificationType.ORDER_CONFIRMED, order);
            } catch (Exception e) {
                logger.error("Unable to send mail notification", e);
            }

            return "redirect:" + Mappings.CHECKOUT_END;
        }

        // payment online
        else {
            return "redirect:" + Mappings.CHECKOUT;
        }

    }

    @RequestMapping(value = Mappings.CHECKOUT_END, method = RequestMethod.GET)
    public String checkoutEnd(HttpSession session, Model model) {

        CommercialOrder order = (CommercialOrder) session.getAttribute(Basket.ORDER_SATTR);
        if (order == null) {
            return "redirect:" + Mappings.BASKET;
        }

        List<Product> products = productRepository.findAll(false);
        List<Expense> expenses = expenseRepository.findAll();

        Basket basket = Basket.getBasketOrCreate(session);
        double total = basket.computeTotalWithExpenses(products, expenses);

        model.addAttribute("order", order);
        model.addAttribute("total", total);

        Mappings.includeMappings(model);
        return Templates.CHECKOUT_END;
    }

    @RequestMapping(value = Mappings.CHECKOUT_CONFIRMED, method = RequestMethod.GET)
    public String checkoutConfirmed(Model model, HttpSession session,
                                    @RequestParam(name = "token", required = false) String token) {

        if (token == null) {
            return "redirect:" + Mappings.BASKET;
        }

        CommercialOrder order = (CommercialOrder) session.getAttribute(Basket.ORDER_SATTR);

        // check if basket is not empty
        if (order == null) {
            return "redirect:" + Mappings.BASKET;
        }

        Basket basket = Basket.getBasketOrCreate(session);

        // compute total
        List<Expense> expenses = expenseRepository.findAll();
        List<Product> products = productRepository.findAll(false);
        double total = basket.computeTotalWithExpenses(products, expenses);
        model.addAttribute("total", total);

        // reset basket
        basket.resetBasket(session);

        // test session token
        String stoken = (String) session.getAttribute(PAYMENT_TOKEN_SATTR);
        if (token == null || stoken == null || token.equals(stoken) == false) {
            Error err = new Error("Invalid token: " + token + " / stoken: " + stoken);
            logger.error("Invalid token: " + token + " / stoken: " + stoken, err);
            throw err;
        }

        // send notification to client
        try {
            mailer.sendClientNotification(OrderNotificationType.ORDER_CONFIRMED, order);
        } catch (Exception e) {
            logger.error("Unable to send mail notification", e);
        }

        model.addAttribute("order", order);
        model.addAttribute("status", "confirmed");

        Mappings.includeMappings(model);
        return Templates.CHECKOUT_END;
    }

    @RequestMapping(value = Mappings.CHECKOUT_FAILED, method = RequestMethod.GET)
    public String checkoutFailed(HttpSession session, Model model) {

        CommercialOrder order = (CommercialOrder) session.getAttribute(Basket.ORDER_SATTR);

        // check if basket is not empty
        if (order == null) {
            return "redirect:" + Mappings.BASKET;
        }

        Basket basket = Basket.getBasketOrCreate(session);

        // compute total
        List<Expense> expenses = expenseRepository.findAll();
        List<Product> products = productRepository.findAll(false);

        double total = basket.computeTotalWithExpenses(products, expenses);
        model.addAttribute("total", total);

        // reset basket
        basket.resetBasket(session);

        // send notification to client
        try {
            mailer.sendClientNotification(OrderNotificationType.PAYMENT_FAILED, order);
        } catch (Exception e) {
            logger.error("Unable to send mail notification", e);
        }

        model.addAttribute("order", order);
        model.addAttribute("status", "failed");

        Mappings.includeMappings(model);
        return Templates.CHECKOUT_END;

    }

}
