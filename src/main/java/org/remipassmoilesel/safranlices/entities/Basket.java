package org.remipassmoilesel.safranlices.entities;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Basket {

    public static Basket getBasketOrCreate(HttpSession session) {

        Basket basket;
        HashMap<Long, Integer> productMap = (HashMap<Long, Integer>) session.getAttribute(BASKET_SATTR);

        if (productMap == null) {
            basket = new Basket();
            session.setAttribute(BASKET_SATTR, basket.getProductMap());
        } else {
            basket = new Basket(productMap);
        }

        return basket;
    }

    public static final String BASKET_SATTR = "basket";
    public static final String ORDER_SATTR = "order";

    /**
     * Raw basket, with
     * long=product id,
     * integer=quantity ordered
     */
    private HashMap<Long, Integer> productMap;

    public Basket() {
        this(new HashMap<>());
    }

    public Basket(HashMap<Long, Integer> productsMap) {

        if (productsMap == null) {
            throw new NullPointerException("Product map must not be null");
        }

        this.productMap = productsMap;
    }

    public Double computeTotalForBasket(List<Product> products) {

        Double total = 0d;
        Iterator<Long> keys = productMap.keySet().iterator();
        while (keys.hasNext()) {
            Long pId = keys.next();
            Product p = products.stream()
                    .filter(pf -> pId.equals(pf.getId()))
                    .findAny().orElse(null);

            total += p.getPrice() * productMap.get(pId);
        }

        return total;
    }

    public Double computeTotalWithExpenses(List<Product> products, List<Expense> expenses) {

        Double total = computeTotalForBasket(products);

        for (Expense ex : expenses) {
            total += ex.getValue();
        }

        return total;
    }

    public void resetBasket(HttpSession session) {
        HashMap<Long, Integer> rawBasket = new HashMap<>();
        session.setAttribute(BASKET_SATTR, rawBasket);
    }

    public Integer size() {
        return productMap.size();
    }

    public List<Long> getProductIds() {
        return Arrays.asList(productMap.keySet().toArray(new Long[productMap.size()]));
    }

    public HashMap<Long, Integer> getProductMap() {
        return productMap;
    }

    public Integer remove(Long productId) {
        return productMap.remove(productId);
    }

    public void addProduct(Long id, Integer qtty) {
        if (productMap.get(id) != null) {
            qtty += productMap.get(id);
        }

        productMap.put(id, qtty);
    }

    public Integer getQuantityFor(Long productId) {
        return productMap.get(productId);
    }
}
