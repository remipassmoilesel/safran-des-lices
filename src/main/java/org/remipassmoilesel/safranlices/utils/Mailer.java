package org.remipassmoilesel.safranlices.utils;

import org.remipassmoilesel.safranlices.entities.Basket;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.OrderNotificationType;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by remipassmoilesel on 22/07/17.
 */
@Component
public class Mailer {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PdfBillGenerator billGenerator;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Value("${app.mail.adminAdresses}")
    private String adminAdresses;

    @Autowired
    private ProductRepository productRepository;

    public String getTemplatedMailAsString(String templateName, HashMap<String, Object> vars) {
        final Context ctx = new Context();

        Iterator<String> it = vars.keySet().iterator();
        while (it.hasNext()) {
            String k = it.next();
            Object v = vars.get(k);
            ctx.setVariable(k, v);
        }

        return this.templateEngine.process(templateName, ctx);
    }

    public void sendAdminNotification(CommercialOrder order) throws MessagingException {
        sendAdminNotification(order, null);
    }

    public void sendAdminNotification(CommercialOrder order, String billId) throws MessagingException {

        // get admin receivers
        List<String> adminDests = Arrays.asList(adminAdresses.split(","));

        // create message
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // set from, to, subject
        helper.setFrom(mailFrom);
        helper.setTo(adminDests.toArray(new String[adminDests.size()]));
        helper.setSubject("Safran des Lices - Nouvelle commande");

        // set body
        List<Product> products = productRepository.findAll(false);
        HashMap<Product, Integer> productsWithQuantities = Basket.fromOrder(order).mapProductWithQuantities(products);
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("order", order);
        vars.put("productsWithQuantities", productsWithQuantities);
        helper.setText(getTemplatedMailAsString("mail/admin", vars), true);

        // set attachment
        if (billId != null) {
            FileSystemResource billResource = new FileSystemResource(
                    billGenerator.getPdfAbsolutePath(billId).toString());
            helper.addAttachment(billId, billResource);
        }

        // send message
        javaMailSender.send(message);

    }

    public void sendClientNotification(OrderNotificationType step, CommercialOrder order) throws MessagingException {
        sendClientNotification(step, order, null);
    }

    public void sendClientNotification(OrderNotificationType step, CommercialOrder order, String billId) throws MessagingException {

        // create message
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // set from, to, subject
        helper.setFrom(mailFrom);
        helper.setTo(order.getEmail());
        helper.setSubject("Safran des Lices - " + step.getMailSubject());

        // set body
        List<Product> products = productRepository.findAll(false);
        HashMap<Product, Integer> productsWithQuantities = Basket.fromOrder(order).mapProductWithQuantities(products);
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("order", order);
        vars.put("productsWithQuantities", productsWithQuantities);
        String template = getTemplatedMailAsString(step.getMailTemplate(), vars);
        helper.setText(template, true);

        if (step.equals(OrderNotificationType.ORDER_CONFIRMED) && billId != null) {
            helper.addAttachment("facture.pdf",
                    new FileSystemResource(billGenerator.getPdfAbsolutePath(billId).toString()));
        }

        // send message
        javaMailSender.send(message);

    }

}
