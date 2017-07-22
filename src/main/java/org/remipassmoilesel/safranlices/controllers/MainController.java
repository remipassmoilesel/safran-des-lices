package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.CheckoutForm;
import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.entities.*;
import org.remipassmoilesel.safranlices.repositories.ExpenseRepository;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.utils.DevDataFactory;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class MainController {

    public static final String BASKET_SATTR = "basket";
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Value("${app.mail.to}")
    private String mailTo;

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
            Model model) {

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
            return Templates.CHECKOUT_RESULT;
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

        // reset basket
        resetBasket(session);

        Double total = Utils.computeTotalForBasket(productRepository.findAll(false), basket);
        model.addAttribute("total", total);
        model.addAttribute("paymentType", paymentType.toString());

        // send notification
        try {
            sendAdminNotification(order);
        } catch (Exception e) {
            logger.error("Unable to send mail notification", e);
        }

        Mappings.includeMappings(model);
        return Templates.CHECKOUT_RESULT;
    }

    @RequestMapping("/send-admin-mail-example")
    @ResponseBody
    public void sendAdminMail() throws MessagingException {
        List<Product> products = productRepository.findAll(false);
        sendAdminNotification(DevDataFactory.createOrder(null, products, null, null, null, null, null, null, null, null));
    }

    @RequestMapping("/show-admin-mail-example")
    public String show(Model model) {

        List<Product> products = productRepository.findAll(false);

        CommercialOrder order = DevDataFactory.createOrder(null, products, null, null, null, null, null, null, null, null);
        model.addAttribute("order", order);

        HashMap<Product, Integer> productsWithQuantities = Utils.mapProductWithQuantities(products, order);
        model.addAttribute("productsWithQuantities", productsWithQuantities);

        return "mail/admin";
    }

    public String getTemplatedMailAsString(String templateName, HashMap<String, Object> vars) {
        final Context ctx = new Context();

        Iterator<String> it = vars.keySet().iterator();
        while (it.hasNext()) {
            String k = it.next();
            Object v = vars.get(k);
            ctx.setVariable(k, v);
        }

        return this.templateEngine.process(templateName, ctx);
    }

    public void sendAdminNotification(CommercialOrder order) throws MessagingException {

        // get admin receivers
        List<String> adminDests = Arrays.asList(mailTo.split(","));

        // create message
        MimeMessage adminMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(adminMessage, false, "utf-8");

        // set from, to, subject
        adminMessage.setFrom(mailFrom);
        helper.setTo(adminDests.toArray(new String[adminDests.size()]));
        adminMessage.setSubject("Safran des Lices - Nouvelle commande");

        // set body
        List<Product> products = productRepository.findAll(false);
        HashMap<Product, Integer> productsWithQuantities = Utils.mapProductWithQuantities(products, order);
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("order", order);
        vars.put("productsWithQuantities", productsWithQuantities);
        adminMessage.setContent(getTemplatedMailAsString("mail/admin", vars), "text/html");

        // send message
        javaMailSender.send(adminMessage);

    }

    public void sendClientNotification(OrderNotificationType step, CommercialOrder order) throws MessagingException {

        // create message
        MimeMessage clientMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(clientMessage, false, "utf-8");

        // set from, to, subject
        clientMessage.setFrom(mailFrom);
        helper.setTo(order.getEmail());
        clientMessage.setSubject("Safran des Lices - " + step.getMailSubject());

        // set body
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("order", order);
        clientMessage.setContent(getTemplatedMailAsString(step.getMailTemplate(), vars), "text/html");

        // send message
        javaMailSender.send(clientMessage);

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
