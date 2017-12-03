package org.remipassmoilesel.safranlices.entities;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Basket {

    public static final String BASKET_SATTR = "basket";
    public static final String ORDER_SATTR = "order";

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


    public static Basket fromOrder(CommercialOrder order) {
        return new Basket(order.getQuantities());
    }

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
    public Double computeTotalWithoutShippingCosts(List<Product> products) {

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
     * @param product
     * @return
     */
    public Integer remove(Product product) {
        return productMap.remove(product.getId());
    }

    /**
     * Add a product in basket, and specified quantity
     *
     * @param product
     * @param quantity
     */
    public void addProduct(Product product, Integer quantity) {

        Long id = product.getId();

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

    public HashMap<Product, Integer> mapProductWithQuantities(List<Product> allProducts) {

        HashMap<Product, Integer> productsWithQuantities = new HashMap<>();

        for (Long productId : this.getProductIds()) {

            Product p = allProducts.stream()
                    .filter(pf -> productId.equals(pf.getId()))
                    .findAny().orElse(null);

            productsWithQuantities.put(p, this.getQuantityFor(productId));
        }

        return productsWithQuantities;
    }


    public Double computeTotalWeight(List<Product> allProducts) {

        Double result = 0d;
        HashMap<Product, Integer> map = this.mapProductWithQuantities(allProducts);
        Iterator<Product> iter = map.keySet().iterator();

        while (iter.hasNext()) {
            Product product = iter.next();
            Integer quantity = map.get(product);
            result += product.getGrossWeight() * quantity;
        }

        return result;
    }

    public Double computeShippingCosts(List<Product> allProducts,
                                       List<ShippingCost> allShippingCosts)
            throws IllegalStateException {

        Double totalWeight = computeTotalWeight(allProducts);

        for (ShippingCost sc : allShippingCosts) {
            if (sc.getMinWeight() <= totalWeight && sc.getMaxWeight() > totalWeight) {
                return sc.getPrice();
            }
        }

        throw new IllegalStateException("No valid shipping cost found for weight: " + totalWeight);
    }

    public void clear() {
        this.productMap.clear();
    }
}
