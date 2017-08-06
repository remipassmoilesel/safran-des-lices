package org.remipassmoilesel.safranlices.controllers;

import com.mangopay.MangoPayApi;
import com.mangopay.core.Address;
import com.mangopay.core.Money;
import com.mangopay.core.enumerations.CardType;
import com.mangopay.core.enumerations.CountryIso;
import com.mangopay.core.enumerations.CurrencyIso;
import com.mangopay.entities.*;
import com.mangopay.entities.subentities.PayInExecutionDetailsDirect;
import com.mangopay.entities.subentities.PayInPaymentDetailsCard;
import org.remipassmoilesel.safranlices.Mappings;
import org.remipassmoilesel.safranlices.Templates;
import org.remipassmoilesel.safranlices.entities.CommercialOrder;
import org.remipassmoilesel.safranlices.entities.Expense;
import org.remipassmoilesel.safranlices.entities.Product;
import org.remipassmoilesel.safranlices.repositories.ExpenseRepository;
import org.remipassmoilesel.safranlices.repositories.OrderRepository;
import org.remipassmoilesel.safranlices.repositories.ProductRepository;
import org.remipassmoilesel.safranlices.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * Created by remipassmoilesel on 06/08/17.
 */
@Controller
public class PaymentController {

    private static final String CARD_REGISTRATION_ID_ATTR = "cardRegistrationId";

    private static final Logger logger = LoggerFactory.getLogger(DevController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Value("${app.mail.mainAdress}")
    private String mainMailAdress;

    @Autowired
    private Environment env;

    @Value("${app.bankapi.id}")
    private String bankapiId;

    @Value("${app.bankapi.pwd}")
    private String bankapiPwd;

    private MangoPayApi api = new MangoPayApi();

    public PaymentController() {

    }

    @RequestMapping(value = Mappings.CHECKOUT, method = RequestMethod.GET)
    public String showCheckoutForm(
            HttpSession session,
            Model model) throws Exception {

        configureBankApi();

        // get current order
        CommercialOrder order = (CommercialOrder) session.getAttribute(MainController.ORDER_SATTR);
        if (order == null) {
            return "redirect:" + Mappings.BASKET;
        }

        UserNatural user = createUser(order);

        logger.info("user.getId()");
        logger.info(user.getId());

        CardRegistration cardRegistration = createCardRegistration(user);

        // dev vars
        model.addAttribute("devmode", Utils.isDevProfileEnabled(env));
        model.addAttribute("mainMailAddress", mainMailAdress);
        model.addAttribute("order", order);
        // TODO: check total, if expenses are included
        model.addAttribute("total", order.getTotal());

        // TODO: ask for these vars asynchronous ?
        model.addAttribute("user", user);
        model.addAttribute("preRegistrationData", cardRegistration.getPreregistrationData());
        model.addAttribute("accessKey", cardRegistration.getAccessKey());
        model.addAttribute("cardRegistrationUrl", cardRegistration.getCardRegistrationUrl());
        model.addAttribute("cardId", cardRegistration.getId());
        model.addAttribute("cardType", cardRegistration.getCardType().name());
        model.addAttribute("mongoApiBaseUrl", api.getConfig().getBaseUrl());
        model.addAttribute("mongoApiClientId", api.getConfig().getClientId());

        session.setAttribute(CARD_REGISTRATION_ID_ATTR, cardRegistration.getId());

        Mappings.includeMappings(model);
        return Templates.CHECKOUT_FORM;
    }

    @RequestMapping(value = Mappings.CHECKOUT, method = RequestMethod.POST)
    public String registerCard(
            @RequestParam("data") String data,
            HttpSession session) throws Exception {

        configureBankApi();

        // update card registration
        String id = (String) session.getAttribute(CARD_REGISTRATION_ID_ATTR);
        CardRegistration cardRegistration = api.getCardRegistrationApi().get(id);
        cardRegistration.setRegistrationData(data);
        api.getCardRegistrationApi().update(cardRegistration);

        // create temporary wallet for user
        UserNatural user = api.getUserApi().getNatural(cardRegistration.getUserId());
        Wallet wallet = createWallet(user);

        // create payin
        PayIn payIn = new PayIn();
        payIn.setAuthorId(user.getId());
        // payIn.setCreditedUserId(owner.getId());

        payIn.setDebitedFunds(new Money());
        payIn.getDebitedFunds().setCurrency(CurrencyIso.EUR);
        payIn.getDebitedFunds().setAmount(123);
        //payIn.setCreditedWalletId(wallet.getId());

        // set card details
        PayInPaymentDetailsCard paymentDetail = new PayInPaymentDetailsCard();
        paymentDetail.setCardId(cardRegistration.getCardId());
        paymentDetail.setCardType(cardRegistration.getCardType());
        payIn.setPaymentDetails(paymentDetail);

        PayInExecutionDetailsDirect executionDetail = new PayInExecutionDetailsDirect();
        executionDetail.setSecureModeRedirectUrl("https://localhost:8085");
        payIn.setExecutionDetails(executionDetail);

        // example credit card: 4970101122334414  cvv 123 exp future
        payIn = api.getPayInApi().create(payIn);

        return "redirect:" + Mappings.CHECKOUT_END;
    }

    public void configureBankApi() {
        logger.info("bankapiId: " + bankapiId);
        logger.info("bankapiPwd: " + bankapiPwd);

        api.getConfig().setClientId(bankapiId);
        api.getConfig().setClientPassword(bankapiPwd);

        //api.getConfig().setBaseUrl("https://api.mangopay.com"); // production only
        api.getConfig().setBaseUrl("https://api.sandbox.mangopay.com"); // production only
    }

    private UserNatural createUser(CommercialOrder order) throws Exception {

        UserNatural user = new UserNatural();

        // TODO: ask for birthdate, which is mandatory
        Calendar c = Calendar.getInstance();
        c.set(1975, 12, 21, 0, 0, 0);

        // user ID is mandatory, even if it is not used (it will change after creation)
        user.setId(UUID.randomUUID().toString());

        user.setFirstName(order.getFirstName());
        user.setLastName(order.getLastName());
        user.setEmail(order.getEmail());

        Address address = new Address();
        address.setAddressLine1(order.getAddress());
        address.setCity(order.getCity());
        address.setCountry(CountryIso.FR);
        address.setPostalCode(order.getPostalcode());

        user.setAddress(address);
        user.setBirthday(c.getTimeInMillis() / 1000);
        user.setNationality(CountryIso.FR);
        user.setCountryOfResidence(CountryIso.FR);

        user = (UserNatural) api.getUserApi().create(user);

        return user;
    }

    private Wallet createWallet(UserNatural user) throws Exception {

        Wallet wallet = new Wallet();
        wallet.setOwners(new ArrayList<>());
        wallet.getOwners().add(user.getId());
        wallet.setCurrency(CurrencyIso.EUR);
        wallet.setDescription("WALLET IN EUR");

        api.getWalletApi().create(wallet);

        return wallet;
    }

    private CardRegistration createCardRegistration(User user) throws Exception {

        CardRegistration cardRegistration = new CardRegistration();
        cardRegistration.setUserId(user.getId());
        cardRegistration.setCurrency(CurrencyIso.EUR);
        cardRegistration.setCardType(CardType.CB_VISA_MASTERCARD);

        cardRegistration = api.getCardRegistrationApi().create(cardRegistration);

        return cardRegistration;
    }


}
