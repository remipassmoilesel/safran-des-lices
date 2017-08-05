package org.remipassmoilesel.safranlices.controllers;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.utils.DevDataFactory;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remipassmoilesel on 05/08/17.
 */
@Controller
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private String tokenUrl;
    private String paymentUrl;

    @Autowired
    private Environment env;

    @Value("${app.paypal.id}")
    private String paypalId;

    @Value("${app.paypal.pwd}")
    private String paypalPwd;

    public PaymentController() {

    }


    @RequestMapping("/payment")
    @ResponseBody
    public String testPayment() throws UnirestException {

        String out = "";

        CommercialOrder order = DevDataFactory.createOrder(null, null, null, null, null, null
                , null, null, null, null);
        String creditCardNumber = "4417119669820331";
        String creditCardType = "visa";
        Integer expireMonth = 11;
        Integer expireYear = 2022;
        String cvv2 = "874";

        // TODO: return always with two decimal digits
        order.setTotal(25.22);

        return out + pay(order, creditCardNumber, creditCardType, expireMonth, expireYear, cvv2);
    }

    public String pay(CommercialOrder order,
                      String creditCardNumber,
                      String creditCardType,
                      Integer creditCardExpireMonth,
                      Integer creditCardExpireYear,
                      String creditCardCvv2) throws UnirestException {

        Address billingAddress = new Address();
        billingAddress.setCity(order.getCity());
        billingAddress.setCountryCode("FR");
        billingAddress.setLine1(order.getAddress());
        billingAddress.setPostalCode(order.getPostalcode());
        billingAddress.setState("France");

        CreditCard card = new CreditCard()
                .setType(creditCardType)
                .setNumber(creditCardNumber)
                .setExpireMonth(creditCardExpireMonth)
                .setExpireYear(creditCardExpireYear)
                .setCvv2(creditCardCvv2)
                .setFirstName(order.getFirstName())
                .setLastName(order.getLastName());

        Details details = new Details();
        details.setShipping("0");
        details.setSubtotal(order.getRoundedTotalAsString());
        details.setTax("0");

        Amount amount = new Amount();
        amount.setCurrency("USD");
        amount.setTotal(String.valueOf(order.getRoundedTotalAsString()));
        amount.setDetails(details);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Paiement Safran des Lices");

        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        FundingInstrument fundingInstrument = new FundingInstrument();
        fundingInstrument.setCreditCard(card);

        List<FundingInstrument> fundingInstrumentList = new ArrayList();
        fundingInstrumentList.add(fundingInstrument);

        Payer payer = new Payer();
        payer.setFundingInstruments(fundingInstrumentList);
        payer.setPaymentMethod("credit_card");

        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        Payment createdPayment = null;
        try {
            APIContext apiContext = new APIContext(paypalId, paypalPwd, "sandbox");

            // posting
            createdPayment = payment.create(apiContext);

            System.out.println(createdPayment);
            System.out.println(createdPayment);
            System.out.println(createdPayment);
            System.out.println(createdPayment);

        } catch (PayPalRESTException e) {
            logger.error("Error while paying: ", e);
        }

        return createdPayment.toJSON();

    }


}
