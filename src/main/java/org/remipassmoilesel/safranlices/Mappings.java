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

    public static final String OUR_SAFRAN = ROOT + "notre-safran";
    public static final String OUR_EXPLOITATION = ROOT + "notre-safraniere";
    public static final String ORDER = ROOT + "commander";
    public static final String BASKET = ROOT + "mon-panier";
    public static final String CHECKOUT = ROOT + "paiement-securise";
    public static final String TEMPLATE = ROOT + "template";

    public static final String SEND_MAIL = ROOT + "send-mail";

    public static final String ADMIN_PAGE = ROOT + "admin";
    public static final String ADMIN_LOGIN = ADMIN_PAGE + "/login";
    public static final String ADMIN_LOGOUT = ADMIN_PAGE + "/logout";
    public static final String ADMIN_MODIFICATION = ADMIN_PAGE + "/modification";



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
