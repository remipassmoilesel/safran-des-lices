package org.remipassmoilesel.safranlices.controllers;

import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

    @RequestMapping(Mappings.ADMIN_PAGE)
    public String showTemplate(Model model) {

        List<Product> products = productRepository.findAll();

        // orders non processed
        List<CommercialOrder> lastOrdersToProcess = orderRepository.findLasts(
                new PageRequest(1, 20), false).getContent();

        model.addAttribute("ordersToProcess", lastOrdersToProcess);
        model.addAttribute("basketsToProcess", getBasketsFromOrders(lastOrdersToProcess, products));

        // orders processed
        List<CommercialOrder> lastOrdersProcessed = orderRepository.findLasts(
                new PageRequest(1, 20), true).getContent();

        model.addAttribute("ordersProcessed", lastOrdersProcessed);
        model.addAttribute("basketsProcessed", getBasketsFromOrders(lastOrdersProcessed, products));

        Mappings.includeMappings(model);
        return Templates.ADMIN_TEMPLATE;
    }

    private List<List<Object[]>> getBasketsFromOrders(List<CommercialOrder> orders, List<Product> products){

        List<List<Object[]>> baskets = new ArrayList<>();

        for(CommercialOrder order : orders){

            HashMap<Long, Integer> qtties = order.getQuantities();
            Iterator<Long> it = qtties.keySet().iterator();

            ArrayList<Object[]> bskt = new ArrayList<>();

            while(it.hasNext()){
                Long pId = it.next();
                Product p = products.stream()
                        .filter(pf -> pId.equals(pf.getId()))
                        .findAny().orElse(null);
                Integer qt = qtties.get(pId);
                bskt.add(new Object[]{p, qt});
            }

            baskets.add(bskt);
        }

        return baskets;
    }

    @RequestMapping(value = Mappings.ADMIN_LOGIN, method = RequestMethod.GET)
    public String showLoginPage(Model model) throws IOException {

        Mappings.includeMappings(model);
        return Templates.ADMIN_LOGIN;
    }

}
