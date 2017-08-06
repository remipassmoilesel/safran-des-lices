var App = {

    init: function () {

        $("#billingForm .validButton").click(function () {
            App.checkFormAndSubmit()
        });

    },

    checkFormAndSubmit: function () {

        var form = $("#billingForm");
        var errorArea = $(".errorArea");
        var firstname = form.find("input[name='firstname']");
        var lastname = form.find("input[name='lastname']");
        var postalcode = form.find("input[name='postalcode']");
        var city = form.find("input[name='city']");
        var address = form.find("input[name='address']");
        var shipmentPostalcode = form.find("input[name='shipmentPostalcode']");
        var shipmentcity = form.find("input[name='shipmentCity']");
        var shipmentaddress = form.find("input[name='shipmentAddress']");
        var phonenumber = form.find("input[name='phonenumber']");
        var paymentType = form.find("input[name='paymentType']");
        var email = form.find("input[name='email']");

        var showErrorMessage = function(message){
          errorArea.css("opacity", "0");
          errorArea.html(message);
          errorArea.animate({"opacity": 1}, 700);
        };

        if(firstname.val().trim().length < 3){
            showErrorMessage("Prénom invalide, 3 caractères minimum");
            return;
        }

        if(lastname.val().trim().length < 3){
            showErrorMessage("Nom invalide, 3 caractères minimum");
            return;
        }

        if(!postalcode.val().trim().match(/^[0-9]+$/)){
            showErrorMessage("Code postal invalide");
            return;
        }

        if(city.val().trim().length < 2){
            showErrorMessage("Ville invalide, 2 caractères minimum");
            return;
        }

        if(address.val().trim().length < 10){
            showErrorMessage("Adresse invalide, 5 caractères minimum");
            return;
        }

        if(shipmentPostalcode.val().trim().length > 0 && !shipmentPostalcode.val().trim().match(/^[0-9]+$/)){
            showErrorMessage("Code postal d'adresse de livraison invalide");
            return;
        }

        if(shipmentcity.val().trim().length > 0 && shipmentcity.val().trim().length < 2){
            showErrorMessage("Ville de livraison invalide, 2 caractères minimum");
            return;
        }

        if(shipmentaddress.val().trim().length > 0 && shipmentaddress.val().trim().length < 10){
            showErrorMessage("Adresse de livraison invalide, 5 caractères minimum");
            return;
        }

        if(!phonenumber.val().trim().match(/^\+?[0-9]{4,}$/)){
            showErrorMessage("Numéro de téléphone invalide");
            return;
        }
        if(!email.val().trim().match(/^[A-Za-z0-9._+-]+@[A-Za-z0-9.-]+\.[a-zA-Z]{2,6}$/)){
            showErrorMessage("Adresse email invalide");
            return;
        }

        form.submit();

    }

};

$(function () {
    App.init();
});