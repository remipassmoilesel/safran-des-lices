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

    /**
     * Compute total in current currency of products in basket
     *
     * @param products
     * @return
     */
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

    /**
     * Compute total of current basket and add supplementary expenses
     *
     * @param products
     * @param expenses
     * @return
     */
    public Double computeTotalWithExpenses(List<Product> products, List<Expense> expenses) {

        Double total = computeTotalForBasket(products);

        for (Expense ex : expenses) {
            total += ex.getValue();
        }

        return total;
    }

    /**
     * Reset basket and save it into current session
     *
     * @param session
     */
    public void resetBasket(HttpSession session) {
        HashMap<Long, Integer> rawBasket = new HashMap<>();
        session.setAttribute(BASKET_SATTR, rawBasket);
    }

    /**
     * Return number of different products in basket
     *
     * @return
     */
    public Integer getNumberOfProducts() {
        return productMap.size();
    }

    /**
     * Return a list of products ids
     *
     * @return
     */
    public List<Long> getProductIds() {
        return Arrays.asList(productMap.keySet().toArray(new Long[productMap.size()]));
    }

    /**
     * Return a map with products ids and article numbers
     *
     * @return
     */
    public HashMap<Long, Integer> getProductMap() {
        return productMap;
    }

    /**
     * Remove a product from basket
     *
     * @param productId
     * @return
     */
    public Integer remove(Long productId) {
        return productMap.remove(productId);
    }

    /**
     * Add a product in basket, and specified quantity
     *
     * @param id
     * @param quantity
     */
    public void addProduct(Long id, Integer quantity) {
        if (productMap.get(id) != null) {
            quantity += productMap.get(id);
        }

        productMap.put(id, quantity);
    }

    /**
     * Return current quantity of specified product id
     *
     * @param productId
     * @return
     */
    public Integer getQuantityFor(Long productId) {
        return productMap.get(productId);
    }
}
