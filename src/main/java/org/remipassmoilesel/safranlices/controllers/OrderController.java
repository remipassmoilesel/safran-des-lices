package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.entities.Basket;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.entities.ShippingCost;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.repositories.ShippingCostRepository;
import org.remipassmoilesel.safranlices.utils.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by remipassmoilesel on 13/06/17.
 */
@Controller
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ShippingCostRepository shippingCostRepository;

    @Autowired
    private Mailer mailer;

    @Autowired
    private Environment env;

    @Value("${app.mail.mainAdress}")
    private String mainMailAdress;

    /**
     * Show products on page, where customer can choose them
     *
     * @param model
     * @return
     */
    @RequestMapping(Mappings.ORDER)
    public String showOrder(Model model) {

        // create a list of products dedicated for display,
        // with each list item as a row, composed of three articles
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

    /**
     * Show current basket, where customer see article he choosed
     *
     * @param productId
     * @param addToCart
     * @param productQuantity
     * @param reset
     * @param delete
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = Mappings.BASKET)
    public String showBasket(
            @RequestParam(required = false, name = "id") Long productId,
            @RequestParam(required = false, name = "addToCart") Boolean addToCart,
            @RequestParam(required = false, name = "changeQtty") Boolean changeQtty,
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
            Product product = productRepository.getOne(productId);
            basket.remove(product);
            return "redirect:" + Mappings.BASKET;
        }

        // add something to current basket
        if (addToCart != null && addToCart == true && productId != null && productQuantity != null) {
            Product product = productRepository.getOne(productId);
            basket.addProduct(product, productQuantity);
            return "redirect:" + Mappings.BASKET;
        }

        // change article quantity in basket
        if (changeQtty != null && changeQtty == true && productId != null && productQuantity != null) {
            Product product = productRepository.getOne(productId);
            basket.remove(product);
            basket.addProduct(product, productQuantity);
            return "redirect:" + Mappings.BASKET;
        }

        // map quantities and products
        List<Product> allProducts = productRepository.findAll(false);
        List<ShippingCost> allShippingCosts = shippingCostRepository.findAll(false);

        HashMap<Product, Integer> productsWithQuantities = basket.mapProductWithQuantities(allProducts);
        model.addAttribute("basket", productsWithQuantities);

        // total
        double total = basket.computeTotal(allProducts);
        model.addAttribute("total", total);

        // shipping informations
        double shippingCosts = basket.computeShippingCosts(allProducts, allShippingCosts);
        double totalWeight = basket.computeTotalWeight(allProducts);
        model.addAttribute("shippingCosts", shippingCosts);
        model.addAttribute("totalWeight", totalWeight);

        Mappings.includeMappings(model);
        return Templates.BASKET;
    }

}
