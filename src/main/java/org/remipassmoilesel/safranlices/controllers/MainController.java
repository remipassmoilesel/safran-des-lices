package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.SafranLicesApplication;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.entities.*;
import org.remipassmoilesel.safranlices.forms.CheckoutForm;
import org.remipassmoilesel.safranlices.repositories.ExpenseRepository;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.utils.DevDataFactory;
import org.remipassmoilesel.safranlices.utils.Mailer;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class MainController {

    public static final String PAYMENT_TOKEN_SATTR = "paymentToken";
    public static final String BASKET_SATTR = "basket";
    public static final String ORDER_SATTR = "order";

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
            @RequestParam(required = false, name = "id") Long id,
            @RequestParam(required = false, name = "addToCart") Boolean addToCart,
            @RequestParam(required = false, name = "qtty") Integer qtty,
            @RequestParam(required = false, name = "reset") Boolean reset,
            @RequestParam(required = false, name = "delete") Boolean delete,
            HttpSession session,
            Model model) {

        // current user basket: Article ID / Quantity
        HashMap<Long, Integer> basket = checkOrCreateBasket(session);

        // empty basket
        if (reset != null && reset == true) {
            resetBasket(session);
            return "redirect:" + Mappings.BASKET;
        }

        // delete article
        if (delete != null && delete == true && id != null) {
            basket.remove(id);
            session.setAttribute("basket", basket);

            return "redirect:" + Mappings.BASKET;
        }

        // add something to cart
        if (addToCart != null && addToCart == true && id != null && qtty != null) {

            if (basket.get(id) != null) {
                qtty += basket.get(id);
            }

            basket.put(id, qtty);
            session.setAttribute(BASKET_SATTR, basket);

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
        double total = Utils.computeTotalForBasket(products, basket);
        for (Expense ex : expenses) {
            total += ex.getValue();
        }
        model.addAttribute("total", total);

        Mappings.includeMappings(model);
        return Templates.BASKET;
    }


    @RequestMapping(value = Mappings.CHECKOUT, method = RequestMethod.GET)
    public String showCheckout(
            HttpSession session,
            Model model) {

        HashMap<Long, Integer> basket = checkOrCreateBasket(session);

        // check if basket is not empty
        if (basket.size() < 1) {
            return "redirect:" + Mappings.BASKET;
        }

        List<Product> products = productRepository.findAll(false);
        Double total = Utils.computeTotalForBasket(products, basket);

        List<Expense> expenses = expenseRepository.findAll();
        for (Expense ex : expenses) {
            total += ex.getValue();
        }

        model.addAttribute("total", total);

        Mappings.includeMappings(model);
        return Templates.CHECKOUT_FORM;
    }

    @RequestMapping(value = Mappings.CHECKOUT, method = RequestMethod.POST)
    public String processCheckout(
            @Valid CheckoutForm checkoutForm,
            BindingResult results,
            HttpSession session,
            Model model) throws NoSuchAlgorithmException {

        HashMap<Long, Integer> basket = checkOrCreateBasket(session);

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
            return Templates.CHECKOUT_REDIRECT;
        }

        // save order
        ArrayList<Product> products = new ArrayList<>();
        Iterator<Long> it = basket.keySet().iterator();
        while (it.hasNext()) {
            Long pid = it.next();
            products.add(productRepository.findOne(pid));
        }

        PaymentType paymentType = PaymentType.valueOf(checkoutForm.getPaymentType());

        CommercialOrder order = new CommercialOrder(
                new Date(),
                products,
                basket,
                checkoutForm.getAddress(),
                checkoutForm.getPhonenumber(),
                checkoutForm.getFirstname(),
                checkoutForm.getLastname(),
                paymentType,
                checkoutForm.getComment(),
                checkoutForm.getEmail());

        order.setTotal(Utils.computeTotalForBasket(products, basket));
        orderRepository.save(order);

        // include order in session
        session.setAttribute("order", order);

        // send notification
        try {
            mailer.sendAdminNotification(order);
        } catch (Exception e) {
            logger.error("Unable to send mail notification", e);
        }

        // payment is differed
        if (PaymentType.BANK_CHECK == order.getPaymentType()) {
            model.addAttribute("order", order);

            // send notification to client
            try {
                mailer.sendClientNotification(OrderNotificationType.PAYMENT_CONFIRMED, order);
            } catch (Exception e) {
                logger.error("Unable to send mail notification", e);
            }

            Mappings.includeMappings(model);
            return Templates.CHECKOUT_END;
        }

        // payment must be confirmed
        else {

            // create a token
            String token = String.valueOf(MessageDigest.getInstance("md5").digest(String.valueOf(System.currentTimeMillis()).getBytes()));
            session.setAttribute("paymentToken", token);

            // dev vars
            model.addAttribute("devmode", Arrays.asList(env.getActiveProfiles()).contains(SafranLicesApplication.DEV_PROFILE));
            model.addAttribute("checkoutConfirmedLink", Mappings.CHECKOUT_CONFIRMED + "?token=" + token);
            model.addAttribute("checkoutFailedLink", Mappings.CHECKOUT_FAILED);

            Mappings.includeMappings(model);
            return Templates.CHECKOUT_REDIRECT;
        }

    }

    @RequestMapping(value = Mappings.CHECKOUT_CONFIRMED, method = RequestMethod.GET)
    public String checkoutConfirmed(Model model, HttpSession session,
                                    @RequestParam(name = "token", required = true) String token) {

        CommercialOrder order = (CommercialOrder) session.getAttribute(ORDER_SATTR);

        // check if basket is not empty
        if (order == null) {
            return "redirect:" + Mappings.BASKET;
        }

        // reset basket
        resetBasket(session);

        // test session token
        String stoken = (String) session.getAttribute(PAYMENT_TOKEN_SATTR);
        if (token == null || stoken == null || token.equals(stoken) == false) {
            Error err = new Error("Invalid token: " + token + " / stoken: " + stoken);
            logger.error("Invalid token: " + token + " / stoken: " + stoken, err);
            throw err;
        }

        // send notification to client
        try {
            mailer.sendClientNotification(OrderNotificationType.PAYMENT_CONFIRMED, order);
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

        CommercialOrder order = (CommercialOrder) session.getAttribute(ORDER_SATTR);

        // check if basket is not empty
        if (order == null) {
            return "redirect:" + Mappings.BASKET;
        }

        // reset basket
        resetBasket(session);

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

    @RequestMapping("/send-admin-mail-example")
    @ResponseBody
    public void sendAdminMailExample() throws MessagingException {
        List<Product> products = productRepository.findAll(false);
        mailer.sendAdminNotification(DevDataFactory.createOrder(null, products, null, null, null, null, null, null, null, null));
    }

    @RequestMapping("/send-client-order-confirmed-example")
    @ResponseBody
    public void sendClientOrderConfirmedMailExample() throws MessagingException {
        List<Product> products = productRepository.findAll(false);
        mailer.sendClientNotification(OrderNotificationType.PAYMENT_CONFIRMED, DevDataFactory.createOrder(null, products, null, null, null, null, null, null, null, null));
    }

    @RequestMapping("/send-client-order-failed-example")
    @ResponseBody
    public void sendClientOrderFailedMailExample() throws MessagingException {
        List<Product> products = productRepository.findAll(false);
        mailer.sendClientNotification(OrderNotificationType.PAYMENT_FAILED, DevDataFactory.createOrder(null, products, null, null, null, null, null, null, null, null));
    }

    @RequestMapping("/show-admin-mail-example")
    public String showAdminMailExample(Model model) {

        List<Product> products = productRepository.findAll(false);

        CommercialOrder order = DevDataFactory.createOrder(null, products, null, null, null, null, null, null, null, null);
        model.addAttribute("order", order);

        HashMap<Product, Integer> productsWithQuantities = Utils.mapProductWithQuantities(products, order);
        model.addAttribute("productsWithQuantities", productsWithQuantities);

        return "mail/admin";
    }

    @RequestMapping("/show-client-order-confirmed-mail-example")
    public String showClientOrderConfirmedMailExample(Model model) {

        List<Product> products = productRepository.findAll(false);

        CommercialOrder order = DevDataFactory.createOrder(null, products, null, null, null, null, null, null, null, null);
        model.addAttribute("order", order);

        HashMap<Product, Integer> productsWithQuantities = Utils.mapProductWithQuantities(products, order);
        model.addAttribute("productsWithQuantities", productsWithQuantities);

        return "mail/orderConfirmed";
    }

    @RequestMapping("/show-client-order-failed-mail-example")
    public String showClientOrderFailedMailExample(Model model) {

        List<Product> products = productRepository.findAll(false);

        CommercialOrder order = DevDataFactory.createOrder(null, products, null, null, null, null, null, null, null, null);
        model.addAttribute("order", order);

        HashMap<Product, Integer> productsWithQuantities = Utils.mapProductWithQuantities(products, order);
        model.addAttribute("productsWithQuantities", productsWithQuantities);

        return "mail/orderFailed";
    }

    private void resetBasket(HttpSession session) {
        HashMap<Long, Integer> basket = new HashMap<>();
        session.setAttribute("basket", basket);
    }

    private HashMap<Long, Integer> checkOrCreateBasket(HttpSession session) {
        HashMap<Long, Integer> basket = (HashMap<Long, Integer>) session.getAttribute(BASKET_SATTR);
        if (basket == null) {
            basket = new HashMap<>();
            session.setAttribute("basket", basket);
        }

        return basket;
    }

}
