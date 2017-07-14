package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.CheckoutForm;
import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.PaymentType;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

    @RequestMapping(Mappings.ORDER)
    public String showOrder(Model model) {

        List<Product> products = productRepository.findAll();
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
        List<Product> products = productRepository.findAll();
        HashMap<Product, Integer> productsWithQuantities = new HashMap<>();
        Iterator<Long> keys = basket.keySet().iterator();
        while (keys.hasNext()) {
            Long pId = keys.next();
            Product p = products.stream()
                    .filter(pf -> pId.equals(pf.getId()))
                    .findAny().orElse(null);

            productsWithQuantities.put(p, basket.get(pId));
        }

        model.addAttribute("basket", productsWithQuantities);

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

        Double total = computeTotal(basket);
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
                checkoutForm.getComment());

        orderRepository.save(order);

        // reset basket
        resetBasket(session);

        //
        Double total = computeTotal(basket);
        model.addAttribute("total", total);
        model.addAttribute("paymentType", paymentType.toString());

        Mappings.includeMappings(model);
        return Templates.CHECKOUT_RESULT;
    }

    private void resetBasket(HttpSession session) {
        HashMap<Long, Integer> basket = new HashMap<>();
        session.setAttribute("basket", basket);
    }

    private Double computeTotal(HashMap<Long, Integer> basket) {

        Double total = 0d;
        Iterator<Long> keys = basket.keySet().iterator();
        List<Product> products = productRepository.findAll();
        while (keys.hasNext()) {
            Long pId = keys.next();
            Product p = products.stream()
                    .filter(pf -> pId.equals(pf.getId()))
                    .findAny().orElse(null);

            total += p.getPrice() * basket.get(pId);
        }

        return total;
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
