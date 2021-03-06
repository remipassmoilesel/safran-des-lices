package org.remipassmoilesel.safranlices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by remipassmoilesel on 12/06/17.
 */
public class Mappings {

    private static final Logger logger = LoggerFactory.getLogger(Mappings.class);
    public static final String MODEL_ARGUMENT_NAME = "mappings";

    public static final String ROOT = "/";

    public static final String OUR_SAFRAN = ROOT + "le-safran";
    public static final String OUR_EXPLOITATION = ROOT + "notre-safraniere";
    public static final String ORDER = ROOT + "commander";
    public static final String BASKET = ROOT + "mon-panier";
    public static final String BILLING_FORM = ROOT + "facturation";
    public static final String CHECKOUT_END = ROOT + "fin-de-commande";
    public static final String SHOW_BILL = ROOT + "facture";
    public static final String QUALITY_ANALYSIS = "analyse-qualite";

    public static final String LEGAL_MENTIONS = ROOT + "mentions-legales";
    public static final String TERMS_OF_SALES = ROOT + "conditions-generales-de-vente";

    public static final String ADMIN_ROOT = ROOT + "admin/";
    public static final String ADMIN_LOGIN = ADMIN_ROOT + "login";
    public static final String ADMIN_LOGOUT = ADMIN_ROOT + "logout";
    public static final String ADMIN_CONFIGURE_SALES = ADMIN_ROOT + "configurer-les-ventes";
    public static final String ADMIN_SHOW_ORDER = ADMIN_ROOT + "afficher-la-commande";
    public static final String ADMIN_SHOW_BILL = ADMIN_ROOT + "facture";
    public static final String ADMIN_SHOW_ALL_BILLS = ADMIN_ROOT + "afficher-toutes-les-factures";
    public static final String ADMIN_DOWNLOAD_PRODUCTS = ADMIN_ROOT + "telecharger-les-produits";
    public static final String ADMIN_UPLOAD_PRODUCTS = ADMIN_ROOT + "enregistrer-les-produits";
    public static final String ADMIN_DOWNLOAD_SHIPPING_COSTS = ADMIN_ROOT + "telecharger-les-frais";
    public static final String ADMIN_UPLOAD_SHIPPING_COSTS = ADMIN_ROOT + "enregistrer-les-frais";
    public static final String ADMIN_ACTIONS = ADMIN_ROOT + "actions";

    public static final String TEMPLATE = ROOT + "template";
    public static final String ERROR = ROOT + "error";

    public static MappingMap getMap() {

        MappingMap result = new MappingMap();

        for (Field f : Mappings.class.getDeclaredFields()) {
            try {
                Object val = f.get(null);
                if (val instanceof String) {
                    result.put(f.getName(), (String) val);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to access field: " + f);
            }
        }

        return result;
    }

    /**
     * Special class used to show errors
     */
    public static class MappingMap extends HashMap<String, String> {
        @Override
        public String get(Object key) {
            String res = super.get(key);
            if (res == null) {
                logger.error("Key do not exist: " + key, new Exception("Key do not exist: " + key));
            }
            return res;
        }
    }

    public static void includeMappings(Model model) {
        model.addAttribute(Mappings.MODEL_ARGUMENT_NAME, Mappings.getMap());
    }


}
