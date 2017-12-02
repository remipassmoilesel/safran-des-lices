package org.remipassmoilesel.safranlices.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.remipassmoilesel.safranlices.SafranLicesApplication;
import org.remipassmoilesel.safranlices.entities.Basket;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by remipassmoilesel on 12/06/17.
 */
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    private static Random rand = new Random();

    /**
     * Return a random int
     *
     * @param min
     * @param max
     * @return
     */
    public static int randInt(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static String nodeToHtmlString(Node doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "html");
        transformer.transform(domSource, result);
        return writer.toString();
    }

    private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
            + "Donec a diam lectus. Sed sit amet ipsum mauris. Maecenas congue ligula ac "
            + "quam viverra nec consectetur ante hendrerit. Donec et mollis dolor. "
            + "Praesent et diam eget libero egestas mattis sit amet vitae augue. Nam "
            + "tincidunt congue enim, ut porta lorem lacinia consectetur. Donec ut libero "
            + "sed arcu vehicula ultricies a non tortor. Lorem ipsum dolor sit amet, "
            + "consectetur adipiscing elit. Aenean ut gravida lorem. Ut turpis felis, "
            + "pulvinar a semper sed, adipiscing id dolor. Pellentesque auctor nisi id "
            + "magna consequat sagittis. Curabitur dapibus enim sit amet elit pharetra "
            + "tincidunt feugiat nisl imperdiet. Ut convallis libero in urna ultrices "
            + "accumsan. Donec sed odio eros. Donec viverra mi quis quam pulvinar at "
            + "malesuada arcu rhoncus. Cum sociis natoque penatibus et magnis dis parturient "
            + "montes, nascetur ridiculus mus. In rutrum accumsan ultricies. Mauris "
            + "vitae nisi at sem facilisis semper ac in est.";

    public static String generateLoremIpsum(int length) {

        String rslt = LOREM_IPSUM;

        while (rslt.length() < length) {
            rslt += LOREM_IPSUM;
        }

        return rslt.substring(0, length);
    }

    public static String anonymizeIpAdress(String stringIp) {
        return stringIp.replaceFirst("[0-9]{1,3}\\.[0-9]{1,3}$", "X.X");
    }

    public void anonymizeRawString(String raw, Path destinationPath) throws IOException {

        Files.createFile(destinationPath);

        BufferedWriter writer = Files.newBufferedWriter(destinationPath);

        for (String ip : raw.split("\n")) {
            writer.write(Utils.anonymizeIpAdress(ip));
            writer.newLine();
        }

        writer.flush();
        writer.close();
    }

    public static HashMap<Product, Integer> mapProductWithQuantities(List<Product> allProducts, CommercialOrder order) {
        HashMap<Long, Integer> productsMap = order.getQuantities();
        return mapProductWithQuantities(allProducts, new Basket(productsMap));
    }

    public static HashMap<Product, Integer> mapProductWithQuantities(List<Product> allProducts, Basket basket) {

        HashMap<Product, Integer> productsWithQuantities = new HashMap<>();

        for (Long productId : basket.getProductIds()) {

            Product p = allProducts.stream()
                    .filter(pf -> productId.equals(pf.getId()))
                    .findAny().orElse(null);

            productsWithQuantities.put(p, basket.getQuantityFor(productId));
        }

        return productsWithQuantities;
    }

    public static boolean isDevProfileEnabled(Environment env) {
        return Arrays.asList(env.getActiveProfiles()).contains(SafranLicesApplication.DEV_PROFILE);
    }


    public static byte[] readRaw(Path pdfPath) throws IOException {

        // display it
        try (InputStream pdfInputStream = Files.newInputStream(pdfPath)) {
            return readRaw(pdfInputStream);
        }

    }

    public static byte[] readRaw(InputStream pdfStream) throws IOException {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = pdfStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    public static void pdfResponse(HttpServletResponse response, byte[] content) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "inline; filename='output.pdf'");
        response.setContentLength(content.length);

        response.getOutputStream().write(content);
        response.getOutputStream().flush();
    }

    public static void pdfResponse(HttpServletResponse response, Path pdfPath) throws IOException {
        byte[] content = readRaw(pdfPath);
        pdfResponse(response, content);
    }

    public static void pdfResponse(HttpServletResponse response, InputStream pdf) throws IOException {
        byte[] content = readRaw(pdf);
        pdfResponse(response, content);
    }

    public static String getFormattedDate(Date date, String pattern) {
        return DateTimeFormat.forPattern(pattern).print(new DateTime(date));
    }
}
