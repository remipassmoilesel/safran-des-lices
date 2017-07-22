package org.remipassmoilesel.safranlices.utils;

import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.OrderNotificationType;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

        // get admin receivers
        List<String> adminDests = Arrays.asList(adminAdresses.split(","));

        // create message
        MimeMessage adminMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(adminMessage, false, "utf-8");

        // set from, to, subject
        adminMessage.setFrom(mailFrom);
        helper.setTo(adminDests.toArray(new String[adminDests.size()]));
        adminMessage.setSubject("Safran des Lices - Nouvelle commande");

        // set body
        List<Product> products = productRepository.findAll(false);
        HashMap<Product, Integer> productsWithQuantities = Utils.mapProductWithQuantities(products, order);
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("order", order);
        vars.put("productsWithQuantities", productsWithQuantities);
        adminMessage.setContent(getTemplatedMailAsString("mail/admin", vars), "text/html");

        // send message
        javaMailSender.send(adminMessage);

    }

    public void sendClientNotification(OrderNotificationType step, CommercialOrder order) throws MessagingException {

        // create message
        MimeMessage clientMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(clientMessage, false, "utf-8");

        // set from, to, subject
        clientMessage.setFrom(mailFrom);
        helper.setTo(order.getEmail());
        clientMessage.setSubject("Safran des Lices - " + step.getMailSubject());

        // set body
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("order", order);
        clientMessage.setContent(getTemplatedMailAsString(step.getMailTemplate(), vars), "text/html");

        // send message
        javaMailSender.send(clientMessage);

    }

}
