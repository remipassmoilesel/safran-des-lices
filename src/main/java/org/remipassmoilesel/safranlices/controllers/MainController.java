package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private static final String CURRENT_BASKET = "basket";

    @Autowired
    private ProductRepository productRepository;

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

    @RequestMapping(Mappings.PRODUCTS)
    public String showProducts(Model model) {
        Mappings.includeMappings(model);
        return Templates.PRODUCTS;
    }

    @RequestMapping(Mappings.EXPLOITATION)
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

    @RequestMapping(Mappings.BASKET)
    public String showBasket(
            @RequestParam(required = false, name = "id") Long id,
            @RequestParam(required = false, name = "addToCart") Boolean addToCart,
            @RequestParam(required = false, name = "qtty") Integer qtty,
            @RequestParam(required = false, name = "reset") Boolean reset,
            @RequestParam(required = false, name = "delete") Boolean delete,
            HttpSession session,
            Model model) {

        // current user basket: Article ID / Quantity
        HashMap<Long, Integer> basket = (HashMap<Long, Integer>) session.getAttribute(CURRENT_BASKET);
        if (basket == null) {
            basket = new HashMap<>();
            session.setAttribute("basket", basket);
        }

        // empty basket
        if(reset != null && reset == true){
            basket = new HashMap<>();
            session.setAttribute("basket", basket);

            return "redirect:" + Mappings.BASKET;
        }

        // delete article
        if(delete != null && delete == true && id != null){
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
            session.setAttribute(CURRENT_BASKET, basket);
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

    @RequestMapping(Mappings.CHECKOUT)
    public String checkout(
            HttpSession session,
            Model model) {

        HashMap<Long, Integer> basket = (HashMap<Long, Integer>) session.getAttribute(CURRENT_BASKET);
        if (basket == null) {
            basket = new HashMap<>();
            session.setAttribute("basket", basket);
        }

        if(basket.size() < 1){
            return "redirect:" + Mappings.BASKET;
        }

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

        model.addAttribute("total", total);

        Mappings.includeMappings(model);
        return Templates.CHECKOUT;
    }

}
