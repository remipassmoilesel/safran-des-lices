var App = {

    init: function () {

        $("#checkoutForm button").click(function () {
            App.checkFormAndSubmit()
        });

    },

    checkFormAndSubmit: function () {

        var form = $("#checkoutForm");
        var errorArea = $(".errorArea");
        var firstname = form.find("input[name='firstname']");
        var lastname = form.find("input[name='lastname']");
        var postalcode = form.find("input[name='postalcode']");
        var city = form.find("input[name='city']");
        var address = form.find("input[name='address']");
        var phonenumber = form.find("input[name='phonenumber']");
        var paymentType = form.find("input[name='paymentType']");

        var showErrorMessage = function(message){
          errorArea.css("opacity", "0");
          errorArea.html(message);
          errorArea.animate({"opacity": 1}, 700);
        };

        if(firstname.val().trim().length < 5){
            showErrorMessage("Prénom invalide, 5 caractères minimum");
            return;
        }

        if(lastname.val().trim().length < 5){
            showErrorMessage("Nom invalide, 5 caractères minimum");
            return;
        }

        if(!postalcode.val().trim().match(/^[0-9]+$/)){
            showErrorMessage("Code postal invalide");
            return;
        }

        if(city.val().trim().length < 3){
            showErrorMessage("Ville invalide, 3 caractères minimum");
            return;
        }

        if(address.val().trim().length < 10){
            showErrorMessage("Adresse invalide, 5 caractères minimum");
            return;
        }

        if(!phonenumber.val().trim().match(/^\+?[0-9]{4,}$/)){
            showErrorMessage("Numéro de téléphone invalide");
            return;
        }

        form.submit();

    }

};

$(function () {
    App.init();
});