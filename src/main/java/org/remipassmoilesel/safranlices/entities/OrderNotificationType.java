package org.remipassmoilesel.safranlices.entities;

/**
 * Created by remipassmoilesel on 22/07/17.
 */
public enum OrderNotificationType {

    CONFIRMED("Commande confirmée", "orderConfirmed"),
    CANCELED("Commande annulée", "orderCanceled"),
    SENT("Commande envoyée", "orderSent");

    private String mailSubject;
    private String mailTemplate;

    OrderNotificationType(String mailSubject, String mailTemplate) {
        this.mailSubject = mailSubject;
        this.mailTemplate = mailTemplate;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailTemplate() {
        return mailTemplate;
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    @Override
    public String toString() {
        return "OrderStep{" +
                "mailSubject='" + mailSubject + '\'' +
                ", mailTemplate='" + mailTemplate + '\'' +
                '}';
    }

}
